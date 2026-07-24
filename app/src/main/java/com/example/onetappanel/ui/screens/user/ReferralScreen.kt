package com.example.onetappanel.ui.screens.user

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
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
import com.example.onetappanel.data.model.UserEntity
import com.example.ui.theme.*

@Composable
fun ReferralScreen(
    user: UserEntity,
    selectedCurrency: String,
    onCopyToast: (String) -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    val refLink = "https://onetappanel.com/register?ref=${user.referralCode}"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "REFERRAL PROGRAM",
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            color = NeonBluePrimary
        )
        Text(
            text = "Invite friends and earn lifetime commission on every deposit!",
            fontSize = 12.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

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
                Icon(Icons.Filled.CardGiftcard, contentDescription = null, tint = StatusCompleted, modifier = Modifier.size(54.dp))

                Spacer(modifier = Modifier.height(12.dp))

                Text("YOUR UNIQUE REFERRAL CODE", fontSize = 11.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(user.referralCode, fontSize = 24.sp, fontWeight = FontWeight.Black, color = NeonBluePrimary)

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardBackground, RoundedCornerShape(12.dp))
                        .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text("REFERRAL LINK", fontSize = 10.sp, color = TextMuted)
                        Text(refLink, fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(refLink))
                            onCopyToast("Referral link copied!")
                        },
                        modifier = Modifier.weight(1f).height(46.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonBluePrimary)
                    ) {
                        Icon(Icons.Filled.ContentCopy, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("COPY LINK", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString("Join One Tap Panel using my code ${user.referralCode}: $refLink"))
                            onCopyToast("Shared text copied!")
                        },
                        modifier = Modifier.weight(1f).height(46.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                    ) {
                        Icon(Icons.Filled.Share, contentDescription = null, tint = NeonBluePrimary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("SHARE", color = TextPrimary, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
