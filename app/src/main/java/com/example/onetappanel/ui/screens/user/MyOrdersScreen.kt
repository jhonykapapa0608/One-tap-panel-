package com.example.onetappanel.ui.screens.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ReceiptLong
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
import com.example.onetappanel.ui.components.OrderCard
import com.example.ui.theme.*

@Composable
fun MyOrdersScreen(
    orders: List<OrderEntity>,
    selectedCurrency: String,
    onWhatsappOrderShare: (OrderEntity) -> Unit,
    onNewOrderClick: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatusFilter by remember { mutableStateOf("All") }

    val statusTabs = listOf("All", "Pending", "Processing", "Completed", "Partial", "Cancelled")

    val filteredOrders = remember(searchQuery, selectedStatusFilter, orders) {
        orders.filter { order ->
            val matchesStatus = selectedStatusFilter == "All" || order.status.equals(selectedStatusFilter, ignoreCase = true)
            val matchesSearch = searchQuery.isBlank() ||
                    order.orderIdString.contains(searchQuery, ignoreCase = true) ||
                    order.serviceName.contains(searchQuery, ignoreCase = true) ||
                    order.link.contains(searchQuery, ignoreCase = true)

            matchesStatus && matchesSearch
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "ORDER HISTORY",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = NeonBluePrimary
                )
                Text(
                    text = "Track status of your orders in real-time",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            Button(
                onClick = onNewOrderClick,
                colors = ButtonDefaults.buttonColors(containerColor = NeonBluePrimary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("+ New Order", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search order ID, service or link...", color = TextMuted) },
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

        // Status Tabs
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(statusTabs) { status ->
                val count = if (status == "All") orders.size else orders.count { it.status.equals(status, ignoreCase = true) }
                FilterChip(
                    selected = selectedStatusFilter == status,
                    onClick = { selectedStatusFilter = status },
                    label = { Text("$status ($count)") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = NeonBluePrimary,
                        selectedLabelColor = Color.Black
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (filteredOrders.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.ReceiptLong, contentDescription = null, tint = TextMuted, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No orders found", fontSize = 14.sp, color = TextSecondary)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredOrders, key = { it.id }) { order ->
                    OrderCard(
                        order = order,
                        selectedCurrency = selectedCurrency,
                        onWhatsappShare = onWhatsappOrderShare
                    )
                }
            }
        }
    }
}
