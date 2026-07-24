package com.example.onetappanel.util

import com.example.onetappanel.data.model.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

data class FullAppBackup(
    val exportDate: String = System.currentTimeMillis().toString(),
    val appVersion: String = "1.0",
    val users: List<UserEntity> = emptyList(),
    val orders: List<OrderEntity> = emptyList(),
    val categories: List<ServiceCategoryEntity> = emptyList(),
    val services: List<ServiceEntity> = emptyList(),
    val recharges: List<RechargeRequestEntity> = emptyList(),
    val coupons: List<CouponEntity> = emptyList(),
    val tickets: List<SupportTicketEntity> = emptyList(),
    val messages: List<SupportMessageEntity> = emptyList(),
    val notifications: List<NotificationEntity> = emptyList()
)

object BackupUtils {
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val adapter = moshi.adapter(FullAppBackup::class.java)

    fun exportToJson(backup: FullAppBackup): String {
        return adapter.toJson(backup)
    }

    fun importFromJson(jsonStr: String): FullAppBackup? {
        return try {
            adapter.fromJson(jsonStr)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
