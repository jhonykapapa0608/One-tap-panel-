package com.example.onetappanel.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.onetappanel.data.dao.*
import com.example.onetappanel.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        WalletTransactionEntity::class,
        OrderEntity::class,
        ServiceCategoryEntity::class,
        ServiceEntity::class,
        RechargeRequestEntity::class,
        PaymentSettingsEntity::class,
        CouponEntity::class,
        SupportTicketEntity::class,
        SupportMessageEntity::class,
        NotificationEntity::class,
        WebsiteSettingsEntity::class,
        ActivityLogEntity::class,
        FavouriteServiceEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun walletTransactionDao(): WalletTransactionDao
    abstract fun orderDao(): OrderDao
    abstract fun serviceCategoryDao(): ServiceCategoryDao
    abstract fun serviceDao(): ServiceDao
    abstract fun rechargeDao(): RechargeDao
    abstract fun paymentSettingsDao(): PaymentSettingsDao
    abstract fun couponDao(): CouponDao
    abstract fun supportTicketDao(): SupportTicketDao
    abstract fun notificationDao(): NotificationDao
    abstract fun websiteSettingsDao(): WebsiteSettingsDao
    abstract fun activityLogDao(): ActivityLogDao
    abstract fun favouriteServiceDao(): FavouriteServiceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "one_tap_panel_db"
                )
                .addCallback(SeedDatabaseCallback(context))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class SeedDatabaseCallback(private val context: Context) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                CoroutineScope(Dispatchers.IO).launch {
                    val database = getDatabase(context)
                    seedInitialData(database)
                }
            }
        }

        private suspend fun seedInitialData(db: AppDatabase) {
            // Seed Admin User
            val adminUser = UserEntity(
                fullName = "Admin Master",
                username = "admin",
                email = "admin@onetappanel.com",
                phoneNumber = "+919876543210",
                role = "ADMIN",
                passwordHash = "admin123", // Secure plain-check hash for demonstration
                walletBalance = 25000.0,
                joinDate = "2026-01-01",
                emailVerified = true,
                referralCode = "OTPADMIN"
            )
            val adminId = db.userDao().insertUser(adminUser)

            // Seed Payment Settings
            db.paymentSettingsDao().insertOrUpdateSettings(
                PaymentSettingsEntity(
                    id = 1,
                    upiId = "onetappanel@upi",
                    receiverName = "One Tap Panel Official",
                    instructions = "Scan QR Code or pay directly to UPI ID on GPay / PhonePe / Paytm. Enter UTR Transaction Ref Number below for instant credit.",
                    minRechargeAmount = 10.0
                )
            )

            // Seed Website Settings
            db.websiteSettingsDao().updateSettings(
                WebsiteSettingsEntity(
                    id = 1,
                    siteName = "One Tap Panel",
                    whatsappSupportNumber = "+919876543210",
                    telegramLink = "https://t.me/onetappanel",
                    instagramLink = "https://instagram.com/onetappanel",
                    defaultCurrency = "INR",
                    isMaintenanceMode = false
                )
            )

            // Seed Categories
            val catIg = db.serviceCategoryDao().insertCategory(
                ServiceCategoryEntity(categoryIdString = "CAT-1", name = "Instagram Growth", iconName = "camera_alt", displayOrder = 1)
            )
            val catYt = db.serviceCategoryDao().insertCategory(
                ServiceCategoryEntity(categoryIdString = "CAT-2", name = "YouTube Boost", iconName = "play_circle_filled", displayOrder = 2)
            )
            val catTg = db.serviceCategoryDao().insertCategory(
                ServiceCategoryEntity(categoryIdString = "CAT-3", name = "Telegram Members", iconName = "telegram", displayOrder = 3)
            )
            val catFb = db.serviceCategoryDao().insertCategory(
                ServiceCategoryEntity(categoryIdString = "CAT-4", name = "Facebook & TikTok", iconName = "thumb_up", displayOrder = 4)
            )

            // Seed Services
            db.serviceDao().insertService(
                ServiceEntity(
                    serviceIdString = "SRV-101",
                    categoryId = catIg,
                    categoryName = "Instagram Growth",
                    name = "Instagram Real Followers [Instant Start - HQ Refill]",
                    minQuantity = 100,
                    maxQuantity = 100000,
                    ratePer1000 = 89.0,
                    description = "Instant speed up to 20k/day. Real looking profile pictures with non-drop guarantee.",
                    isPopular = true,
                    isFeatured = true
                )
            )

            db.serviceDao().insertService(
                ServiceEntity(
                    serviceIdString = "SRV-102",
                    categoryId = catIg,
                    categoryName = "Instagram Growth",
                    name = "Instagram Likes [Super Fast - Non Drop]",
                    minQuantity = 50,
                    maxQuantity = 50000,
                    ratePer1000 = 29.0,
                    description = "Instant start within 30 seconds. Works on posts and reels.",
                    isPopular = false,
                    isFeatured = true
                )
            )

            db.serviceDao().insertService(
                ServiceEntity(
                    serviceIdString = "SRV-103",
                    categoryId = catIg,
                    categoryName = "Instagram Growth",
                    name = "Instagram Reel Views + Reach Boost",
                    minQuantity = 500,
                    maxQuantity = 1000000,
                    ratePer1000 = 9.0,
                    description = "Super cheap high quality views to get your reels onto the Explore tab.",
                    isPopular = true,
                    isFeatured = false
                )
            )

            db.serviceDao().insertService(
                ServiceEntity(
                    serviceIdString = "SRV-201",
                    categoryId = catYt,
                    categoryName = "YouTube Boost",
                    name = "YouTube Views [High Retention - Monetizable]",
                    minQuantity = 500,
                    maxQuantity = 500000,
                    ratePer1000 = 129.0,
                    description = "Safe for monetization. Retention 2 to 5 minutes per view.",
                    isPopular = true,
                    isFeatured = true
                )
            )

            db.serviceDao().insertService(
                ServiceEntity(
                    serviceIdString = "SRV-202",
                    categoryId = catYt,
                    categoryName = "YouTube Boost",
                    name = "YouTube Real Subscribers [30 Days Auto Refill]",
                    minQuantity = 100,
                    maxQuantity = 10000,
                    ratePer1000 = 499.0,
                    description = "Organic looking profiles, gradual safe drip-feed rate.",
                    isPopular = true,
                    isFeatured = false
                )
            )

            db.serviceDao().insertService(
                ServiceEntity(
                    serviceIdString = "SRV-301",
                    categoryId = catTg,
                    categoryName = "Telegram Members",
                    name = "Telegram Channel Members [Non-Drop - Instant]",
                    minQuantity = 100,
                    maxQuantity = 50000,
                    ratePer1000 = 69.0,
                    description = "Fast speed, zero drop rate, clean account names.",
                    isPopular = false,
                    isFeatured = true
                )
            )

            db.serviceDao().insertService(
                ServiceEntity(
                    serviceIdString = "SRV-401",
                    categoryId = catFb,
                    categoryName = "Facebook & TikTok",
                    name = "TikTok Followers [Instant Delivery]",
                    minQuantity = 100,
                    maxQuantity = 50000,
                    ratePer1000 = 99.0,
                    description = "High quality profiles, start time 0-15 minutes.",
                    isPopular = true,
                    isFeatured = false
                )
            )

            // Seed Sample Coupon
            db.couponDao().insertCoupon(
                CouponEntity(
                    code = "ONETAP10",
                    discountPercent = 10.0,
                    minOrderAmount = 50.0,
                    maxDiscount = 200.0,
                    expiryDate = "2028-12-31",
                    usageLimit = 500,
                    timesUsed = 0,
                    isActive = true
                )
            )

            // Seed Welcome Announcement
            db.notificationDao().insertNotification(
                NotificationEntity(
                    userId = 0, // Broadcast
                    title = "🚀 Welcome to One Tap Panel!",
                    message = "Get 10% OFF on your orders with coupon code ONETAP10! Fast, reliable, 24/7 automated delivery.",
                    type = "ANNOUNCEMENT",
                    timestamp = System.currentTimeMillis()
                )
            )

            // Seed Activity Log
            db.activityLogDao().insertLog(
                ActivityLogEntity(
                    actorRole = "ADMIN",
                    actorName = "Admin Master",
                    actionType = "SYSTEM_INIT",
                    details = "One Tap Panel SMM System initialized successfully.",
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }
}
