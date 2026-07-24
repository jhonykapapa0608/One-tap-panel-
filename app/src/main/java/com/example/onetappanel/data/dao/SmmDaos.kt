package com.example.onetappanel.data.dao

import androidx.room.*
import com.example.onetappanel.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Long): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun getUserFlowById(id: Long): Flow<UserEntity?>

    @Query("SELECT * FROM users ORDER BY id DESC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET walletBalance = walletBalance + :amount WHERE id = :userId")
    suspend fun creditWallet(userId: Long, amount: Double)

    @Query("UPDATE users SET walletBalance = walletBalance - :amount WHERE id = :userId AND walletBalance >= :amount")
    suspend fun debitWallet(userId: Long, amount: Double): Int
}

@Dao
interface WalletTransactionDao {
    @Query("SELECT * FROM wallet_transactions WHERE userId = :userId ORDER BY timestamp DESC")
    fun getTransactionsForUser(userId: Long): Flow<List<WalletTransactionEntity>>

    @Query("SELECT * FROM wallet_transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<WalletTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(tx: WalletTransactionEntity): Long
}

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders WHERE userId = :userId ORDER BY timestamp DESC")
    fun getOrdersForUser(userId: Long): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders ORDER BY timestamp DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE id = :orderId LIMIT 1")
    suspend fun getOrderById(orderId: Long): OrderEntity?

    @Query("SELECT COUNT(*) FROM orders")
    suspend fun getOrdersCount(): Int

    @Query("SELECT COUNT(*) FROM orders WHERE status = :status")
    suspend fun getOrdersCountByStatus(status: String): Int

    @Query("SELECT SUM(price) FROM orders WHERE status = 'Completed'")
    suspend fun getTotalCompletedRevenue(): Double?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity): Long

    @Query("UPDATE orders SET status = :status WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: Long, status: String)
}

@Dao
interface ServiceCategoryDao {
    @Query("SELECT * FROM service_categories WHERE isActive = 1 ORDER BY displayOrder ASC")
    fun getActiveCategories(): Flow<List<ServiceCategoryEntity>>

    @Query("SELECT * FROM service_categories ORDER BY displayOrder ASC")
    fun getAllCategories(): Flow<List<ServiceCategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: ServiceCategoryEntity): Long

    @Update
    suspend fun updateCategory(category: ServiceCategoryEntity)

    @Query("DELETE FROM service_categories WHERE id = :categoryId")
    suspend fun deleteCategory(categoryId: Long)
}

@Dao
interface ServiceDao {
    @Query("SELECT * FROM services WHERE isActive = 1 ORDER BY id DESC")
    fun getActiveServices(): Flow<List<ServiceEntity>>

    @Query("SELECT * FROM services WHERE categoryId = :categoryId AND isActive = 1 ORDER BY id DESC")
    fun getServicesByCategory(categoryId: Long): Flow<List<ServiceEntity>>

    @Query("SELECT * FROM services ORDER BY id DESC")
    fun getAllServices(): Flow<List<ServiceEntity>>

    @Query("SELECT * FROM services WHERE isPopular = 1 AND isActive = 1")
    fun getPopularServices(): Flow<List<ServiceEntity>>

    @Query("SELECT * FROM services WHERE isFeatured = 1 AND isActive = 1")
    fun getFeaturedServices(): Flow<List<ServiceEntity>>

    @Query("SELECT * FROM services WHERE id = :serviceId LIMIT 1")
    suspend fun getServiceById(serviceId: Long): ServiceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertService(service: ServiceEntity): Long

    @Update
    suspend fun updateService(service: ServiceEntity)

    @Query("DELETE FROM services WHERE id = :serviceId")
    suspend fun deleteService(serviceId: Long)
}

@Dao
interface RechargeDao {
    @Query("SELECT * FROM recharge_requests WHERE userId = :userId ORDER BY timestamp DESC")
    fun getRechargesForUser(userId: Long): Flow<List<RechargeRequestEntity>>

    @Query("SELECT * FROM recharge_requests ORDER BY timestamp DESC")
    fun getAllRecharges(): Flow<List<RechargeRequestEntity>>

