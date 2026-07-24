package com.example.onetappanel.ui.screens.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onetappanel.data.model.ServiceCategoryEntity
import com.example.onetappanel.data.model.ServiceEntity
import com.example.onetappanel.ui.components.ServiceCard
import com.example.ui.theme.*

@Composable
fun ServicesListScreen(
    categories: List<ServiceCategoryEntity>,
    services: List<ServiceEntity>,
    favouriteServiceIds: List<Long>,
    selectedCurrency: String,
    onFavouriteToggle: (Long) -> Unit,
    onSelectServiceToOrder: (ServiceEntity) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf<Long?>(null) }
    var showOnlyFavorites by remember { mutableStateOf(false) }

    val filteredServices = remember(searchQuery, selectedCategoryId, showOnlyFavorites, services, favouriteServiceIds) {
        services.filter { srv ->
            val matchesCategory = selectedCategoryId == null || srv.categoryId == selectedCategoryId
            val matchesSearch = searchQuery.isBlank() ||
                    srv.name.contains(searchQuery, ignoreCase = true) ||
                    srv.serviceIdString.contains(searchQuery, ignoreCase = true) ||
                    srv.categoryName.contains(searchQuery, ignoreCase = true)
            val matchesFav = !showOnlyFavorites || favouriteServiceIds.contains(srv.id)

            matchesCategory && matchesSearch && matchesFav
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(16.dp)
    ) {
        Text(
            text = "SERVICES CATALOG",
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            color = NeonBluePrimary
        )
        Text(
            text = "Browse high quality social media growth services",
            fontSize = 12.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search services by name or ID...", color = TextMuted) },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = NeonBluePrimary) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Filled.Close, contentDescription = null, tint = TextMuted)
                    }
                }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonBluePrimary, unfocusedBorderColor = CardBorder,
                focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Category Filter Pills + Favorites Toggle
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            item {
                FilterChip(
                    selected = selectedCategoryId == null && !showOnlyFavorites,
                    onClick = {
                        selectedCategoryId = null
                        showOnlyFavorites = false
                    },
                    label = { Text("All Services") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = NeonBluePrimary,
                        selectedLabelColor = androidx.compose.ui.graphics.Color.Black
                    )
                )
            }

            item {
                FilterChip(
                    selected = showOnlyFavorites,
                    onClick = {
                        showOnlyFavorites = !showOnlyFavorites
                        selectedCategoryId = null
                    },
                    label = { Text("⭐ Favorites (${favouriteServiceIds.size})") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = StatusPending,
                        selectedLabelColor = androidx.compose.ui.graphics.Color.Black
                    )
                )
            }

            items(categories) { cat ->
                FilterChip(
                    selected = selectedCategoryId == cat.id && !showOnlyFavorites,
                    onClick = {
                        selectedCategoryId = if (selectedCategoryId == cat.id) null else cat.id
                        showOnlyFavorites = false
                    },
                    label = { Text(cat.name) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ElectricBlueSecondary,
                        selectedLabelColor = androidx.compose.ui.graphics.Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Showing ${filteredServices.size} services",
            fontSize = 11.sp,
            color = TextMuted
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredServices, key = { it.id }) { service ->
                val isFav = favouriteServiceIds.contains(service.id)
                ServiceCard(
                    service = service,
                    selectedCurrency = selectedCurrency,
                    isFavourite = isFav,
                    onFavouriteToggle = { onFavouriteToggle(service.id) },
                    onSelectService = onSelectServiceToOrder
                )
            }
        }
    }
}
