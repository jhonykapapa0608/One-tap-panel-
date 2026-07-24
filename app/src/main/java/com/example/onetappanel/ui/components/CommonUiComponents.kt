package com.example.onetappanel.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onetappanel.data.model.OrderEntity
import com.example.onetappanel.data.model.ServiceEntity
import com.example.onetappanel.data.model.UserEntity
import com.example.ui.theme.*
import com.example.onetappanel.util.CurrencyCode
import com.example.onetappanel.util.CurrencyUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmmTopAppBar(
    title: String,
    currentUser: UserEntity?,
    selectedCurrency: String,
    onCurrencySelect: (String) -> Unit,
    onNotificationClick: () -> Unit,
    onAdminToggle: () -> Unit,
    onLogoutClick: () -> Unit,
    canGoBack: Boolean = false,
    onBackClick: () -> Unit = {}
) {
    var currencyMenuExpanded by remember { mutableStateOf(false) }

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = BackgroundDark,
            titleContentColor = TextPrimary
        ),
        navigationIcon = {
            if (canGoBack) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = NeonBluePrimary
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(NeonBluePrimary, ElectricBlueSecondary))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.FlashOn,
                        contentDescription = "Logo",
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        },
        title = {
            Column {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (currentUser != null) {
                    Text(
                        text = "${currentUser.fullName} (@${currentUser.username})",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        maxLines = 1
                    )
                }
            }
        },
        actions = {
            // Currency Switcher Button
            Box {
                OutlinedButton(
                    onClick = { currencyMenuExpanded = true },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier
                        .height(34.dp)
                        .padding(end = 6.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.linearGradient(listOf(NeonBluePrimary, ElectricBlueSecondary)))
                ) {
                    Text(
                        text = CurrencyCode.fromCode(selectedCurrency).symbol + selectedCurrency,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonBluePrimary
                    )
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = "Select Currency",
                        tint = NeonBluePrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }

                DropdownMenu(
                    expanded = currencyMenuExpanded,
                    onDismissRequest = { currencyMenuExpanded = false },
                    modifier = Modifier.background(SurfaceDark)
                ) {
                    CurrencyCode.entries.forEach { curr ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "${curr.symbol} ${curr.displayName}",
                                    color = if (curr.name == selectedCurrency) NeonBluePrimary else TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = if (curr.name == selectedCurrency) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            onClick = {
                                onCurrencySelect(curr.name)
                                currencyMenuExpanded = false
                            }
                        )
                    }
                }
            }

            // Notifications Bell
            if (currentUser != null) {
                IconButton(onClick = onNotificationClick) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notifications",
                        tint = TextPrimary
                    )
                }
            }

            // Admin Toggle Button
            if (currentUser?.role == "ADMIN") {
                IconButton(onClick = onAdminToggle) {
                    Icon(
                        imageVector = Icons.Filled.AdminPanelSettings,
                        contentDescription = "Admin Switch",
                        tint = StatusPending
                    )
                }
            }

            // Logout
            if (currentUser != null) {
                IconButton(onClick = onLogoutClick) {
                    Icon(
                        imageVector = Icons.Filled.Logout,
                        contentDescription = "Logout",
                        tint = ErrorRed
                    )
                }
            }
        }
    )
}

@Composable
fun SmmBottomNavBar(
    currentRoute: String,
    userRole: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = if (userRole == "ADMIN") {
        listOf(
            Triple("admin_dashboard", "Dashboard", Icons.Filled.Dashboard),
            Triple("admin_orders", "Orders", Icons.Filled.ShoppingBag),
            Triple("admin_users", "Users", Icons.Filled.Group),
            Triple("admin_recharges", "Recharges", Icons.Filled.AccountBalanceWallet),
            Triple("admin_services", "Services", Icons.Filled.Category)
        )
    } else {
        listOf(
            Triple("user_dashboard", "Home", Icons.Filled.Home),
            Triple("services", "Services", Icons.Filled.GridView),
            Triple("new_order", "New Order", Icons.Filled.AddCircle),
            Triple("my_orders", "Orders", Icons.Filled.ReceiptLong),
            Triple("add_funds", "Wallet", Icons.Filled.AccountBalanceWallet)
        )
    }

    NavigationBar(
        containerColor = SurfaceDark,
        contentColor = TextPrimary,
        tonalElevation = 8.dp,
        modifier = modifier.navigationBarsPadding()
    ) {
        items.forEach { (route, label, icon) ->
            val selected = currentRoute == route
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(route) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = if (selected) NeonBluePrimary else TextSecondary
                    )
                },
                label = {
                    Text(
                        text = label,
                        fontSize = 10.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                        color = if (selected) NeonBluePrimary else TextSecondary
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = CardBorder.copy(alpha = 0.5f)
                )
            )
        }
    }
}

