package com.example.onetappanel.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onetappanel.data.model.WebsiteSettingsEntity
import com.example.ui.theme.*

@Composable
fun AdminWebsiteSettingsScreen(
    currentSettings: WebsiteSettingsEntity?,
    onSaveWebsiteSettingsSubmit: (WebsiteSettingsEntity) -> Unit,
    onBroadcastNotificationSubmit: (String, String, String) -> Unit,
    onExportBackupClick: () -> Unit
) {
    var siteName by remember { mutableStateOf(currentSettings?.siteName ?: "One Tap Panel") }
    var whatsappNumber by remember { mutableStateOf(currentSettings?.whatsappSupportNumber ?: "919999999999") }
    var telegramLink by remember { mutableStateOf(currentSettings?.telegramLink ?: "https://t.me/onetappanel") }
    var isMaintenance by remember { mutableStateOf(currentSettings?.isMaintenanceMode ?: false) }

    var notifTitle by remember { mutableStateOf("") }
    var notifMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("WEBSITE & BACKUP SETTINGS", fontSize = 20.sp, fontWeight = FontWeight.Black, color = TextPrimary)
        Text("Configure contact links, broadcast announcements & backup system", fontSize = 12.sp, color = TextSecondary)

        Spacer(modifier = Modifier.height(16.dp))

        // Branding & Contact Settings
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("BRANDING & SUPPORT LINKS", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NeonBluePrimary)
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = siteName,
                    onValueChange = { siteName = it },
                    label = { Text("Website Name", color = TextSecondary) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = whatsappNumber,
                    onValueChange = { whatsappNumber = it },
                    label = { Text("WhatsApp Support Number (with Country Code)", color = TextSecondary) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = telegramLink,
                    onValueChange = { telegramLink = it },
                    label = { Text("Telegram Group / Channel Link", color = TextSecondary) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Maintenance Mode", color = TextPrimary, fontWeight = FontWeight.Bold)
                    Switch(
                        checked = isMaintenance,
                        onCheckedChange = { isMaintenance = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = NeonBluePrimary)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        onSaveWebsiteSettingsSubmit(
                            WebsiteSettingsEntity(
                                id = currentSettings?.id ?: 1,
                                siteName = siteName,
                                whatsappSupportNumber = whatsappNumber,
                                telegramLink = telegramLink,
                                isMaintenanceMode = isMaintenance
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonBluePrimary)
                ) {
                    Text("SAVE WEBSITE SETTINGS", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Broadcast Notification
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("BROADCAST SYSTEM ANNOUNCEMENT", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = StatusPending)
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = notifTitle,
                    onValueChange = { notifTitle = it },
                    label = { Text("Notification Title", color = TextSecondary) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = notifMessage,
                    onValueChange = { notifMessage = it },
                    label = { Text("Announcement Message...", color = TextSecondary) },
                    modifier = Modifier.fillMaxWidth().height(80.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (notifTitle.isNotBlank() && notifMessage.isNotBlank()) {
                            onBroadcastNotificationSubmit(notifTitle, notifMessage, "ANNOUNCEMENT")
                            notifTitle = ""
                            notifMessage = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = StatusPending)
                ) {
                    Text("SEND BROADCAST TO ALL USERS", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Backup Export
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("DATABASE BACKUP & RESTORE", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = StatusCompleted)
                Text("Export full system database as encrypted JSON backup file", fontSize = 11.sp, color = TextMuted)
                Spacer(modifier = Modifier.height(14.dp))

                OutlinedButton(
                    onClick = onExportBackupClick,
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, StatusCompleted)
                ) {
                    Text("EXPORT FULL DATABASE BACKUP JSON", color = StatusCompleted, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
