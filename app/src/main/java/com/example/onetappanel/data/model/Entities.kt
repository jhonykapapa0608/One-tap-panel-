package com.example.onetappanel.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fullName: String,
    val username: String,
    val email: String,
    val phoneNumber: String,
    val profilePhoto: String = "",
    val role: String = "USER", // "USER" or "ADMIN"
    val passwordHash: String,
    val walletBalance: Double = 0.0,
    val joinDate: String = "",
    val emailVerified: Boolean = true,
    val referralCode: String = "",
    val referredBy: String = "",
    val isBlocked: Boolean = false
)

@Entity(tableName = "wallet_transactions")
data class WalletTransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val type: String, // "CREDIT" or "DEBIT"
    val amount: Double,
    val currency: String = "INR",
    val description: String,
    val uTrNumber: String? = null,
    val orderId: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orderIdString: String,
    val userId: Long,
    val userFullName: String,
    val username: String,
    val userEmail: String,
    val userPhone: String,
    val serviceId: Long,
    val serviceName: String,
    val categoryName: String,
    val link: String,
    val quantity: Int,
    val price: Double,
    val currency: String = "INR",
    val date: String,
    val time: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "Pending" // "Pending", "Processing", "Completed", "Partial", "Cancelled"
)

@Entity(tableName = "service_categories")
data class ServiceCategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val categoryIdString: String,
    val name: String,
    val iconName: String = "category",
    val displayOrder: Int = 0,
    val isActive: Boolean = true
)

@Entity(tableName = "services")
data class ServiceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val serviceIdString: String,
    val categoryId: Long,
    val categoryName: String,
    val name: String,
    val minQuantity: Int = 100,
    val maxQuantity: Int = 100000,
    val ratePer1000: Double,
    val currency: String = "INR",
    val description: String = "",
    val isPopular: Boolean = false,
    val isFeatured: Boolean = false,
    val isActive: Boolean = true
)

@Entity(tableName = "recharge_requests")
data class RechargeRequestEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val rechargeIdString: String,
    val userId: Long,
    val userFullName: String,
    val username: String,
    val email: String,
    val phone: String,
    val amount: Double,
    val currency: String = "INR",
    val uTrNumber: String,
    val screenshotPath: String = "",
    val status: String = "Pending", // "Pending", "Approved", "Rejected"
    val adminNote: String? = null,
    val createdAt: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "payment_settings")
data class PaymentSettingsEntity(
    @PrimaryKey val id: Long = 1,
    val upiId: String = "onetappanel@upi",
    val receiverName: String = "One Tap Panel Official",
    val qrImageUrl: String = "",
    val instructions: String = "Scan QR Code using PhonePe, GPay, or Paytm. Enter exact UTR/Ref Number below for instant credit.",
    val minRechargeAmount: Double = 10.0
)

@Entity(tableName = "coupons")
data class CouponEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val code: String,
    val discountPercent: Double = 0.0,
    val discountAmount: Double = 0.0,
    val minOrderAmount: Double = 0.0,
    val maxDiscount: Double = 1000.0,
    val expiryDate: String = "2030-12-31",
    val usageLimit: Int = 1000,
    val timesUsed: Int = 0,
    val isActive: Boolean = true
)

@Entity(tableName = "support_tickets")
data class SupportTicketEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ticketIdString: String,
    val userId: Long,
    val userFullName: String,
    val subject: String,
    val category: String, // "Order", "Wallet", "Technical", "Other"
    val priority: String = "Normal", // "Low", "Normal", "High"
    val status: String = "Open", // "Open", "Replied", "Closed"
    val createdAt: String,
    val updatedAt: String,
    val lastMessage: String = ""
)

@Entity(tableName = "support_messages")
data class SupportMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ticketId: Long,
    val senderRole: String, // "USER" or "ADMIN"
    val senderName: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long = 0, // 0 for broadcast to all users
    val title: String,
    val message: String,
    val type: String = "ANNOUNCEMENT", // "ORDER", "WALLET", "RECHARGE", "COUPON", "ANNOUNCEMENT"
    val isRead: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "website_settings")
data class WebsiteSettingsEntity(
    @PrimaryKey val id: Long = 1,
    val siteName: String = "One Tap Panel",
    val logoUri: String = "",
    val whatsappSupportNumber: String = "919999999999",
    val telegramLink: String = "https://t.me/onetappanel",
    val instagramLink: String = "https://instagram.com/onetappanel",
    val defaultCurrency: String = "INR",
    val isMaintenanceMode: Boolean = false
)

@Entity(tableName = "activity_logs")
data class ActivityLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val actorRole: String, // "USER" or "ADMIN"
    val actorName: String,
    val actionType: String,
    val details: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "favourite_services", primaryKeys = ["userId", "serviceId"])
data class FavouriteServiceEntity(
    val userId: Long,
    val serviceId: Long
)
