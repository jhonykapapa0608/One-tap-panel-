package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.onetappanel.data.model.OrderEntity
import com.example.onetappanel.data.model.ServiceEntity
import com.example.onetappanel.ui.components.*
import com.example.onetappanel.ui.screens.admin.*
import com.example.onetappanel.ui.screens.auth.*
import com.example.onetappanel.ui.screens.user.*
import com.example.ui.theme.OneTapPanelTheme
import com.example.onetappanel.util.WhatsappUtils
import com.example.onetappanel.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            OneTapPanelTheme {
                val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
                val selectedCurrency by viewModel.selectedCurrency.collectAsStateWithLifecycle()
                val currentRoute by viewModel.currentRoute.collectAsStateWithLifecycle()

                val categories by viewModel.categories.collectAsStateWithLifecycle()
                val activeServices by viewModel.activeServices.collectAsStateWithLifecycle()
                val popularServices by viewModel.popularServices.collectAsStateWithLifecycle()
                val myOrders by viewModel.myOrders.collectAsStateWithLifecycle()
                val myRecharges by viewModel.myRecharges.collectAsStateWithLifecycle()
                val myTickets by viewModel.myTickets.collectAsStateWithLifecycle()
                val myNotifications by viewModel.myNotifications.collectAsStateWithLifecycle()
                val favServiceIds by viewModel.favouriteServiceIds.collectAsStateWithLifecycle()

                val paymentSettings by viewModel.paymentSettings.collectAsStateWithLifecycle()
                val websiteSettings by viewModel.websiteSettings.collectAsStateWithLifecycle()

                val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()
                val allOrders by viewModel.allOrders.collectAsStateWithLifecycle()
                val allRecharges by viewModel.allRecharges.collectAsStateWithLifecycle()
                val allServices by viewModel.allServices.collectAsStateWithLifecycle()
                val allCoupons by viewModel.allCoupons.collectAsStateWithLifecycle()

                var preselectedServiceForOrder by remember { mutableStateOf<ServiceEntity?>(null) }
                var confirmedOrderDialogState by remember { mutableStateOf<OrderEntity?>(null) }

                val adminPhone = websiteSettings?.whatsappSupportNumber ?: "919999999999"

                // Observe Toast Events
                LaunchedEffect(Unit) {
                    viewModel.uiEvent.collect { message ->
                        Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
                    }
                }

                val showBottomNav = currentUser != null && currentRoute !in listOf("login", "register", "forgot_password")

                Scaffold(
                    topBar = {
                        if (currentUser != null && currentRoute !in listOf("login", "register", "forgot_password")) {
                            SmmTopAppBar(
                                title = websiteSettings?.siteName ?: "One Tap Panel",
                                currentUser = currentUser,
                                selectedCurrency = selectedCurrency,
                                onCurrencySelect = { viewModel.setCurrency(it) },
                                onNotificationClick = { viewModel.setRoute("notifications") },
                                onAdminToggle = {
                                    if (currentRoute.startsWith("admin_")) {
                                        viewModel.setRoute("user_dashboard")
                                    } else {
                                        viewModel.setRoute("admin_dashboard")
                                    }
                                },
                                onLogoutClick = { viewModel.logout() }
                            )
                        }
                    },
                    bottomBar = {
                        if (showBottomNav) {
                            SmmBottomNavBar(
                                currentRoute = currentRoute,
                                userRole = currentUser?.role ?: "USER",
                                onNavigate = { viewModel.setRoute(it) }
                            )
                        }
                    },
                    floatingActionButton = {
                        if (currentUser != null) {
                            FloatingWhatsappButton(
                                onClick = {
                                    WhatsappUtils.openGeneralSupportChat(
                                        context = this@MainActivity,
                                        adminPhoneNumber = adminPhone,
                                        username = currentUser?.username ?: "User"
                                    )
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        AnimatedContent(
                            targetState = currentRoute,
                            label = "ScreenTransition"
                        ) { targetRoute ->
                            when (targetRoute) {
                                "login" -> LoginScreen(
                                    onLoginSubmit = { emailOrUsername, password ->
                                        viewModel.login(emailOrUsername, password) { success, msg ->
                                            Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    onGoogleLoginClick = {
                                        // Demo Google Login Auto-Session
                                        viewModel.login("user", "user123") { success, msg ->
                                            Toast.makeText(this@MainActivity, "Google Auth: $msg", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    onNavigateToRegister = { viewModel.setRoute("register") },
                                    onNavigateToForgotPassword = { viewModel.setRoute("forgot_password") }
                                )

                                "register" -> RegisterScreen(
                                    onRegisterSubmit = { fullName, username, email, phone, password, refCode ->
                                        viewModel.register(fullName, username, email, phone, password, refCode) { success, msg ->
                                            Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    onNavigateToLogin = { viewModel.setRoute("login") }
                                )

                                "forgot_password" -> ForgotPasswordScreen(
                                    onResetSubmit = { email ->
                                        Toast.makeText(this@MainActivity, "Password reset instructions sent to $email", Toast.LENGTH_LONG).show()
                                        viewModel.setRoute("login")
                                    },
                                    onBackToLogin = { viewModel.setRoute("login") }
                                )

                                "user_dashboard" -> currentUser?.let { user ->
                                    UserDashboardScreen(
                                        user = user,
                                        selectedCurrency = selectedCurrency,
                                        orders = myOrders,
                                        popularServices = popularServices,
                                        onNavigate = { viewModel.setRoute(it) },
                                        onSelectService = { srv ->
                                            preselectedServiceForOrder = srv
                                            viewModel.setRoute("new_order")
                                        },
                                        onWhatsappOrderShare = { order ->
                                            WhatsappUtils.openOrderWhatsAppChat(
                                                context = this@MainActivity,
                                                adminPhoneNumber = adminPhone,
                                                orderId = order.orderIdString,
                                                serviceName = order.serviceName,
                                                link = order.link,
                                                quantity = order.quantity,
                                                price = order.price,
                                                currency = selectedCurrency
                                            )
                                        }
                                    )
                                }

                                "new_order" -> currentUser?.let { user ->
                                    NewOrderScreen(
                                        user = user,
                                        selectedCurrency = selectedCurrency,
                                        categories = categories,
                                        allServices = activeServices,
                                        preselectedService = preselectedServiceForOrder,
                                        onPlaceOrderSubmit = { srv, link, qty, coupon ->
                                            viewModel.placeOrder(srv, link, qty, coupon) { success, order, msg ->
                                                Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
                                                if (success && order != null) {
                                                    confirmedOrderDialogState = order
                                                }
                                            }
                                        },
                                        onNavigateToAddFunds = { viewModel.setRoute("add_funds") }
                                    )
                                }

                                "services" -> ServicesListScreen(
                                    categories = categories,
                                    services = activeServices,
                                    favouriteServiceIds = favServiceIds,
                                    selectedCurrency = selectedCurrency,
                                    onFavouriteToggle = { serviceId -> viewModel.toggleFavourite(serviceId) },
                                    onSelectServiceToOrder = { srv ->
                                        preselectedServiceForOrder = srv
                                        viewModel.setRoute("new_order")
                                    }
                                )

                                "my_orders" -> MyOrdersScreen(
                                    orders = myOrders,
                                    selectedCurrency = selectedCurrency,
                                    onWhatsappOrderShare = { order ->
                                        WhatsappUtils.openOrderWhatsAppChat(
                                            context = this@MainActivity,
                                            adminPhoneNumber = adminPhone,
                                            orderId = order.orderIdString,
                                            serviceName = order.serviceName,
                                            link = order.link,
                                            quantity = order.quantity,
                                            price = order.price,
                                            currency = selectedCurrency
                                        )
                                    },
                                    onNewOrderClick = { viewModel.setRoute("new_order") }
                                )

                                "add_funds" -> currentUser?.let { user ->
                                    AddFundsScreen(
                                        user = user,
                                        selectedCurrency = selectedCurrency,
                                        paymentSettings = paymentSettings,
                                        myRecharges = myRecharges,
                                        onSubmitRecharge = { amount, utr, screenshot ->
                                            viewModel.submitRecharge(amount, utr, screenshot) { success, msg ->
                                                Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    )
                                }

                                "coupons" -> CouponsScreen(
                                    coupons = allCoupons,
                                    selectedCurrency = selectedCurrency,
                                    onCopyToast = { viewModel.emitToast(it) }
                                )

                                "referral" -> currentUser?.let { user ->
                                    ReferralScreen(
                                        user = user,
                                        selectedCurrency = selectedCurrency,
                                        onCopyToast = { viewModel.emitToast(it) }
                                    )
                                }

                                "support" -> SupportTicketsScreen(
                                    tickets = myTickets,
                                    onCreateTicketSubmit = { subj, cat, prio, msg ->
                                        viewModel.createSupportTicket(subj, cat, prio, msg) { success, toast ->
                                            Toast.makeText(this@MainActivity, toast, Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    onSelectTicket = { /* Select Ticket Details */ }
                                )

                                "notifications" -> NotificationsScreen(notifications = myNotifications)

                                "profile" -> currentUser?.let { user ->
                                    UserProfileScreen(
                                        user = user,
                                        selectedCurrency = selectedCurrency,
                                        onUpdateProfile = { name, phone, photo ->
                                            viewModel.updateProfile(name, phone, photo) { success, msg ->
                                                Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        onChangePassword = { oldP, newP ->
                                            viewModel.changePassword(oldP, newP) { success, msg ->
                                                Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    )
                                }

                                // Admin Panel Screens
                                "admin_dashboard" -> AdminDashboardScreen(
                                    users = allUsers,
                                    orders = allOrders,
                                    recharges = allRecharges,
                                    services = allServices,
                                    selectedCurrency = selectedCurrency,
                                    onNavigate = { viewModel.setRoute(it) }
                                )

                                "admin_users" -> AdminUsersScreen(
                                    users = allUsers,
                                    selectedCurrency = selectedCurrency,
                                    onCreditBalanceSubmit = { userId, amount, note ->
                                        viewModel.adminCreditUserBalance(userId, amount, note)
                                    },
                                    onToggleUserBlock = { user ->
                                        viewModel.adminToggleUserBlock(user)
                                    }
                                )

                                "admin_orders" -> AdminOrdersScreen(
                                    orders = allOrders,
                                    selectedCurrency = selectedCurrency,
                                    onUpdateOrderStatus = { orderId, status ->
                                        viewModel.adminUpdateOrderStatus(orderId, status)
                                    }
                                )

                                "admin_recharges" -> AdminRechargesScreen(
                                    recharges = allRecharges,
                                    selectedCurrency = selectedCurrency,
                                    onApproveRecharge = { rechargeId, note ->
                                        viewModel.adminApproveRecharge(rechargeId, note)
                                    },
                                    onRejectRecharge = { rechargeId, note ->
                                        viewModel.adminRejectRecharge(rechargeId, note)
                                    }
                                )

                                "admin_services" -> AdminServicesScreen(
                                    categories = categories,
                                    services = allServices,
                                    selectedCurrency = selectedCurrency,
                                    onSaveServiceSubmit = { srv -> viewModel.adminSaveService(srv) },
                                    onDeleteServiceSubmit = { srvId -> viewModel.adminDeleteService(srvId) },
                                    onSaveCategorySubmit = { cat -> viewModel.adminSaveCategory(cat) }
                                )

                                "admin_payment_settings" -> AdminPaymentSettingsScreen(
                                    currentSettings = paymentSettings,
                                    onSavePaymentSettingsSubmit = { settings -> viewModel.adminSavePaymentSettings(settings) }
                                )

                                "admin_coupons" -> AdminCouponsScreen(
                                    coupons = allCoupons,
                                    selectedCurrency = selectedCurrency,
                                    onCreateCouponSubmit = { coupon -> viewModel.adminCreateCoupon(coupon) },
                                    onDeleteCouponSubmit = { couponId -> viewModel.adminDeleteCoupon(couponId) }
                                )

                                "admin_website_settings" -> AdminWebsiteSettingsScreen(
                                    currentSettings = websiteSettings,
                                    onSaveWebsiteSettingsSubmit = { settings -> viewModel.adminSaveWebsiteSettings(settings) },
                                    onBroadcastNotificationSubmit = { title, msg, type ->
                                        viewModel.adminBroadcastNotification(title, msg, type)
                                    },
                                    onExportBackupClick = {
                                        viewModel.exportBackupJson { json ->
                                            Toast.makeText(this@MainActivity, "Backup Generated (${json.length} chars)", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                )

                                else -> Text("Page Not Found", modifier = Modifier.padding(16.dp))
                            }
                        }

                        // Order Confirmation Dialog Overlay
                        confirmedOrderDialogState?.let { order ->
                            OrderConfirmationDialog(
                                order = order,
                                selectedCurrency = selectedCurrency,
                                onDismiss = { confirmedOrderDialogState = null },
                                onWhatsAppClick = { ord ->
                                    WhatsappUtils.openOrderWhatsAppChat(
                                        context = this@MainActivity,
                                        adminPhoneNumber = adminPhone,
                                        orderId = ord.orderIdString,
                                        serviceName = ord.serviceName,
                                        link = ord.link,
                                        quantity = ord.quantity,
                                        price = ord.price,
                                        currency = selectedCurrency
                                    )
                                    confirmedOrderDialogState = null
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
