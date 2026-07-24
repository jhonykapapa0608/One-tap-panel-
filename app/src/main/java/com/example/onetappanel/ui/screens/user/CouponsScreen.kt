package com.example.onetappanel.ui.screens.user

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onetappanel.data.model.CouponEntity
import com.example.ui.theme.*
import com.example.onetappanel.util.CurrencyUtils

@Composable
fun CouponsScreen(
    coupons: List<CouponEntity>,
    selectedCurrency: String,
    onCopyToast: (String) -> Unit
) {
    val clipboardManager = LocalClipboardManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(16.dp)
    ) {
        Text(
            text = "DISCOUNT COUPONS",
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            color = NeonBluePrimary
        )
        Text(
            text = "Apply valid coupon codes on new orders for instant discounts",
            fontSize = 12.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (coupons.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No active coupons available right now.", color = TextMuted, fontSize = 13.sp)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(coupons, key = { it.id }) { coupon ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = CardBackground),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.LocalOffer, contentDescription = null, tint = StatusPending)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(coupon.code, fontSize = 18.sp, fontWeight = FontWeight.Black, color = NeonBluePrimary)
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                val discountText = if (coupon.discountPercent > 0) "${coupon.discountPercent.toInt()}% OFF" else "${CurrencyUtils.format(coupon.discountAmount, selectedCurrency)} OFF"
                                Text(discountText, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = StatusCompleted)

                                Text(
                                    "Min order: ${CurrencyUtils.format(coupon.minOrderAmount, selectedCurrency)} | Valid till ${coupon.expiryDate}",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }

                            Button(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(coupon.code))
                                    onCopyToast("Coupon code ${coupon.code} copied!")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = StatusPending),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Filled.ContentCopy, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("COPY", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
