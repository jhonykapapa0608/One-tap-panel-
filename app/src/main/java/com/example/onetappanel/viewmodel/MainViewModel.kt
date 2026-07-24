package com.example.onetappanel.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.onetappanel.data.database.AppDatabase
import com.example.onetappanel.data.model.*
import com.example.onetappanel.data.repository.SmmRepository
import com.example.onetappanel.util.BackupUtils
import com.example.onetappanel.util.FullAppBackup
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    val repository = SmmRepository(db)

    // Current Session State
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    private val _selectedCurrency = MutableStateFlow("INR")
    val selectedCurrency: StateFlow<String> = _selectedCurrency.asStateFlow()

    // UI Toast / Message Event State
    private val _uiEvent = MutableSharedFlow<String>()
    val uiEvent: SharedFlow<String> = _uiEvent.asSharedFlow()

    // Screen State / Route
    private val _currentRoute = MutableStateFlow("login")
    val currentRoute: StateFlow<String> = _currentRoute.asStateFlow()

    // User Streams
    val categories: StateFlow<List<ServiceCategoryEntity>> = repository.categoryDao.getActiveCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeServices: StateFlow<List<ServiceEntity>> = repository.serviceDao.getActiveServices()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val popularServices: StateFlow<List<ServiceEntity>> = repository.serviceDao.getPopularServices()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val featuredServices: StateFlow<List<ServiceEntity>> = repository.serviceDao.getFeaturedServices()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val paymentSettings: StateFlow<PaymentSettingsEntity?> = repository.paymentDao.getSettingsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val websiteSettings: StateFlow<WebsiteSettingsEntity?> = repository.settingsDao.getSettingsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // User Specific Reactive Flows
    val myOrders: StateFlow<List<OrderEntity>> = currentUser.flatMapLatest { user ->
        if (user != null) repository.orderDao.getOrdersForUser(user.id)
        else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val myRecharges: StateFlow<List<RechargeRequestEntity>> = currentUser.flatMapLatest { user ->
        if (user != null) repository.rechargeDao.getRechargesForUser(user.id)
        else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val myWalletTransactions: StateFlow<List<WalletTransactionEntity>> = currentUser.flatMapLatest { user ->
        if (user != null) repository.walletDao.getTransactionsForUser(user.id)
        else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val myTickets: StateFlow<List<SupportTicketEntity>> = currentUser.flatMapLatest { user ->
        if (user != null) repository.ticketDao.getTicketsForUser(user.id)
        else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val myNotifications: StateFlow<List<NotificationEntity>> = currentUser.flatMapLatest { user ->
        if (user != null) repository.notificationDao.getNotificationsForUser(user.id)
        else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favouriteServiceIds: StateFlow<List<Long>> = currentUser.flatMapLatest { user ->
        if (user != null) repository.favDao.getFavouriteServiceIds(user.id)
        else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Live updated User Balance observer
    init {
        viewModelScope.launch {
            currentUser.collectLatest { user ->
                if (user != null) {
                    repository.userDao.getUserFlowById(user.id).collect { updated ->
                        if (updated != null) {
                            _currentUser.value = updated
                        }
                    }
                }
            }
        }
    }

    // Admin Streams
    val allUsers: StateFlow<List<UserEntity>> = repository.userDao.getAllUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allOrders: StateFlow<List<OrderEntity>> = repository.orderDao.getAllOrders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allRecharges: StateFlow<List<RechargeRequestEntity>> = repository.rechargeDao.getAllRecharges()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allServices: StateFlow<List<ServiceEntity>> = repository.serviceDao.getAllServices()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCategories: StateFlow<List<ServiceCategoryEntity>> = repository.categoryDao.getAllCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCoupons: StateFlow<List<CouponEntity>> = repository.couponDao.getAllCoupons()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTickets: StateFlow<List<SupportTicketEntity>> = repository.ticketDao.getAllTickets()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allNotifications: StateFlow<List<NotificationEntity>> = repository.notificationDao.getAllNotifications()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activityLogs: StateFlow<List<ActivityLogEntity>> = repository.logDao.getRecentLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setRoute(route: String) {
        _currentRoute.value = route
    }

    fun setCurrency(code: String) {
        _selectedCurrency.value = code
    }

    fun emitToast(message: String) {
        viewModelScope.launch {
            _uiEvent.emit(message)
        }
    }

    // Auth Actions
    fun login(emailOrUsername: String, passwordHash: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val res = repository.loginUser(emailOrUsername, passwordHash)
            res.fold(
                onSuccess = { user ->
                    _currentUser.value = user
                    if (user.role == "ADMIN") {
                        _currentRoute.value = "admin_dashboard"
                    } else {
                        _currentRoute.value = "user_dashboard"
                    }
                    onResult(true, "Welcome back, ${user.fullName}!")
                },
                onFailure = { err ->
                    onResult(false, err.message ?: "Login failed")
                }
            )
        }
    }

    fun register(
        fullName: String,
        username: String,
        email: String,
        phone: String,
        passwordHash: String,
        refCode: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val res = repository.registerUser(fullName, username, email, phone, passwordHash, refCode)
            res.fold(
                onSuccess = { user ->
                    _currentUser.value = user
                    _currentRoute.value = "user_dashboard"
                    onResult(true, "Registration successful! Welcome ${user.fullName}.")
                },
                onFailure = { err ->
                    onResult(false, err.message ?: "Registration failed")
                }
            )
        }
    }

    fun logout() {
        _currentUser.value = null
        _currentRoute.value = "login"
        emitToast("Logged out successfully.")
    }

    fun updateProfile(fullName: String, phone: String, photo: String, onResult: (Boolean, String) -> Unit) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val updated = user.copy(fullName = fullName, phoneNumber = phone, profilePhoto = photo)
            repository.updateUserProfile(updated)
            _currentUser.value = updated
            onResult(true, "Profile updated successfully")
        }
    }

    fun changePassword(oldPass: String, newPass: String, onResult: (Boolean, String) -> Unit) {
        val user = _currentUser.value ?: return
        if (user.passwordHash != oldPass) {
            onResult(false, "Current password does not match")
            return
        }
        viewModelScope.launch {
            val updated = user.copy(passwordHash = newPass)
            repository.updateUserProfile(updated)
            _currentUser.value = updated
            onResult(true, "Password changed successfully!")
        }
    }

    // Place Order Action
    fun placeOrder(
        service: ServiceEntity,
        link: String,
        quantity: Int,
        couponCode: String,
        onResult: (Boolean, OrderEntity?, String) -> Unit
    ) {
        val user = _currentUser.value
        if (user == null) {
            onResult(false, null, "User session expired. Please log in.")
            return
        }

        viewModelScope.launch {
            val res = repository.placeOrder(user, service, link, quantity, couponCode, _selectedCurrency.value)
            res.fold(
                onSuccess = { order ->
                    onResult(true, order, "Order #${order.orderIdString} placed successfully!")
                },
                onFailure = { err ->
                    onResult(false, null, err.message ?: "Order failed")
                }
            )
        }
    }

    // Recharge Action
    fun submitRecharge(
        amount: Double,
        uTrNumber: String,
        screenshotPath: String,
        onResult: (Boolean, String) -> Unit
    ) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val res = repository.submitRechargeRequest(user, amount, _selectedCurrency.value, uTrNumber, screenshotPath)
            res.fold(
                onSuccess = {
                    onResult(true, "Recharge request submitted successfully! Pending approval.")
                },
                onFailure = { err ->
                    onResult(false, err.message ?: "Recharge failed")
                }
            )
        }
    }

    // Toggle Favourite
    fun toggleFavourite(serviceId: Long) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            if (favouriteServiceIds.value.contains(serviceId)) {
                repository.favDao.removeFavourite(user.id, serviceId)
            } else {
                repository.favDao.addFavourite(FavouriteServiceEntity(user.id, serviceId))
            }
        }
    }

    // Support Tickets
    fun createSupportTicket(subject: String, category: String, priority: String, message: String, onResult: (Boolean, String) -> Unit) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val res = repository.createSupportTicket(user, subject, category, priority, message)
            res.fold(
                onSuccess = {
                    onResult(true, "Support ticket created successfully.")
                },
                onFailure = { err ->
                    onResult(false, err.message ?: "Failed to create ticket")
                }
            )
        }
    }

    fun replyTicket(ticketId: Long, message: String, onResult: (Boolean, String) -> Unit) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val res = repository.replyToTicket(ticketId, user.role, user.fullName, message)
            res.fold(
                onSuccess = { onResult(true, "Reply sent") },
                onFailure = { err -> onResult(false, err.message ?: "Failed to reply") }
            )
        }
    }

    // Admin Actions
    fun adminApproveRecharge(rechargeId: Long, note: String) {
        viewModelScope.launch {
            val res = repository.approveRecharge(rechargeId, note)
            res.fold(
                onSuccess = { emitToast("Recharge Approved") },
                onFailure = { err -> emitToast(err.message ?: "Approval failed") }
            )
        }
    }

    fun adminRejectRecharge(rechargeId: Long, note: String) {
        viewModelScope.launch {
            val res = repository.rejectRecharge(rechargeId, note)
            res.fold(
                onSuccess = { emitToast("Recharge Rejected") },
                onFailure = { err -> emitToast(err.message ?: "Rejection failed") }
            )
        }
    }

    fun adminUpdateOrderStatus(orderId: Long, status: String) {
        viewModelScope.launch {
            if (status == "Cancelled") {
                repository.cancelOrder(orderId, "Cancelled by Admin")
            } else {
                repository.orderDao.updateOrderStatus(orderId, status)
                emitToast("Order status updated to $status")
            }
        }
    }

    fun adminCreditUserBalance(userId: Long, amount: Double, note: String) {
        viewModelScope.launch {
            repository.userDao.creditWallet(userId, amount)
            repository.walletDao.insertTransaction(
                WalletTransactionEntity(
                    userId = userId,
                    type = "CREDIT",
                    amount = amount,
                    currency = _selectedCurrency.value,
                    description = "Admin Manual Adjustment: $note"
                )
            )
            emitToast("Credited $amount to user wallet")
        }
    }

    fun adminToggleUserBlock(user: UserEntity) {
        viewModelScope.launch {
            val updated = user.copy(isBlocked = !user.isBlocked)
            repository.userDao.updateUser(updated)
            emitToast(if (updated.isBlocked) "User account suspended" else "User account unblocked")
        }
    }

    fun adminSaveService(service: ServiceEntity) {
        viewModelScope.launch {
            repository.serviceDao.insertService(service)
            emitToast("Service saved successfully")
        }
    }

    fun adminDeleteService(serviceId: Long) {
        viewModelScope.launch {
            repository.serviceDao.deleteService(serviceId)
            emitToast("Service deleted")
        }
    }

    fun adminSaveCategory(category: ServiceCategoryEntity) {
        viewModelScope.launch {
            repository.categoryDao.insertCategory(category)
            emitToast("Category saved successfully")
        }
    }

    fun adminSavePaymentSettings(settings: PaymentSettingsEntity) {
        viewModelScope.launch {
            repository.paymentDao.insertOrUpdateSettings(settings)
            emitToast("Payment settings updated successfully")
        }
    }

    fun adminSaveWebsiteSettings(settings: WebsiteSettingsEntity) {
        viewModelScope.launch {
            repository.settingsDao.updateSettings(settings)
            emitToast("Website settings updated successfully")
        }
    }

    fun adminCreateCoupon(coupon: CouponEntity) {
        viewModelScope.launch {
            repository.couponDao.insertCoupon(coupon)
            emitToast("Coupon created successfully")
        }
    }

    fun adminDeleteCoupon(couponId: Long) {
        viewModelScope.launch {
            repository.couponDao.deleteCoupon(couponId)
            emitToast("Coupon deleted")
        }
    }

    fun adminBroadcastNotification(title: String, message: String, type: String) {
        viewModelScope.launch {
            repository.notificationDao.insertNotification(
                NotificationEntity(
                    userId = 0,
                    title = title,
                    message = message,
                    type = type
                )
            )
            emitToast("Broadcast notification sent!")
        }
    }

    // Export Backup
    fun exportBackupJson(onResult: (String) -> Unit) {
        viewModelScope.launch {
            val backup = FullAppBackup(
                users = allUsers.value,
                orders = allOrders.value,
                categories = allCategories.value,
                services = allServices.value,
                recharges = allRecharges.value,
                coupons = allCoupons.value,
                tickets = allTickets.value,
                notifications = allNotifications.value
            )
            val json = BackupUtils.exportToJson(backup)
            onResult(json)
        }
    }
}
