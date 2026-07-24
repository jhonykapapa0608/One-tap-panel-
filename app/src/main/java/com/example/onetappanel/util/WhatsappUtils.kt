package com.example.onetappanel.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import java.net.URLEncoder

object WhatsappUtils {

    fun buildOrderMessage(
        orderId: String,
        serviceName: String,
        link: String,
        quantity: Int,
        priceFormatted: String
    ): String {
        return """
            🚀 *ONE TAP PANEL - ORDER DETAILS*
            ------------------------------------
            📌 *Order ID:* $orderId
            ⚡ *Service:* $serviceName
            🔗 *Target Link:* $link
            📊 *Quantity:* $quantity
            💰 *Total Paid:* $priceFormatted
            ------------------------------------
            Hi Admin, please check my order status!
        """.trimIndent()
    }

    fun openOrderWhatsAppChat(
        context: Context,
        adminPhoneNumber: String,
        orderId: String,
        serviceName: String,
        link: String,
        quantity: Int,
        price: Double,
        currency: String
    ) {
        val priceFormatted = CurrencyUtils.format(price, currency)
        val message = buildOrderMessage(orderId, serviceName, link, quantity, priceFormatted)
        openWhatsappWithMessage(context, adminPhoneNumber, message)
    }

    fun openGeneralSupportChat(
        context: Context,
        adminPhoneNumber: String,
        username: String
    ) {
        val message = "Hi One Tap Panel Support, I am @$username. I need assistance regarding my account/orders."
        openWhatsappWithMessage(context, adminPhoneNumber, message)
    }

    fun openWhatsappWithMessage(context: Context, whatsappNumber: String, message: String) {
        try {
            val cleanNumber = whatsappNumber.replace("+", "").replace(" ", "").replace("-", "")
            val encodedMessage = URLEncoder.encode(message, "UTF-8")
            val uri = Uri.parse("https://api.whatsapp.com/send?phone=$cleanNumber&text=$encodedMessage")
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
