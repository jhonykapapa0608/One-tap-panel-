package com.example.onetappanel.ui.screens.user

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onetappanel.data.model.SupportTicketEntity
import com.example.onetappanel.ui.components.StatusChip
import com.example.ui.theme.*

@Composable
fun SupportTicketsScreen(
    tickets: List<SupportTicketEntity>,
    onCreateTicketSubmit: (String, String, String, String) -> Unit,
    onSelectTicket: (SupportTicketEntity) -> Unit
) {
    var createDialogVisible by remember { mutableStateOf(false) }

    var subject by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Order") }
    var priority by remember { mutableStateOf("Normal") }
    var message by remember { mutableStateOf("") }

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
                    text = "SUPPORT TICKETS",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = NeonBluePrimary
                )
                Text(
                    text = "24/7 dedicated customer assistance",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            Button(
                onClick = { createDialogVisible = true },
                colors = ButtonDefaults.buttonColors(containerColor = NeonBluePrimary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = null, tint = Color.Black)
                Spacer(modifier = Modifier.width(4.dp))
                Text("New Ticket", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (tickets.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.SupportAgent, contentDescription = null, tint = TextMuted, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No support tickets created yet.", color = TextSecondary, fontSize = 13.sp)
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(tickets, key = { it.id }) { ticket ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectTicket(ticket) },
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
                                Text("#${ticket.ticketIdString} - ${ticket.subject}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                StatusChip(status = ticket.status)
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text("Category: ${ticket.category} | Priority: ${ticket.priority}", fontSize = 11.sp, color = TextMuted)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Last message: ${ticket.lastMessage}", fontSize = 12.sp, color = TextSecondary, maxLines = 1)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Updated: ${ticket.updatedAt}", fontSize = 10.sp, color = TextMuted)
                        }
                    }
                }
            }
        }
    }

    if (createDialogVisible) {
        AlertDialog(
            onDismissRequest = { createDialogVisible = false },
            title = { Text("Create Support Ticket", fontWeight = FontWeight.Bold, color = NeonBluePrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = subject,
                        onValueChange = { subject = it },
                        label = { Text("Subject", color = TextSecondary) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = message,
                        onValueChange = { message = it },
                        label = { Text("Describe your issue...", color = TextSecondary) },
                        modifier = Modifier.fillMaxWidth().height(100.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (subject.isNotBlank() && message.isNotBlank()) {
                            onCreateTicketSubmit(subject, category, priority, message)
                            createDialogVisible = false
                            subject = ""
                            message = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonBluePrimary)
                ) {
                    Text("Submit Ticket", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { createDialogVisible = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = SurfaceDark
        )
    }
}
