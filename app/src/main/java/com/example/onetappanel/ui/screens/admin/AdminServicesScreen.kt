package com.example.onetappanel.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
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
import com.example.ui.theme.*
import com.example.onetappanel.util.CurrencyUtils

@Composable
fun AdminServicesScreen(
    categories: List<ServiceCategoryEntity>,
    services: List<ServiceEntity>,
    selectedCurrency: String,
    onSaveServiceSubmit: (ServiceEntity) -> Unit,
    onDeleteServiceSubmit: (Long) -> Unit,
    onSaveCategorySubmit: (ServiceCategoryEntity) -> Unit
) {
    var addServiceDialogVisible by remember { mutableStateOf(false) }
    var addCategoryDialogVisible by remember { mutableStateOf(false) }

    var serviceName by remember { mutableStateOf("") }
    var serviceRate by remember { mutableStateOf("150") }
    var serviceMin by remember { mutableStateOf("100") }
    var serviceMax by remember { mutableStateOf("100000") }
    var serviceDesc by remember { mutableStateOf("Instant start high quality service") }
    var selectedCat by remember { mutableStateOf<ServiceCategoryEntity?>(categories.firstOrNull()) }

    var newCategoryName by remember { mutableStateOf("") }

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
                Text("SERVICES MANAGER", fontSize = 20.sp, fontWeight = FontWeight.Black, color = AccentCyan)
                Text("Manage categories and set rate per 1,000", fontSize = 12.sp, color = TextSecondary)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Button(
                    onClick = { addCategoryDialogVisible = true },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlueSecondary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("+ Category", fontSize = 11.sp, color = Color.White)
                }

                Button(
                    onClick = { addServiceDialogVisible = true },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonBluePrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("+ Service", fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(services, key = { it.id }) { srv ->
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
                        Column(modifier = Modifier.weight(1f)) {
                            Text("ID: ${srv.serviceIdString} | ${srv.categoryName}", fontSize = 10.sp, color = NeonBluePrimary, fontWeight = FontWeight.Bold)
                            Text(srv.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("Rate: ${CurrencyUtils.format(srv.ratePer1000, selectedCurrency)} / 1k | Min: ${srv.minQuantity} Max: ${srv.maxQuantity}", fontSize = 12.sp, color = StatusCompleted, fontWeight = FontWeight.SemiBold)
                        }

                        IconButton(onClick = { onDeleteServiceSubmit(srv.id) }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = ErrorRed)
                        }
                    }
                }
            }
        }
    }

    if (addServiceDialogVisible) {
        AlertDialog(
            onDismissRequest = { addServiceDialogVisible = false },
            title = { Text("Add New Service", fontWeight = FontWeight.Bold, color = NeonBluePrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = serviceName,
                        onValueChange = { serviceName = it },
                        label = { Text("Service Name", color = TextSecondary) },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = serviceRate,
                        onValueChange = { serviceRate = it },
                        label = { Text("Rate per 1,000 (INR)", color = TextSecondary) },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = serviceMin,
                        onValueChange = { serviceMin = it },
                        label = { Text("Min Quantity", color = TextSecondary) },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = serviceMax,
                        onValueChange = { serviceMax = it },
                        label = { Text("Max Quantity", color = TextSecondary) },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val cat = selectedCat ?: categories.firstOrNull() ?: return@Button
                        val rate = serviceRate.toDoubleOrNull() ?: 100.0
                        val min = serviceMin.toIntOrNull() ?: 100
                        val max = serviceMax.toIntOrNull() ?: 100000
                        val serviceIdString = "${cat.id * 100 + (services.size + 1)}"

                        onSaveServiceSubmit(
                            ServiceEntity(
                                serviceIdString = serviceIdString,
                                categoryId = cat.id,
                                categoryName = cat.name,
                                name = serviceName,
                                ratePer1000 = rate,
                                minQuantity = min,
                                maxQuantity = max,
                                description = serviceDesc
                            )
                        )
                        addServiceDialogVisible = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonBluePrimary)
                ) {
                    Text("Save Service", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { addServiceDialogVisible = false }) { Text("Cancel", color = TextSecondary) }
            },
            containerColor = SurfaceDark
        )
    }

    if (addCategoryDialogVisible) {
        AlertDialog(
            onDismissRequest = { addCategoryDialogVisible = false },
            title = { Text("Add New Category", fontWeight = FontWeight.Bold, color = ElectricBlueSecondary) },
            text = {
                OutlinedTextField(
                    value = newCategoryName,
                    onValueChange = { newCategoryName = it },
                    label = { Text("Category Name", color = TextSecondary) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newCategoryName.isNotBlank()) {
                            onSaveCategorySubmit(
                                ServiceCategoryEntity(
                                    categoryIdString = "CAT-${categories.size + 1}",
                                    name = newCategoryName,
                                    iconName = "category",
                                    displayOrder = categories.size + 1
                                )
                            )
                            addCategoryDialogVisible = false
                            newCategoryName = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlueSecondary)
                ) {
                    Text("Save Category", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { addCategoryDialogVisible = false }) { Text("Cancel", color = TextSecondary) }
            },
            containerColor = SurfaceDark
        )
    }
}
