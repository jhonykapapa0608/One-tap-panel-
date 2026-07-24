package com.example.onetappanel.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onetappanel.data.model.*
import com.example.ui.theme.*
import com.example.onetappanel.util.CurrencyUtils

@Composable
fun AdminDashboardScreen(
    users: List<UserEntity>,
    orders: List<OrderEntity>,
    recharges: List<RechargeRequestEntity>,
    services: List<ServiceEntity>,
    selectedCurrency: String,
    onNavigate: (String) -> Unit
) {
    val totalRevenueINR = orders.filter { it.status == "Completed" }.sumOf { it.price }
    val pendingOrdersCount = orders.count { it.status == "Pending" || it.status == "Processing" }
    val pendingRechargesCount = recharges.count { it.status == "Pending" }
    val totalWalletBalanceINR = users.sumOf { it.walletBalance }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "ADMIN MASTER PANEL",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = StatusPending
                )
                Text(
                    text = "Complete control over users, orders, wallet & system",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = StatusPending.copy(alpha = 0.2f),
                border = androidx.compose.foundation.BorderStroke(1.dp, StatusPending)
            ) {
                Text(
                    text = "ADMIN",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = StatusPending,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Analytics Cards 2x3 Grid
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AdminStatCard(
                    title = "TOTAL USERS",
                    value = "${users.size}",
                    icon = Icons.Filled.Group,
                    color = NeonBluePrimary,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate("admin_users") }
                )
                AdminStatCard(
                    title = "PENDING ORDERS",
                    value = "$pendingOrdersCount",
                    icon = Icons.Filled.ShoppingBag,
                    color = StatusPending,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate("admin_orders") }
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AdminStatCard(
                    title = "RECHARGE REQS",
                    value = "$pendingRechargesCount",
                    icon = Icons.Filled.AccountBalanceWallet,
                    color = StatusPartial,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate("admin_recharges") }
                )
                AdminStatCard(
                    title = "TOTAL REVENUE",
                    value = CurrencyUtils.format(totalRevenueINR, selectedCurrency),
                    icon = Icons.Filled.Payments,
                    color = StatusCompleted,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate("admin_orders") }
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AdminStatCard(
                    title = "USER BALANCES",
                    value = CurrencyUtils.format(totalWalletBalanceINR, selectedCurrency),
                    icon = Icons.Filled.AccountBalance,
                    color = AccentCyan,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate("admin_users") }
                )
                AdminStatCard(
                    title = "TOTAL SERVICES",
                    value = "${services.size}",
                    icon = Icons.Filled.GridView,
                    color = ElectricBlueSecondary,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate("admin_services") }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Admin Action Navigation Items
        Text("SYSTEM MANAGEMENT", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextSecondary, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(10.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            AdminActionRow("Manage Users & Balances", "View accounts, edit wallet, block users", Icons.Filled.Group, NeonBluePrimary) { onNavigate("admin_users") }
            AdminActionRow("Manage Orders Queue", "Update order status or cancel with auto refund", Icons.Filled.ReceiptLong, StatusPending) { onNavigate("admin_orders") }
            AdminActionRow("Manage Recharge Requests", "Approve UTR payments or reject", Icons.Filled.AccountBalanceWallet, StatusCompleted) { onNavigate("admin_recharges") }
            AdminActionRow("Manage Services & Categories", "Add, edit prices per 1k or hide services", Icons.Filled.Category, AccentCyan) { onNavigate("admin_services") }
            AdminActionRow("Payment & QR Settings", "Change UPI ID, receiver name & payment QR", Icons.Filled.QrCode2, ElectricBlueSecondary) { onNavigate("admin_payment_settings") }
            AdminActionRow("Coupons & Discount Manager", "Create coupon codes & usage limits", Icons.Filled.LocalOffer, StatusPending) { onNavigate("admin_coupons") }
            AdminActionRow("Website & Backup Settings", "App settings, maintenance mode & database backup", Icons.Filled.Settings, TextPrimary) { onNavigate("admin_website_settings") }
        }
    }
}

@Composable
fun AdminStatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                Box(
                    modifier = Modifier.size(28.dp).clip(CircleShape).background(color.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Black, color = TextPrimary)
        }
    }
}

@Composable
fun AdminActionRow(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
    ) {
        Row(
            modifier = Modifier.padding(14.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(38.dp).clip(CircleShape).background(iconTint.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(subtitle, fontSize = 11.sp, color = TextMuted)
            }

            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = TextMuted)
        }
    }
}
