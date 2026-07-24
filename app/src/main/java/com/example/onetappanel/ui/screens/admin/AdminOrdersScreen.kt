package com.example.onetappanel.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onetappanel.data.model.OrderEntity
import com.example.onetappanel.ui.components.StatusChip
import com.example.ui.theme.*
import com.example.onetappanel.util.CurrencyUtils

@Composable
fun AdminOrdersScreen(
    orders: List<OrderEntity>,
    selectedCurrency: String,
    onUpdateOrderStatus: (Long, String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatusFilter by remember { mutableStateOf("All") }

    val statusTabs = listOf("All", "Pending", "Processing", "Completed", "Partial", "Cancelled")

    val filteredOrders = remember(searchQuery, selectedStatusFilter, orders) {
        orders.filter { o ->
            val matchesStatus = selectedStatusFilter == "All" || o.status.equals(selectedStatusFilter, ignoreCase = true)
            val matchesSearch = searchQuery.isBlank() ||
                    o.orderIdString.contains(searchQuery, ignoreCase = true) ||
                    o.username.contains(searchQuery, ignoreCase = true) ||
                    o.userFullName.contains(searchQuery, ignoreCase = true) ||
                    o.serviceName.contains(searchQuery, ignoreCase = true) ||
                    o.link.contains(searchQuery, ignoreCase = true)

            matchesStatus && matchesSearch
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(16.dp)
    ) {
        Text("ADMIN ORDERS QUEUE", fontSize = 20.sp, fontWeight = FontWeight.Black, color = StatusPending)
        Text("Manage status or trigger auto-refunds on cancellation", fontSize = 12.sp, color = TextSecondary)

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search by user, order ID, or link...", color = TextMuted) },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = NeonBluePrimary) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonBluePrimary, unfocusedBorderColor = CardBorder,
                focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(statusTabs) { status ->
                FilterChip(
                    selected = selectedStatusFilter == status,
                    onClick = { selectedStatusFilter = status },
                    label = { Text(status) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = StatusPending,
                        selectedLabelColor = Color.Black
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(filteredOrders, key = { it.id }) { o ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
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
                            Text("Order #${o.orderIdString} by @${o.username}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NeonBluePrimary)
                            StatusChip(status = o.status)
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(o.serviceName, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        Text("Link: ${o.link}", fontSize = 12.sp, color = AccentCyan)
                        Text("Qty: ${o.quantity} | Total: ${CurrencyUtils.format(o.price, selectedCurrency)}", fontSize = 12.sp, color = TextSecondary)
                        Text("Placed at: ${o.date} ${o.time}", fontSize = 10.sp, color = TextMuted)

                        Spacer(modifier = Modifier.height(10.dp))

                        // Quick Status Switch Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            StatusOptionButton("Processing", StatusProcessing) { onUpdateOrderStatus(o.id, "Processing") }
                            StatusOptionButton("Completed", StatusCompleted) { onUpdateOrderStatus(o.id, "Completed") }
                            StatusOptionButton("Partial", StatusPartial) { onUpdateOrderStatus(o.id, "Partial") }
                            StatusOptionButton("Cancel & Refund", StatusCancelled) { onUpdateOrderStatus(o.id, "Cancelled") }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RowScope.StatusOptionButton(label: String, color: Color, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.weight(1f).height(32.dp),
        contentPadding = PaddingValues(horizontal = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color)
    ) {
        Text(label, fontSize = 9.sp, color = color, fontWeight = FontWeight.Bold)
    }
}
