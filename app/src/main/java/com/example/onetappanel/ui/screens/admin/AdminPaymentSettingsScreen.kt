package com.example.onetappanel.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onetappanel.data.model.PaymentSettingsEntity
import com.example.ui.theme.*

@Composable
fun AdminPaymentSettingsScreen(
    currentSettings: PaymentSettingsEntity?,
    onSavePaymentSettingsSubmit: (PaymentSettingsEntity) -> Unit
) {
    var upiId by remember { mutableStateOf(currentSettings?.upiId ?: "onetappanel@upi") }
    var receiverName by remember { mutableStateOf(currentSettings?.receiverName ?: "One Tap Panel Official") }
    var minRecharge by remember { mutableStateOf(currentSettings?.minRechargeAmount?.toString() ?: "10") }
    var instructions by remember { mutableStateOf(currentSettings?.instructions ?: "Scan QR Code using PhonePe, GPay or Paytm. Enter UTR number below.") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("PAYMENT GATEWAY SETTINGS", fontSize = 20.sp, fontWeight = FontWeight.Black, color = ElectricBlueSecondary)
        Text("Update UPI ID and receiver details shown on user Add Funds page", fontSize = 12.sp, color = TextSecondary)

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                OutlinedTextField(
                    value = upiId,
                    onValueChange = { upiId = it },
                    label = { Text("UPI ID", color = TextSecondary) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = receiverName,
                    onValueChange = { receiverName = it },
                    label = { Text("Receiver Display Name", color = TextSecondary) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = minRecharge,
                    onValueChange = { minRecharge = it },
                    label = { Text("Minimum Recharge Amount (INR)", color = TextSecondary) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = instructions,
                    onValueChange = { instructions = it },
                    label = { Text("Payment Instructions for Users", color = TextSecondary) },
                    modifier = Modifier.fillMaxWidth().height(100.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        onSavePaymentSettingsSubmit(
                            PaymentSettingsEntity(
                                id = currentSettings?.id ?: 1,
                                upiId = upiId,
                                receiverName = receiverName,
                                minRechargeAmount = minRecharge.toDoubleOrNull() ?: 10.0,
                                instructions = instructions
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonBluePrimary)
                ) {
                    Text("SAVE PAYMENT SETTINGS", fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }
        }
    }
}
