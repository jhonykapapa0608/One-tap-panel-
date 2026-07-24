package com.example.onetappanel.ui.screens.user

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onetappanel.data.model.OrderEntity
import com.example.onetappanel.data.model.ServiceEntity
import com.example.onetappanel.data.model.UserEntity
import com.example.onetappanel.ui.components.OrderCard
import com.example.ui.theme.*
import com.example.onetappanel.util.CurrencyUtils

@Composable
fun UserDashboardScreen(
    user: UserEntity,
    selectedCurrency: String,
    orders: List<OrderEntity>,
    popularServices: List<ServiceEntity>,
    onNavigate: (String) -> Unit,
    onSelectService: (ServiceEntity) -> Unit,
    onWhatsappOrderShare: (OrderEntity) -> Unit
) {
    val totalSpentINR = orders.filter { it.status != "Cancelled" }.sumOf { it.price }
    val activeOrdersCount = orders.count { it.status == "Pending" || it.status == "Processing" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Main Wallet Banner Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            border = androidx.compose.foundation.BorderStroke(
                1.5.dp,
                Brush.linearGradient(listOf(NeonBluePrimary, ElectricBlueSecondary))
            )
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("WALLET BALANCE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = CurrencyUtils.format(user.walletBalance, selectedCurrency),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            color = NeonBluePrimary
                        )
                    }

                    Button(
                        onClick = { onNavigate("add_funds") },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonBluePrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("RECHARGE", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = SurfaceVariantDark)
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("TOTAL SPENT", fontSize = 10.sp, color = TextMuted)
                        Text(CurrencyUtils.format(totalSpentINR, selectedCurrency), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                    Column {
                        Text("ACTIVE ORDERS", fontSize = 10.sp, color = TextMuted)
                        Text("$activeOrdersCount", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = StatusPending)
                    }
                    Column {
                        Text("TOTAL ORDERS", fontSize = 10.sp, color = TextMuted)
                        Text("${orders.size}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AccentCyan)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Quick Actions Grid
        Text("QUICK ACTIONS", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextSecondary, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            QuickActionButton(
                label = "New Order",
                icon = Icons.Filled.AddCircle,
                accentColor = NeonBluePrimary,
                modifier = Modifier.weight(1f),
                onClick = { onNavigate("new_order") }
            )
            QuickActionButton(
                label = "Services",
                icon = Icons.Filled.GridView,
                accentColor = ElectricBlueSecondary,
                modifier = Modifier.weight(1f),
                onClick = { onNavigate("services") }
            )
            QuickActionButton(
                label = "My Orders",
                icon = Icons.Filled.ReceiptLong,
                accentColor = AccentCyan,
                modifier = Modifier.weight(1f),
                onClick = { onNavigate("my_orders") }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            QuickActionButton(
                label = "Coupons",
                icon = Icons.Filled.LocalOffer,
                accentColor = StatusPending,
                modifier = Modifier.weight(1f),
                onClick = { onNavigate("coupons") }
            )
            QuickActionButton(
                label = "Referral",
                icon = Icons.Filled.CardGiftcard,
                accentColor = StatusCompleted,
                modifier = Modifier.weight(1f),
                onClick = { onNavigate("referral") }
            )
            QuickActionButton(
                label = "Support",
                icon = Icons.Filled.SupportAgent,
                accentColor = StatusPartial,
                modifier = Modifier.weight(1f),
                onClick = { onNavigate("support") }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Popular Services Carousel
        if (popularServices.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("POPULAR SERVICES", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextSecondary, letterSpacing = 1.sp)
                TextButton(onClick = { onNavigate("services") }) {
                    Text("View All", fontSize = 12.sp, color = NeonBluePrimary)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(popularServices) { service ->
                    Card(
                        modifier = Modifier
                            .width(220.dp)
                            .clickable { onSelectService(service) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = CardBackground),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = NeonBluePrimary.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = service.categoryName,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonBluePrimary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = service.name,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                maxLines = 2
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "${CurrencyUtils.format(service.ratePer1000, selectedCurrency)} / 1,000",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = StatusCompleted
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Recent Orders List
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("RECENT ORDERS", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextSecondary, letterSpacing = 1.sp)
            TextButton(onClick = { onNavigate("my_orders") }) {
                Text("See History", fontSize = 12.sp, color = NeonBluePrimary)
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        if (orders.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Filled.ShoppingBag, contentDescription = null, tint = TextMuted, modifier = Modifier.size(36.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No orders placed yet.", fontSize = 13.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { onNavigate("new_order") },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonBluePrimary)
                    ) {
                        Text("Place First Order", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            orders.take(3).forEach { order ->
                OrderCard(order = order, selectedCurrency = selectedCurrency, onWhatsappShare = onWhatsappOrderShare)
            }
        }
    }
}

@Composable
fun QuickActionButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
    ) {
        Column(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = label, tint = accentColor, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }
    }
}