@Composable
fun FloatingWhatsappButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = Color(0xFF25D366),
        contentColor = Color.White,
        shape = CircleShape,
        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp),
        modifier = modifier.size(56.dp)
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.Chat,
            contentDescription = "WhatsApp Support",
            modifier = Modifier.size(28.dp)
        )
    }
}

@Composable
fun StatusChip(status: String) {
    val (bgColor: Color, textColor: Color) = when (status) {
        "Pending" -> Pair(StatusPending.copy(alpha = 0.2f), StatusPending)
        "Processing" -> Pair(StatusProcessing.copy(alpha = 0.2f), StatusProcessing)
        "Completed" -> Pair(StatusCompleted.copy(alpha = 0.2f), StatusCompleted)
        "Partial" -> Pair(StatusPartial.copy(alpha = 0.2f), StatusPartial)
        "Cancelled" -> Pair(StatusCancelled.copy(alpha = 0.2f), StatusCancelled)
        "Approved" -> Pair(StatusCompleted.copy(alpha = 0.2f), StatusCompleted)
        "Rejected" -> Pair(StatusCancelled.copy(alpha = 0.2f), StatusCancelled)
        else -> Pair(SurfaceVariantDark, TextSecondary)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(1.dp, textColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = status,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
fun ServiceCard(
    service: ServiceEntity,
    selectedCurrency: String,
    isFavourite: Boolean,
    onFavouriteToggle: () -> Unit,
    onSelectService: (ServiceEntity) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onSelectService(service) },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    if (service.isPopular) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = NeonBluePrimary.copy(alpha = 0.2f),
                            modifier = Modifier.padding(end = 6.dp)
                        ) {
                            Text(
                                text = "POPULAR",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonBluePrimary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    if (service.isFeatured) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = StatusPending.copy(alpha = 0.2f),
                            modifier = Modifier.padding(end = 6.dp)
                        ) {
                            Text(
                                text = "HOT",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = StatusPending,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = "ID: ${service.serviceIdString}",
                        fontSize = 11.sp,
                        color = TextMuted,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                IconButton(
                    onClick = onFavouriteToggle,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = if (isFavourite) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Fav",
                        tint = if (isFavourite) NeonBluePrimary else TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = service.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            if (service.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = service.description,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Min: ${service.minQuantity} | Max: ${service.maxQuantity}",
                    fontSize = 11.sp,
                    color = TextMuted
                )

                Text(
                    text = "${CurrencyUtils.format(service.ratePer1000, selectedCurrency)} / 1k",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = NeonBluePrimary
                )
            }
        }
    }
}

@Composable
fun OrderCard(
    order: OrderEntity,
    selectedCurrency: String,
    onWhatsappShare: (OrderEntity) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Order #${order.orderIdString}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonBluePrimary
                )

                StatusChip(status = order.status)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = order.serviceName,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Link: ${order.link}",
                fontSize = 12.sp,
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            HorizontalDivider(color = SurfaceVariantDark)

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Qty: ${order.quantity} | Total: ${CurrencyUtils.format(order.price, selectedCurrency)}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "${order.date} at ${order.time}",
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                }

                Button(
                    onClick = { onWhatsappShare(order) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Chat,
                        contentDescription = "WhatsApp Order",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "WhatsApp", fontSize = 11.sp, color = Color.White)
                }
            }
        }
    }
}
