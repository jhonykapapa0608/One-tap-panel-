package com.example.onetappanel.ui.screens.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onetappanel.data.model.ServiceCategoryEntity
import com.example.onetappanel.data.model.ServiceEntity
import com.example.onetappanel.data.model.UserEntity
import com.example.ui.theme.*
import com.example.onetappanel.util.CurrencyUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewOrderScreen(
    user: UserEntity,
    selectedCurrency: String,
    categories: List<ServiceCategoryEntity>,
    allServices: List<ServiceEntity>,
    preselectedService: ServiceEntity?,
    onPlaceOrderSubmit: (ServiceEntity, String, Int, String) -> Unit,
    onNavigateToAddFunds: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf<ServiceCategoryEntity?>(categories.firstOrNull()) }
    var selectedService by remember { mutableStateOf<ServiceEntity?>(preselectedService ?: allServices.firstOrNull()) }

    var categoryDropdownExpanded by remember { mutableStateOf(false) }
    var serviceDropdownExpanded by remember { mutableStateOf(false) }

    var targetLink by remember { mutableStateOf("") }
    var quantityText by remember { mutableStateOf("1000") }
    var couponCode by remember { mutableStateOf("") }

    val filteredServices = remember(selectedCategory, allServices) {
        if (selectedCategory != null) {
            allServices.filter { it.categoryId == selectedCategory?.id }
        } else {
            allServices
        }
    }

    LaunchedEffect(selectedCategory) {
        if (selectedService?.categoryId != selectedCategory?.id) {
            selectedService = filteredServices.firstOrNull()
        }
    }

    val quantity = quantityText.toIntOrNull() ?: 0
    val currentRatePer1000 = selectedService?.ratePer1000 ?: 0.0
    val basePriceINR = (quantity / 1000.0) * currentRatePer1000

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "PLACE NEW ORDER",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = NeonBluePrimary
            )
            Text(
                text = "Select service, enter link & quantity for automated instant start",
                fontSize = 12.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Wallet Balance Warning Badge
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.AccountBalanceWallet, contentDescription = null, tint = NeonBluePrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Available Wallet Balance", fontSize = 10.sp, color = TextMuted)
                            Text(CurrencyUtils.format(user.walletBalance, selectedCurrency), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                    }
                    TextButton(onClick = onNavigateToAddFunds) {
                        Text("Add Funds", color = NeonBluePrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    // Category Selector
                    Text("1. CATEGORY", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                    Spacer(modifier = Modifier.height(6.dp))

                    ExposedDropdownMenuBox(
                        expanded = categoryDropdownExpanded,
                        onExpandedChange = { categoryDropdownExpanded = !categoryDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedCategory?.name ?: "Select Category",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonBluePrimary, unfocusedBorderColor = CardBorder,
                                focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        ExposedDropdownMenu(
                            expanded = categoryDropdownExpanded,
                            onDismissRequest = { categoryDropdownExpanded = false },
                            modifier = Modifier.background(SurfaceDark)
                        ) {
                            categories.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat.name, color = TextPrimary) },
                                    onClick = {
                                        selectedCategory = cat
                                        categoryDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Service Selector
                    Text("2. SERVICE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                    Spacer(modifier = Modifier.height(6.dp))

                    ExposedDropdownMenuBox(
                        expanded = serviceDropdownExpanded,
                        onExpandedChange = { serviceDropdownExpanded = !serviceDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedService?.name ?: "Select Service",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = serviceDropdownExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonBluePrimary, unfocusedBorderColor = CardBorder,
                                focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        ExposedDropdownMenu(
                            expanded = serviceDropdownExpanded,
                            onDismissRequest = { serviceDropdownExpanded = false },
                            modifier = Modifier.background(SurfaceDark)
                        ) {
                            filteredServices.forEach { srv ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(srv.name, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 13.sp)
                                            Text("${CurrencyUtils.format(srv.ratePer1000, selectedCurrency)} per 1k", color = NeonBluePrimary, fontSize = 11.sp)
                                        }
                                    },
                                    onClick = {
                                        selectedService = srv
                                        serviceDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Service Details Banner
                    selectedService?.let { srv ->
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(CardBackground, RoundedCornerShape(10.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Text("Service Description:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NeonBluePrimary)
                                Text(srv.description.ifBlank { "Fast instant start delivery." }, fontSize = 12.sp, color = TextSecondary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Min: ${srv.minQuantity} | Max: ${srv.maxQuantity}", fontSize = 11.sp, color = TextMuted)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Target Link Input
                    Text("3. TARGET LINK", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = targetLink,
                        onValueChange = { targetLink = it },
                        placeholder = { Text("https://instagram.com/username or link", color = TextMuted) },
                        leadingIcon = { Icon(Icons.Filled.Link, contentDescription = null, tint = NeonBluePrimary) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonBluePrimary, unfocusedBorderColor = CardBorder,
                            focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Quantity Input
                    Text("4. QUANTITY", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = quantityText,
                        onValueChange = { quantityText = it.filter { ch -> ch.isDigit() } },
                        leadingIcon = { Icon(Icons.Filled.FormatListNumbered, contentDescription = null, tint = NeonBluePrimary) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonBluePrimary, unfocusedBorderColor = CardBorder,
                            focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Coupon Code Input
                    Text("COUPON CODE (OPTIONAL)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = couponCode,
                        onValueChange = { couponCode = it.uppercase().trim() },
                        placeholder = { Text("Enter coupon code e.g. ONETAP10", color = TextMuted) },
                        leadingIcon = { Icon(Icons.Filled.LocalOffer, contentDescription = null, tint = StatusPending) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = StatusPending, unfocusedBorderColor = CardBorder,
                            focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Live Total Price Banner
                    HorizontalDivider(color = SurfaceVariantDark)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("TOTAL AMOUNT DUE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                            Text(
                                text = CurrencyUtils.format(basePriceINR, selectedCurrency),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = StatusCompleted
                            )
                        }

                        Button(
                            onClick = {
                                selectedService?.let { srv ->
                                    onPlaceOrderSubmit(srv, targetLink, quantity, couponCode)
                                }
                            },
                            enabled = selectedService != null && targetLink.isNotBlank() && quantity > 0,
                            modifier = Modifier.height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonBluePrimary)
                        ) {
                            Text("PLACE ORDER NOW", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}