    @Query("SELECT * FROM recharge_requests WHERE id = :id LIMIT 1")
    suspend fun getRechargeById(id: Long): RechargeRequestEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecharge(request: RechargeRequestEntity): Long

    @Query("UPDATE recharge_requests SET status = :status, adminNote = :note WHERE id = :id")
    suspend fun updateRechargeStatus(id: Long, status: String, note: String?)
}

@Dao
interface PaymentSettingsDao {
    @Query("SELECT * FROM payment_settings WHERE id = 1 LIMIT 1")
    fun getSettingsFlow(): Flow<PaymentSettingsEntity?>

    @Query("SELECT * FROM payment_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettings(): PaymentSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSettings(settings: PaymentSettingsEntity)
}

@Dao
interface CouponDao {
    @Query("SELECT * FROM coupons WHERE isActive = 1")
    fun getActiveCoupons(): Flow<List<CouponEntity>>

    @Query("SELECT * FROM coupons ORDER BY id DESC")
    fun getAllCoupons(): Flow<List<CouponEntity>>

    @Query("SELECT * FROM coupons WHERE code = :code AND isActive = 1 LIMIT 1")
    suspend fun getCouponByCode(code: String): CouponEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoupon(coupon: CouponEntity): Long

    @Query("UPDATE coupons SET timesUsed = timesUsed + 1 WHERE id = :id")
    suspend fun incrementCouponUsage(id: Long)

    @Query("DELETE FROM coupons WHERE id = :id")
    suspend fun deleteCoupon(id: Long)
}

@Dao
interface SupportTicketDao {
    @Query("SELECT * FROM support_tickets WHERE userId = :userId ORDER BY updatedAt DESC")
    fun getTicketsForUser(userId: Long): Flow<List<SupportTicketEntity>>

    @Query("SELECT * FROM support_tickets ORDER BY updatedAt DESC")
    fun getAllTickets(): Flow<List<SupportTicketEntity>>

    @Query("SELECT * FROM support_tickets WHERE id = :ticketId LIMIT 1")
    suspend fun getTicketById(ticketId: Long): SupportTicketEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTicket(ticket: SupportTicketEntity): Long

    @Query("UPDATE support_tickets SET status = :status, updatedAt = :updatedAt, lastMessage = :lastMsg WHERE id = :ticketId")
    suspend fun updateTicketStatus(ticketId: Long, status: String, updatedAt: String, lastMsg: String)

    @Query("SELECT * FROM support_messages WHERE ticketId = :ticketId ORDER BY timestamp ASC")
    fun getMessagesForTicket(ticketId: Long): Flow<List<SupportMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(msg: SupportMessageEntity): Long
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications WHERE userId = :userId OR userId = 0 ORDER BY timestamp DESC")
    fun getNotificationsForUser(userId: Long): Flow<List<NotificationEntity>>

    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity): Long

    @Query("UPDATE notifications SET isRead = 1 WHERE userId = :userId OR userId = 0")
    suspend fun markAllAsRead(userId: Long)
}

@Dao
interface WebsiteSettingsDao {
    @Query("SELECT * FROM website_settings WHERE id = 1 LIMIT 1")
    fun getSettingsFlow(): Flow<WebsiteSettingsEntity?>

    @Query("SELECT * FROM website_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettings(): WebsiteSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateSettings(settings: WebsiteSettingsEntity)
}

@Dao
interface ActivityLogDao {
    @Query("SELECT * FROM activity_logs ORDER BY timestamp DESC LIMIT 100")
    fun getRecentLogs(): Flow<List<ActivityLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: ActivityLogEntity): Long
}

@Dao
interface FavouriteServiceDao {
    @Query("SELECT serviceId FROM favourite_services WHERE userId = :userId")
    fun getFavouriteServiceIds(userId: Long): Flow<List<Long>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavourite(fav: FavouriteServiceEntity)

    @Query("DELETE FROM favourite_services WHERE userId = :userId AND serviceId = :serviceId")
    suspend fun removeFavourite(userId: Long, serviceId: Long)
}
