package com.example.onetappanel.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onetappanel.data.model.UserEntity
import com.example.ui.theme.*
import com.example.onetappanel.util.CurrencyUtils

@Composable
fun AdminUsersScreen(
    users: List<UserEntity>,
    selectedCurrency: String,
    onCreditBalanceSubmit: (Long, Double, String) -> Unit,
    onToggleUserBlock: (UserEntity) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedUserForCredit by remember { mutableStateOf<UserEntity?>(null) }
    var creditAmountText by remember { mutableStateOf("100") }
    var creditNote by remember { mutableStateOf("Bonus / Admin adjustment") }

    val filteredUsers = remember(searchQuery, users) {
        users.filter { u ->
            searchQuery.isBlank() ||
                    u.fullName.contains(searchQuery, ignoreCase = true) ||
                    u.username.contains(searchQuery, ignoreCase = true) ||
                    u.email.contains(searchQuery, ignoreCase = true) ||
                    u.phoneNumber.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(16.dp)
    ) {
        Text("MANAGE USERS", fontSize = 20.sp, fontWeight = FontWeight.Black, color = NeonBluePrimary)
        Text("Total ${users.size} registered users in database", fontSize = 12.sp, color = TextSecondary)

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search by name, username, email...", color = TextMuted) },
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

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(filteredUsers, key = { it.id }) { u ->
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
                                Text(u.fullName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text("@${u.username} (${u.email})", fontSize = 12.sp, color = NeonBluePrimary)
                                Text("Phone: ${u.phoneNumber} | Joined: ${u.joinDate}", fontSize = 10.sp, color = TextMuted)
                            }

                            if (u.isBlocked) {
                                Surface(shape = RoundedCornerShape(6.dp), color = StatusCancelled.copy(alpha = 0.2f)) {
                                    Text("BLOCKED", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = StatusCancelled, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = SurfaceVariantDark)
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Balance: ${CurrencyUtils.format(u.walletBalance, selectedCurrency)}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = StatusCompleted
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Button(
                                    onClick = { selectedUserForCredit = u },
                                    colors = ButtonDefaults.buttonColors(containerColor = StatusCompleted),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Icon(Icons.Filled.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                                    Text("Add Funds", fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = { onToggleUserBlock(u) },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.height(32.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (u.isBlocked) StatusCompleted else StatusCancelled)
                                ) {
                                    Icon(Icons.Filled.Block, contentDescription = null, tint = if (u.isBlocked) StatusCompleted else StatusCancelled, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(if (u.isBlocked) "Unblock" else "Block", fontSize = 11.sp, color = if (u.isBlocked) StatusCompleted else StatusCancelled)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    selectedUserForCredit?.let { targetUser ->
        AlertDialog(
            onDismissRequest = { selectedUserForCredit = null },
            title = { Text("Credit Funds to @${targetUser.username}", fontWeight = FontWeight.Bold, color = NeonBluePrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = creditAmountText,
                        onValueChange = { creditAmountText = it.filter { ch -> ch.isDigit() || ch == '.' } },
                        label = { Text("Amount ($selectedCurrency)", color = TextSecondary) },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = creditNote,
                        onValueChange = { creditNote = it },
                        label = { Text("Admin Note / Reason", color = TextSecondary) },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = creditAmountText.toDoubleOrNull() ?: 0.0
                        if (amount > 0) {
                            onCreditBalanceSubmit(targetUser.id, amount, creditNote)
                            selectedUserForCredit = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonBluePrimary)
                ) {
                    Text("Confirm Credit", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedUserForCredit = null }) { Text("Cancel", color = TextSecondary) }
            },
            containerColor = SurfaceDark
        )
    }
}
