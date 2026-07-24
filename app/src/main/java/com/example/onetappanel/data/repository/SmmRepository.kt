package com.example.onetappanel.data.repository

import com.example.onetappanel.data.database.AppDatabase
import com.example.onetappanel.data.model.*
import com.example.onetappanel.util.CurrencyUtils
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class SmmRepository(private val db: AppDatabase) {

    val userDao = db.userDao()
    val walletDao = db.walletTransactionDao()
    val orderDao = db.orderDao()
    val categoryDao = db.serviceCategoryDao()
    val serviceDao = db.serviceDao()
    val rechargeDao = db.rechargeDao()
    val paymentDao = db.paymentSettingsDao()
    val couponDao = db.couponDao()
    val ticketDao = db.supportTicketDao()
    val notificationDao = db.notificationDao()
    val settingsDao = db.websiteSettingsDao()
    val logDao = db.activityLogDao()
    val favDao = db.favouriteServiceDao()

    // Authentication & User Profile
    suspend fun registerUser(
        fullName: String,
        username: String,
        email: String,
        phoneNumber: String,
        passwordHash: String,
        referralCode: String = ""
    ): Result<UserEntity> {
        if (userDao.getUserByEmail(email) != null) {
            return Result.failure(Exception("Email is already registered"))
        }
        if (userDao.getUserByUsername(username) != null) {
            return Result.failure(Exception("Username is already taken"))
        }

        val myReferralCode = "OTP" + UUID.randomUUID().toString().take(6).uppercase()
        val joinDate = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

        val newUser = UserEntity(
            fullName = fullName,
            username = username,
            email = email,
            phoneNumber = phoneNumber,
            passwordHash = passwordHash,
            walletBalance = 0.0,
            joinDate = joinDate,
            emailVerified = true,
            referralCode = myReferralCode,
            referredBy = referralCode
        )

        val id = userDao.insertUser(newUser)
        val createdUser = newUser.copy(id = id)

        // Log Activity
        logDao.insertLog(
            ActivityLogEntity(
                actorRole = "USER",
                actorName = fullName,
                actionType = "USER_REGISTER",
                details = "Registered account with username @$username"
            )
        )

        return Result.success(createdUser)
    }

    suspend fun loginUser(emailOrUsername: String, passwordHash: String): Result<UserEntity> {
        val user = if (emailOrUsername.contains("@")) {
            userDao.getUserByEmail(emailOrUsername)
        } else {
            userDao.getUserByUsername(emailOrUsername)
        }

        if (user == null) {
            return Result.failure(Exception("Account not found"))
        }
        if (user.passwordHash != passwordHash) {
            return Result.failure(Exception("Invalid password"))
        }
        if (user.isBlocked) {
            return Result.failure(Exception("Your account is suspended. Please contact support."))
        }

        logDao.insertLog(
            ActivityLogEntity(
                actorRole = user.role,
                actorName = user.fullName,
                actionType = "USER_LOGIN",
                details = "Logged in successfully"
            )
        )

        return Result.success(user)
    }

    suspend fun updateUserProfile(user: UserEntity): Result<Unit> {
        userDao.updateUser(user)
        logDao.insertLog(
            ActivityLogEntity(
                actorRole = user.role,
                actorName = user.fullName,
                actionType = "PROFILE_UPDATE",
                details = "Updated profile information"
            )
        )
        return Result.success(Unit)
    }

    // Order Placement
    suspend fun placeOrder(
        user: UserEntity,
        service: ServiceEntity,
        link: String,
        quantity: Int,
        couponCode: String = "",
        currency: String = "INR"
    ): Result<OrderEntity> {
        if (quantity < service.minQuantity || quantity > service.maxQuantity) {
            return Result.failure(Exception("Quantity must be between ${service.minQuantity} and ${service.maxQuantity}"))
        }

        val basePriceINR = (quantity / 1000.0) * service.ratePer1000
        var finalPriceINR = basePriceINR

        // Apply coupon if given
        if (couponCode.isNotBlank()) {
            val coupon = couponDao.getCouponByCode(couponCode.trim().uppercase())
            if (coupon != null && coupon.isActive) {
                if (basePriceINR >= coupon.minOrderAmount) {
                    val discount = if (coupon.discountPercent > 0) {
                        (basePriceINR * (coupon.discountPercent / 100.0)).coerceAtMost(coupon.maxDiscount)
                    } else {
                        coupon.discountAmount
                    }
                    finalPriceINR = (basePriceINR - discount).coerceAtLeast(0.0)
                    couponDao.incrementCouponUsage(coupon.id)
                }
            }
        }

        // Check wallet balance
        if (user.walletBalance < finalPriceINR) {
            val requiredDisplay = CurrencyUtils.format(finalPriceINR, currency)
            val currentDisplay = CurrencyUtils.format(user.walletBalance, currency)
            return Result.failure(Exception("Insufficient wallet balance! Required: $requiredDisplay, Current: $currentDisplay"))
        }

        // Debit Wallet
        val rows = userDao.debitWallet(user.id, finalPriceINR)
        if (rows == 0) {
            return Result.failure(Exception("Transaction failed. Insufficient funds or concurrent order."))
        }

        val now = Date()
        val orderIdStr = "OTP-" + (100000..999999).random()
        val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(now)
        val timeStr = SimpleDateFormat("HH:mm:ss", Locale.US).format(now)

        val newOrder = OrderEntity(
            orderIdString = orderIdStr,
            userId = user.id,
            userFullName = user.fullName,
            username = user.username,
            userEmail = user.email,
            userPhone = user.phoneNumber,
            serviceId = service.id,
            serviceName = service.name,
            categoryName = service.categoryName,
            link = link,
            quantity = quantity,
            price = finalPriceINR,
            currency = currency,
            date = dateStr,
            time = timeStr,
            status = "Pending"
        )

        val orderId = orderDao.insertOrder(newOrder)
        val createdOrder = newOrder.copy(id = orderId)

        // Record Debit Transaction
        walletDao.insertTransaction(
            WalletTransactionEntity(
                userId = user.id,
                type = "DEBIT",
                amount = finalPriceINR,
                currency = currency,
                description = "Order #${orderIdStr} - ${service.name}",
                orderId = orderIdStr
            )
        )

        // Send Notification
        notificationDao.insertNotification(
            NotificationEntity(
                userId = user.id,
                title = "Order Placed Successfully",
                message = "Order #${orderIdStr} for ${service.name} has been received and is Pending processing.",
                type = "ORDER"
            )
        )

        // Activity Log
        logDao.insertLog(
            ActivityLogEntity(
                actorRole = user.role,
                actorName = user.fullName,
                actionType = "ORDER_PLACED",
                details = "Placed order #${orderIdStr} for quantity $quantity"
            )
        )

        return Result.success(createdOrder)
    }

    // Recharge System
    suspend fun submitRechargeRequest(
        user: UserEntity,
        amount: Double,
        currency: String,
        uTrNumber: String,
        screenshotPath: String = ""
    ): Result<RechargeRequestEntity> {
        if (uTrNumber.isBlank()) {
            return Result.failure(Exception("Please enter a valid UTR / Transaction Reference Number"))
        }

        val rechargeIdStr = "RCH-" + (10000..99999).random()
        val createdAt = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(Date())

        val amountINR = CurrencyUtils.convertToINR(amount, currency)

        val request = RechargeRequestEntity(
            rechargeIdString = rechargeIdStr,
            userId = user.id,
            userFullName = user.fullName,
            username = user.username,
            email = user.email,
            phone = user.phoneNumber,
            amount = amountINR,
            currency = currency,
            uTrNumber = uTrNumber,
            screenshotPath = screenshotPath,
            status = "Pending",
            createdAt = createdAt
        )

        val id = rechargeDao.insertRecharge(request)

        notificationDao.insertNotification(
            NotificationEntity(
                userId = user.id,
                title = "Recharge Request Submitted",
                message = "Your recharge of ${CurrencyUtils.format(amountINR, currency)} (UTR: $uTrNumber) is pending admin approval.",
                type = "RECHARGE"
            )
        )

        logDao.insertLog(
            ActivityLogEntity(
                actorRole = user.role,
                actorName = user.fullName,
                actionType = "RECHARGE_REQUEST",
                details = "Submitted recharge request of $amount ($currency), UTR: $uTrNumber"
            )
        )

        return Result.success(request.copy(id = id))
    }

    // Admin Recharge Approval
    suspend fun approveRecharge(rechargeId: Long, adminNote: String = "Approved by Admin"): Result<Unit> {
        val req = rechargeDao.getRechargeById(rechargeId) ?: return Result.failure(Exception("Request not found"))
        if (req.status != "Pending") {
            return Result.failure(Exception("Recharge request is already processed"))
        }

        // Update recharge state
        rechargeDao.updateRechargeStatus(rechargeId, "Approved", adminNote)

        // Credit Wallet
        userDao.creditWallet(req.userId, req.amount)

        // Record Credit Transaction
        walletDao.insertTransaction(
            WalletTransactionEntity(
                userId = req.userId,
                type = "CREDIT",
                amount = req.amount,
                currency = req.currency,
                description = "Recharge Approved (UTR: ${req.uTrNumber})",
                uTrNumber = req.uTrNumber
            )
        )

        // Notify User
        notificationDao.insertNotification(
            NotificationEntity(
                userId = req.userId,
                title = "Wallet Recharged! 💰",
                message = "Your recharge of ${CurrencyUtils.format(req.amount, req.currency)} has been approved and added to your wallet.",
                type = "WALLET"
            )
        )

        logDao.insertLog(
            ActivityLogEntity(
                actorRole = "ADMIN",
                actorName = "Admin",
                actionType = "RECHARGE_APPROVE",
                details = "Approved recharge #${req.rechargeIdString} for ${req.username}"
            )
        )

        return Result.success(Unit)
    }

    suspend fun rejectRecharge(rechargeId: Long, adminNote: String = "Rejected by Admin"): Result<Unit> {
        val req = rechargeDao.getRechargeById(rechargeId) ?: return Result.failure(Exception("Request not found"))
        if (req.status != "Pending") {
            return Result.failure(Exception("Recharge request is already processed"))
        }

        rechargeDao.updateRechargeStatus(rechargeId, "Rejected", adminNote)

        notificationDao.insertNotification(
            NotificationEntity(
                userId = req.userId,
                title = "Recharge Request Declined",
                message = "Your recharge request #${req.rechargeIdString} was rejected. Reason: $adminNote",
                type = "RECHARGE"
            )
        )

        logDao.insertLog(
            ActivityLogEntity(
                actorRole = "ADMIN",
                actorName = "Admin",
                actionType = "RECHARGE_REJECT",
                details = "Rejected recharge #${req.rechargeIdString}"
            )
        )

        return Result.success(Unit)
    }

    // Cancel Order & Auto Refund
    suspend fun cancelOrder(orderId: Long, reason: String = "Cancelled by Admin"): Result<Unit> {
        val order = orderDao.getOrderById(orderId) ?: return Result.failure(Exception("Order not found"))
        if (order.status == "Cancelled") {
            return Result.failure(Exception("Order is already cancelled"))
        }

        // Refund wallet
        userDao.creditWallet(order.userId, order.price)

        // Record credit transaction
        walletDao.insertTransaction(
            WalletTransactionEntity(
                userId = order.userId,
                type = "CREDIT",
                amount = order.price,
                currency = order.currency,
                description = "Auto-Refund for Cancelled Order #${order.orderIdString}",
                orderId = order.orderIdString
            )
        )

        // Update status
        orderDao.updateOrderStatus(orderId, "Cancelled")

        // Notify user
        notificationDao.insertNotification(
            NotificationEntity(
                userId = order.userId,
                title = "Order Cancelled & Refunded 🔄",
                message = "Order #${order.orderIdString} was cancelled. Full refund of ${CurrencyUtils.format(order.price, order.currency)} added back to wallet.",
                type = "ORDER"
            )
        )

        logDao.insertLog(
            ActivityLogEntity(
                actorRole = "ADMIN",
                actorName = "Admin",
                actionType = "ORDER_CANCEL",
                details = "Cancelled order #${order.orderIdString} and refunded amount"
            )
        )

        return Result.success(Unit)
    }

    // Support Ticket System
    suspend fun createSupportTicket(
        user: UserEntity,
        subject: String,
        category: String,
        priority: String,
        initialMessage: String
    ): Result<SupportTicketEntity> {
        val ticketIdStr = "TCK-" + (10000..99999).random()
        val nowStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(Date())

        val ticket = SupportTicketEntity(
            ticketIdString = ticketIdStr,
            userId = user.id,
            userFullName = user.fullName,
            subject = subject,
            category = category,
            priority = priority,
            status = "Open",
            createdAt = nowStr,
            updatedAt = nowStr,
            lastMessage = initialMessage
        )

        val ticketId = ticketDao.insertTicket(ticket)

        ticketDao.insertMessage(
            SupportMessageEntity(
                ticketId = ticketId,
                senderRole = "USER",
                senderName = user.fullName,
                message = initialMessage,
                timestamp = System.currentTimeMillis()
            )
        )

        return Result.success(ticket.copy(id = ticketId))
    }

    suspend fun replyToTicket(ticketId: Long, senderRole: String, senderName: String, message: String): Result<Unit> {
        val ticket = ticketDao.getTicketById(ticketId) ?: return Result.failure(Exception("Ticket not found"))

        val nowStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(Date())
        val newStatus = if (senderRole == "ADMIN") "Replied" else "Open"

        ticketDao.insertMessage(
            SupportMessageEntity(
                ticketId = ticketId,
                senderRole = senderRole,
                senderName = senderName,
                message = message,
                timestamp = System.currentTimeMillis()
            )
        )

        ticketDao.updateTicketStatus(ticketId, newStatus, nowStr, message)

        return Result.success(Unit)
    }
}
