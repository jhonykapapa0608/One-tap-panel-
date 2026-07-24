package com.example.onetappanel.ui.screens.user

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onetappanel.data.model.PaymentSettingsEntity
import com.example.onetappanel.data.model.RechargeRequestEntity
import com.example.onetappanel.data.model.UserEntity
import com.example.onetappanel.ui.components.StatusChip
import com.example.ui.theme.*
import com.example.onetappanel.util.CurrencyUtils

@Composable
fun AddFundsScreen(
    user: UserEntity,
    selectedCurrency: String,
    paymentSettings: PaymentSettingsEntity?,
    myRecharges: List<RechargeRequestEntity>,
    onSubmitRecharge: (Double, String, String) -> Unit
) {
    val clipboardManager = LocalClipboardManager.current

    var amountText by remember { mutableStateOf("100") }
    var utrNumber by remember { mutableStateOf("") }
    var screenshotPath by remember { mutableStateOf("") }

    val upiId = paymentSettings?.upiId ?: "onetappanel@upi"
    val receiverName = paymentSettings?.receiverName ?: "One Tap Panel Official"
    val instructions = paymentSettings?.instructions ?: "Scan QR Code using PhonePe, GPay or Paytm. Enter UTR number below."

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "RECHARGE WALLET",
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            color = NeonBluePrimary
        )
        Text(
            text = "Scan QR or pay via UPI ID and submit UTR for instant admin approval",
            fontSize = 12.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // QR & Payment Method Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Mock/Real Visual QR Code Box
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .border(2.dp, NeonBluePrimary, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.QrCode2, contentDescription = "QR Code", tint = Color.Black, modifier = Modifier.size(110.dp))
                        Text("SCAN & PAY", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.Black)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CardBackground)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("PAYMENT UPI ID", fontSize = 10.sp, color = TextMuted)
                        Text(upiId, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NeonBluePrimary)
                    }

                    IconButton(onClick = {
                        clipboardManager.setText(AnnotatedString(upiId))
                    }) {
                        Icon(Icons.Filled.ContentCopy, contentDescription = "Copy UPI", tint = NeonBluePrimary)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Receiver: $receiverName",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = instructions,
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Submit Payment Form
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "SUBMIT RECHARGE DETAILS",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonBluePrimary
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter { ch -> ch.isDigit() || ch == '.' } },
                    label = { Text("Recharge Amount ($selectedCurrency)", color = TextSecondary) },
                    leadingIcon = { Icon(Icons.Filled.AttachMoney, contentDescription = null, tint = StatusCompleted) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonBluePrimary, unfocusedBorderColor = CardBorder,
                        focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = utrNumber,
                    onValueChange = { utrNumber = it.trim() },
                    label = { Text("UTR / Ref / Transaction Number", color = TextSecondary) },
                    placeholder = { Text("12-digit UTR e.g. 320984920194", color = TextMuted) },
                    leadingIcon = { Icon(Icons.Filled.Numbers, contentDescription = null, tint = NeonBluePrimary) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonBluePrimary, unfocusedBorderColor = CardBorder,
                        focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = { screenshotPath = "screenshot_attached.png" },
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                ) {
                    Icon(
                        imageVector = if (screenshotPath.isBlank()) Icons.Filled.AddPhotoAlternate else Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = if (screenshotPath.isBlank()) NeonBluePrimary else StatusCompleted
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (screenshotPath.isBlank()) "Attach Screenshot (Optional)" else "Screenshot Attached ✓",
                        color = TextPrimary,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        val amount = amountText.toDoubleOrNull() ?: 0.0
                        if (amount > 0 && utrNumber.isNotBlank()) {
                            onSubmitRecharge(amount, utrNumber, screenshotPath)
                            utrNumber = ""
                        }
                    },
                    enabled = (amountText.toDoubleOrNull() ?: 0.0) > 0 && utrNumber.isNotBlank(),
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonBluePrimary)
                ) {
                    Text("SUBMIT RECHARGE REQUEST", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 13.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Recharge Requests Queue History
        Text("RECHARGE HISTORY", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextSecondary, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(10.dp))

        if (myRecharges.isEmpty()) {
            Text("No recharge requests found.", fontSize = 12.sp, color = TextMuted)
        } else {
            myRecharges.forEach { req ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Req #${req.rechargeIdString}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NeonBluePrimary)
                            Text("UTR: ${req.uTrNumber}", fontSize = 12.sp, color = TextSecondary)
                            Text("Amount: ${CurrencyUtils.format(req.amount, selectedCurrency)}", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = StatusCompleted)
                            Text(req.createdAt, fontSize = 10.sp, color = TextMuted)
                        }

                        StatusChip(status = req.status)
                    }
                }
            }
        }
    }
}
