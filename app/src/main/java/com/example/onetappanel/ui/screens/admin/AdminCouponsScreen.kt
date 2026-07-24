package com.example.onetappanel.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onetappanel.data.model.CouponEntity
import com.example.ui.theme.*
import com.example.onetappanel.util.CurrencyUtils

@Composable
fun AdminCouponsScreen(
    coupons: List<CouponEntity>,
    selectedCurrency: String,
    onCreateCouponSubmit: (CouponEntity) -> Unit,
    onDeleteCouponSubmit: (Long) -> Unit
) {
    var createDialogVisible by remember { mutableStateOf(false) }

    var code by remember { mutableStateOf("") }
    var discountPercent by remember { mutableStateOf("10") }
    var minOrderAmount by remember { mutableStateOf("100") }
    var expiryDate by remember { mutableStateOf("2026-12-31") }

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
                Text("COUPONS MANAGER", fontSize = 20.sp, fontWeight = FontWeight.Black, color = StatusPending)
                Text("Create promotional discount vouchers for users", fontSize = 12.sp, color = TextSecondary)
            }

            Button(
                onClick = { createDialogVisible = true },
                colors = ButtonDefaults.buttonColors(containerColor = StatusPending),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("+ New Coupon", fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(coupons, key = { it.id }) { c ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(c.code, fontSize = 16.sp, fontWeight = FontWeight.Black, color = NeonBluePrimary)
                            Text("Discount: ${c.discountPercent.toInt()}% OFF | Min Order: ${CurrencyUtils.format(c.minOrderAmount, selectedCurrency)}", fontSize = 12.sp, color = StatusCompleted, fontWeight = FontWeight.Bold)
                            Text("Valid until: ${c.expiryDate}", fontSize = 10.sp, color = TextMuted)
                        }

                        IconButton(onClick = { onDeleteCouponSubmit(c.id) }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = ErrorRed)
                        }
                    }
                }
            }
        }
    }

    if (createDialogVisible) {
        AlertDialog(
            onDismissRequest = { createDialogVisible = false },
            title = { Text("Create New Coupon", fontWeight = FontWeight.Bold, color = StatusPending) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = code,
                        onValueChange = { code = it.uppercase() },
                        label = { Text("Coupon Code (e.g. SAVE20)", color = TextSecondary) },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = discountPercent,
                        onValueChange = { discountPercent = it },
                        label = { Text("Discount Percentage (%)", color = TextSecondary) },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = minOrderAmount,
                        onValueChange = { minOrderAmount = it },
                        label = { Text("Min Order Amount (INR)", color = TextSecondary) },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val pct = discountPercent.toDoubleOrNull() ?: 10.0
                        val minAmt = minOrderAmount.toDoubleOrNull() ?: 100.0
                        if (code.isNotBlank()) {
                            onCreateCouponSubmit(
                                CouponEntity(code = code, discountPercent = pct, minOrderAmount = minAmt, expiryDate = expiryDate)
                            )
                            createDialogVisible = false
                            code = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusPending)
                ) {
                    Text("Create Coupon", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { createDialogVisible = false }) { Text("Cancel", color = TextSecondary) }
            },
            containerColor = SurfaceDark
        )
    }
}
