package com.example.buckmanager.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.buckmanager.ui.AppSpacing
import com.example.buckmanager.ui.GoldAccent
import com.example.buckmanager.ui.components.UniversalHeader
import com.example.buckmanager.ui.components.parseHexColor
import com.example.buckmanager.viewmodel.BuckViewModel
import com.example.buckmanager.model.formatRp

@Composable
fun TransactionScreen(
    viewModel: BuckViewModel,
    onOpenSettings: () -> Unit,

    onEditFundGoal: () -> Unit
) {
    val globalBg by viewModel.globalBackground.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val currencySymbol by viewModel.currencySymbol.collectAsState()
    val isEditLocked by viewModel.isEditLocked.collectAsState()
    val envelopes by viewModel.envelopes.collectAsState()

    val streakData by viewModel.streakData.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()

    val cardBg = if (isDarkMode) Color(0xFF181C26) else Color(0xFFFFFFFF)
    val innerBoxBg = if (isDarkMode) Color(0xFF0F1117) else Color(0xFFF4F5F9)
    val textPrimary = if (isDarkMode) Color.White else Color(0xFF121926)
    val globalTextColor = parseHexColor(globalBg.textColorHex, textPrimary)
    val textSecondary = if (isDarkMode) Color(0xFF9CA3AF) else Color(0xFF5A667A)
    val inputBorder = if (isDarkMode) Color(0xFF2A273C) else Color(0xFFE2E8F0)
    val incomeColor = if (isDarkMode) Color(0xFF34D399) else Color(0xFF0E8345)
    val expenseColor = if (isDarkMode) Color(0xFFFB7185) else Color(0xFFD9254C)
    val expenseActiveBg = if (isDarkMode) Color(0xFF3A1A2A) else Color(0xFFFDF0F2)
    val incomeActiveBg = if (isDarkMode) Color(0xFF1A3A2A) else Color(0xFFE8F8EE)

    var type by remember { mutableStateOf("expense") } // "expense" or "income"
    var amountText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(envelopes.firstOrNull()?.id ?: "needs") }
    var descriptionText by remember { mutableStateOf("") }
    var transactionToDelete by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(envelopes) {
        if (envelopes.none { it.id == selectedCategory }) {
            selectedCategory = envelopes.firstOrNull()?.id ?: "needs"
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(parseHexColor(globalBg.backgroundColorHex, if (isDarkMode) Color(0xFF0F1117) else Color(0xFFF5F6FA)))
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = AppSpacing.base, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                UniversalHeader(
                    title = "Transactions",
                    hasPremium = viewModel.hasPremium(),
                    isEditLocked = isEditLocked,
                    isDarkMode = isDarkMode,
                    showUnlockCustomization = false,
                    onToggleLock = { viewModel.toggleEditLock() },
                    textColorHex = globalBg.textColorHex
                )
            }

            // Record Transaction Form Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Type toggle using TabRow style
                        TabRow(
                            selectedTabIndex = if (type == "expense") 0 else 1,
                            containerColor = innerBoxBg,
                            contentColor = textPrimary,
                            indicator = { },
                            divider = { },
                            modifier = Modifier.clip(RoundedCornerShape(AppSpacing.base))
                        ) {
                            Tab(
                                selected = type == "expense",
                                onClick = { type = "expense" },
                                modifier = Modifier
                                    .padding(AppSpacing.micro)
                                    .clip(RoundedCornerShape(AppSpacing.base))
                                    .background(if (type == "expense") expenseActiveBg else Color.Transparent)
                            ) {
                                Text(
                                    "Expense",
                                    color = if (type == "expense") expenseColor else textSecondary,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(vertical = AppSpacing.compact)
                                )
                            }
                            Tab(
                                selected = type == "income",
                                onClick = { type = "income" },
                                modifier = Modifier
                                    .padding(AppSpacing.micro)
                                    .clip(RoundedCornerShape(AppSpacing.base))
                                    .background(if (type == "income") incomeActiveBg else Color.Transparent)
                            ) {
                                Text(
                                    "Income",
                                    color = if (type == "income") incomeColor else textSecondary,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(vertical = AppSpacing.compact)
                                )
                            }
                        }

                        // Amount field
                        Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.base)) {
                            Text("AMOUNT (RP)", color = textSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            OutlinedTextField(
                                value = amountText,
                                onValueChange = { amountText = it.filter { c -> c.isDigit() } },
                                placeholder = { Text("0", color = textSecondary) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(AppSpacing.base),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = innerBoxBg,
                                    unfocusedContainerColor = innerBoxBg,
                                    focusedTextColor = textPrimary,
                                    unfocusedTextColor = textPrimary,
                                    focusedBorderColor = GoldAccent,
                                    unfocusedBorderColor = inputBorder
                                )
                            )
                        }

                        // Category selection if expense
                        if (type == "expense") {
                            Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.base)) {
                                Text("CATEGORY ENVELOPE", color = textSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                @OptIn(ExperimentalMaterial3Api::class)
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(AppSpacing.base)) {
                                    items(envelopes) { env ->
                                        val isSelected = selectedCategory == env.id
                                        val envColor = parseHexColor(env.colorHex)
                                        FilterChip(
                                            selected = isSelected,
                                            onClick = { selectedCategory = env.id },
                                            label = { Text(env.name, fontWeight = FontWeight.Bold) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = envColor.copy(alpha = 0.2f),
                                                selectedLabelColor = envColor,
                                                labelColor = textSecondary
                                            ),
                                            border = FilterChipDefaults.filterChipBorder(
                                                borderColor = inputBorder,
                                                selectedBorderColor = envColor,
                                                enabled = true,
                                                selected = isSelected
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        // Description
                        Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.base)) {
                            Text("DESCRIPTION", color = textSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            OutlinedTextField(
                                value = descriptionText,
                                onValueChange = { descriptionText = it },
                                placeholder = { Text("Lunch, Coffee, Salary...", color = textSecondary) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(AppSpacing.base),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = innerBoxBg,
                                    unfocusedContainerColor = innerBoxBg,
                                    focusedTextColor = textPrimary,
                                    unfocusedTextColor = textPrimary,
                                    focusedBorderColor = GoldAccent,
                                    unfocusedBorderColor = inputBorder
                                )
                            )
                        }

                        // Submit Button
                        Button(
                            onClick = {
                                val amt = amountText.toDoubleOrNull() ?: 0.0
                                if (amt > 0) {
                                    val cat = if (type == "expense") selectedCategory else "income"
                                    viewModel.addTransaction(type, amt, cat, descriptionText)
                                    amountText = ""
                                    descriptionText = ""
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(AppSpacing.touchTarget),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (type == "expense") expenseColor else incomeColor
                            ),
                            shape = RoundedCornerShape(AppSpacing.standard)
                        ) {
                            Text(
                                "Record Transaction",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }


            // Funding Goal Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Funding Goal", color = globalTextColor, fontWeight = FontWeight.Black, fontSize = 20.sp)
                    TextButton(onClick = onEditFundGoal) {
                        Text("Manage", color = GoldAccent, fontWeight = FontWeight.Bold)
                    }
                }
            }
            
            item {
                val goalConfig by viewModel.fundGoal.collectAsState()
                val goalLabelColor = parseHexColor(goalConfig.labelColorHex, GoldAccent)
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { onEditFundGoal() },
                    shape = RoundedCornerShape(goalConfig.radiusTopLeft.dp, goalConfig.radiusTopRight.dp, goalConfig.radiusBottomRight.dp, goalConfig.radiusBottomLeft.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = parseHexColor(goalConfig.backgroundColorHex, Color(0xFF181C26))
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(goalLabelColor.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Savings, contentDescription = null, tint = goalLabelColor, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(goalConfig.name, color = parseHexColor(goalConfig.valueColorHex, Color.White), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            if (goalConfig.targetAmount > 0) {
                                Text(
                                    "Target: ${formatRp(goalConfig.targetAmount)}",
                                    color = goalLabelColor,
                                    fontSize = 11.sp
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = Color(0xFF9CA3AF),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // History Header
            item {
                Text(
                    "Transaction History",
                    color = globalTextColor,
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp
                )
            }

            // History List
            if (transactions.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(cardBg)
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No transactions recorded yet.", color = textSecondary, fontWeight = FontWeight.Medium)
                    }
                }
            } else {
                items(transactions) { tx ->
                    val env = envelopes.find { it.id == tx.category }
                    val catColor = if (tx.type == "income") incomeColor else parseHexColor(env?.colorHex ?: "#FB7185")

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBg)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(catColor.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (tx.type == "income") Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                                    contentDescription = null,
                                    tint = catColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    tx.description,
                                    color = textPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = if (tx.type == "income") "Income" else (env?.name ?: "Expense"),
                                    color = textSecondary,
                                    fontSize = 11.sp
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "${if (tx.type == "income") "+" else "-"}${formatRp(tx.amount)}",
                                    color = if (tx.type == "income") incomeColor else textPrimary,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp
                                )
                                IconButton(
                                    onClick = { transactionToDelete = tx.id },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = textSecondary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    if (transactionToDelete != null) {
        AlertDialog(
            onDismissRequest = { transactionToDelete = null },
            title = { Text("Delete Transaction", color = textPrimary, fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete this transaction?", color = textSecondary) },
            containerColor = cardBg,
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteTransaction(transactionToDelete!!)
                    transactionToDelete = null
                }) {
                    Text("Delete", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { transactionToDelete = null }) {
                    Text("Cancel", color = textSecondary)
                }
            }
        )
    }
}
