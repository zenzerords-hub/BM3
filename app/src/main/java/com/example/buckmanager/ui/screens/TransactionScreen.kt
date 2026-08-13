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


    val isDarkMode by viewModel.isDarkMode.collectAsState()

    val cardBg = if (isDarkMode) Color(0xFF181C26) else Color(0xFFFFFFFF)
    val innerBoxBg = if (isDarkMode) Color(0xFF0F1117) else Color(0xFFF4F5F9)
    val textPrimary = if (isDarkMode) Color.White else Color(0xFF121926)
    val globalTextColor = parseHexColor(globalBg.textColorHex, textPrimary)
    val textSecondary = if (isDarkMode) Color(0xFF9CA3AF) else Color(0xFF5A667A)
    val inputBorder = if (isDarkMode) Color(0xFF2A273C) else Color(0xFFE2E8F0)
    val incomeColor = if (isDarkMode) Color(0xFF34D399) else Color(0xFF0E8345)
    val expenseColor = if (isDarkMode) Color(0xFFFB7185) else Color(0xFFD9254C)
    var transactionToDelete by remember { mutableStateOf<Long?>(null) }

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
                    showHideBalances = false,
                    onToggleLock = { viewModel.toggleEditLock() },
                    textColorHex = globalBg.textColorHex
                )
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
