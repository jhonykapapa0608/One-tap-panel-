package com.example.onetappanel.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onetappanel.data.model.RechargeRequestEntity
import com.example.onetappanel.ui.components.StatusChip
import com.example.ui.theme.*
import com.example.onetappanel.util.CurrencyUtils

@Composable
fun AdminRechargesScreen(
    recharges: List<RechargeRequestEntity>,
    selectedCurrency: String,
    onApproveRecharge: (Long, String) -> Unit,
    onRejectRecharge: (Long, String) -> Unit
) {
    var adminNote by remember { mutableStateOf("Verified by Admin") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(16.dp)
    ) {
        Text("RECHARGE REQUESTS QUEUE", fontSize = 20.sp, fontWeight = FontWeight.Black, color = StatusCompleted)
        Text("Approve valid UTR payments to credit user wallets automatically", fontSize = 12.sp, color = TextSecondary)

        Spacer(modifier = Modifier.height(16.dp))

        if (recharges.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No recharge requests found.", color = TextMuted, fontSize = 13.sp)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(recharges, key = { it.id }) { req ->
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
                                Column {
                                    Text("Req #${req.rechargeIdString} by @${req.username}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NeonBluePrimary)
                                    Text("UTR: ${req.uTrNumber}", fontSize = 13.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                                }
                                StatusChip(status = req.status)
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text("Amount: ${CurrencyUtils.format(req.amount, selectedCurrency)}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = StatusCompleted)
                            Text("Submitted at: ${req.createdAt}", fontSize = 10.sp, color = TextMuted)

                            if (req.status == "Pending") {
                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Button(
                                        onClick = { onApproveRecharge(req.id, adminNote) },
                                        modifier = Modifier.weight(1f).height(40.dp),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = StatusCompleted)
                                    ) {
                                        Icon(Icons.Filled.Check, contentDescription = null, tint = Color.Black)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("APPROVE", color = Color.Black, fontWeight = FontWeight.Bold)
                                    }

                                    OutlinedButton(
                                        onClick = { onRejectRecharge(req.id, "Invalid UTR") },
                                        modifier = Modifier.weight(1f).height(40.dp),
                                        shape = RoundedCornerShape(10.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, ErrorRed)
                                    ) {
                                        Icon(Icons.Filled.Close, contentDescription = null, tint = ErrorRed)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("REJECT", color = ErrorRed, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
