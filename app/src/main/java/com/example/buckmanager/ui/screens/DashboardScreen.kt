package com.example.buckmanager.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.buckmanager.model.Envelope
import com.example.buckmanager.model.formatRp
import com.example.buckmanager.ui.AppSpacing
import com.example.buckmanager.utils.customCardStyle
import com.example.buckmanager.ui.GoldAccent
import com.example.buckmanager.ui.components.GoalDepositModal
import com.example.buckmanager.ui.components.PremiumModal
import com.example.buckmanager.model.MonetizationState
import com.example.buckmanager.ui.components.ParticleEffectCanvas
import com.example.buckmanager.ui.components.UniversalHeader
import com.example.buckmanager.ui.components.getIconVector
import com.example.buckmanager.ui.components.parseHexColor
import androidx.compose.ui.platform.LocalContext
import com.example.buckmanager.viewmodel.BuckViewModel
import com.example.buckmanager.widget.GoalAppWidgetProvider
import java.text.NumberFormat
import java.util.Locale



@Composable
fun DashboardScreen(
    viewModel: BuckViewModel,
    onNavigateToTransactions: () -> Unit,
    onOpenSettings: () -> Unit,
    onEditEnvelope: (Envelope) -> Unit,
    onAddEnvelopeClick: () -> Unit,
    onEditHeaderCard: (String) -> Unit,
    onEditFundGoal: () -> Unit,
    onEditBackground: () -> Unit
) {
    val context = LocalContext.current
    val globalBg by viewModel.globalBackground.collectAsState()
    val headerCards by viewModel.headerCardsConfig.collectAsState()
    val fundGoal by viewModel.fundGoal.collectAsState()
    val envelopes by viewModel.envelopes.collectAsState()
    val currencySymbol by viewModel.currencySymbol.collectAsState()
    val isEditLocked by viewModel.isEditLocked.collectAsState()
    val streakData by viewModel.streakData.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()

    var showGoalDepositModal by remember { mutableStateOf(false) }
    var showPremiumModal by remember { mutableStateOf(false) }
    val monetization by viewModel.monetization.collectAsState(initial = MonetizationState())

    val hideBalances by viewModel.hideBalances.collectAsState()

    val netWorth = viewModel.getNetWorth()
    val totalIncome = viewModel.getTotalIncome()
    val totalExpense = viewModel.getTotalExpense()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(parseHexColor(globalBg.backgroundColorHex, Color(0xFF0F1117)))
    ) {
        // Background Wallpaper Image
        if (!globalBg.backgroundImageUri.isNullOrBlank()) {
            AsyncImage(
                model = globalBg.backgroundImageUri,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = (globalBg.dimOpacity / 100f).coerceIn(0f, 0.98f)))
            )
        }

        // Particles Layer
        ParticleEffectCanvas(effectType = globalBg.particleEffect)

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                UniversalHeader(
                    title = "Dashboard",
                    hasPremium = viewModel.hasPremium(),
                    isEditLocked = isEditLocked,
                    isDarkMode = isDarkMode,
                    hideBalances = hideBalances,
                    onToggleLock = { viewModel.toggleEditLock() },
                    onCustomizeClick = onEditBackground,
                    onPremiumClick = { showPremiumModal = true },
                    onToggleHideBalances = { viewModel.toggleHideBalances() },
                    textColorHex = globalBg.textColorHex
                )
            }

            // Total Net Worth Card
            item {
                val netCard = headerCards.netWorth
                val netContainerColor = if (!netCard.backgroundImageUri.isNullOrBlank()) Color.Transparent else parseHexColor(netCard.backgroundColorHex, Color(0xFF181C26))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { if (!isEditLocked && viewModel.hasPremium()) onEditHeaderCard("netWorth") }
                        .customCardStyle(
                            shape = RoundedCornerShape(
                                topStart = netCard.radiusTopLeft.dp,
                                topEnd = netCard.radiusTopRight.dp,
                                bottomEnd = netCard.radiusBottomRight.dp,
                                bottomStart = netCard.radiusBottomLeft.dp
                            ),
                            backgroundColor = netContainerColor,
                            useGradient = netCard.useGradient,
                            gradientColors = netCard.gradientColors.map { parseHexColor(it) },
                            gradientAngle = netCard.gradientAngle,
                            borderTop = netCard.borderTop.dp,
                            borderRight = netCard.borderRight.dp,
                            borderBottom = netCard.borderBottom.dp,
                            borderLeft = netCard.borderLeft.dp,
                            borderColor = parseHexColor(netCard.borderColorHex, Color.Gray).copy(alpha = 0.2f)
                        )
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        if (!netCard.backgroundImageUri.isNullOrBlank()) {
                            AsyncImage(
                                model = netCard.backgroundImageUri,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.matchParentSize()
                            )
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .background(Color.Black.copy(alpha = (netCard.dimOpacity / 100f).coerceIn(0f, 0.98f)))
                            )
                        }

                        Column(modifier = Modifier.padding(start = netCard.paddingLeft.dp, top = netCard.paddingTop.dp, end = netCard.paddingRight.dp, bottom = netCard.paddingBottom.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column {
                                    Text(
                                        text = "NET WORTH",
                                        color = parseHexColor(netCard.labelColorHex, Color.White.copy(alpha = 0.8f)),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        letterSpacing = 1.sp
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = if (hideBalances) currencySymbol + "••••••••" else formatRp(netWorth),
                                        color = parseHexColor(netCard.valueColorHex, Color.White),
                                        fontWeight = FontWeight.Black,
                                        fontSize = 32.sp
                                    )

                                }

                                // Pencil icon
                                    if (!isEditLocked && viewModel.hasPremium()) {
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(CircleShape)
                                                .background(Color.White)
                                                .clickable { onEditHeaderCard("netWorth") },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Edit,
                                                contentDescription = "Edit Card",
                                                tint = Color.Black,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                            }
                        }
                    }
                }
            }

            // Income / Expense Split Cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val incCard = headerCards.income
                    val incContainerColor = if (!incCard.backgroundImageUri.isNullOrBlank()) Color.Transparent else parseHexColor(incCard.backgroundColorHex, Color(0xFF1A3A2A))
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { if (!isEditLocked && viewModel.hasPremium()) onEditHeaderCard("income") }
                            .customCardStyle(
                                shape = RoundedCornerShape(
                                    topStart = incCard.radiusTopLeft.dp,
                                    topEnd = incCard.radiusTopRight.dp,
                                    bottomEnd = incCard.radiusBottomRight.dp,
                                    bottomStart = incCard.radiusBottomLeft.dp
                                ),
                                backgroundColor = incContainerColor,
                                useGradient = incCard.useGradient,
                                gradientColors = incCard.gradientColors.map { parseHexColor(it) },
                                gradientAngle = incCard.gradientAngle,
                                borderTop = incCard.borderTop.dp,
                                borderRight = incCard.borderRight.dp,
                                borderBottom = incCard.borderBottom.dp,
                                borderLeft = incCard.borderLeft.dp,
                                borderColor = parseHexColor(incCard.borderColorHex, Color.Gray).copy(alpha = 0.2f)
                            )
                    ) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            if (!incCard.backgroundImageUri.isNullOrBlank()) {
                                AsyncImage(
                                    model = incCard.backgroundImageUri,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.matchParentSize()
                                )
                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .background(Color.Black.copy(alpha = (incCard.dimOpacity / 100f).coerceIn(0f, 0.98f)))
                                )
                            }

                            Column(modifier = Modifier.padding(start = incCard.paddingLeft.dp, top = incCard.paddingTop.dp, end = incCard.paddingRight.dp, bottom = incCard.paddingBottom.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        // Icon in circle
                                        Box(
                                            modifier = Modifier.size(28.dp).clip(CircleShape).background(Color(0xFF10B981).copy(alpha = 0.15f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.ArrowUpward, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "INCOME",
                                            color = parseHexColor(incCard.labelColorHex, Color(0xFF9CA3AF)),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    }

                                    // Pencil icon
                                    if (!isEditLocked && viewModel.hasPremium()) {
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(Color.White)
                                                .clickable { onEditHeaderCard("income") },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Edit,
                                                contentDescription = "Edit Card",
                                                tint = Color.Black,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = if (hideBalances) "+" + currencySymbol + "•••••" else "+${formatRp(totalIncome)}",
                                    color = parseHexColor(incCard.valueColorHex, Color(0xFF10B981)),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp
                                )

                            }
                        }
                    }

                    val expCard = headerCards.expense
                    val expContainerColor = if (!expCard.backgroundImageUri.isNullOrBlank()) Color.Transparent else parseHexColor(expCard.backgroundColorHex, Color(0xFF381E29))
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { if (!isEditLocked && viewModel.hasPremium()) onEditHeaderCard("expense") }
                            .customCardStyle(
                                shape = RoundedCornerShape(
                                    topStart = expCard.radiusTopLeft.dp,
                                    topEnd = expCard.radiusTopRight.dp,
                                    bottomEnd = expCard.radiusBottomRight.dp,
                                    bottomStart = expCard.radiusBottomLeft.dp
                                ),
                                backgroundColor = expContainerColor,
                                useGradient = expCard.useGradient,
                                gradientColors = expCard.gradientColors.map { parseHexColor(it) },
                                gradientAngle = expCard.gradientAngle,
                                borderTop = expCard.borderTop.dp,
                                borderRight = expCard.borderRight.dp,
                                borderBottom = expCard.borderBottom.dp,
                                borderLeft = expCard.borderLeft.dp,
                                borderColor = parseHexColor(expCard.borderColorHex, Color.Gray).copy(alpha = 0.2f)
                            )
                    ) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            if (!expCard.backgroundImageUri.isNullOrBlank()) {
                                AsyncImage(
                                    model = expCard.backgroundImageUri,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.matchParentSize()
                                )
                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .background(Color.Black.copy(alpha = (expCard.dimOpacity / 100f).coerceIn(0f, 0.98f)))
                                )
                            }
                            Column(modifier = Modifier.padding(start = expCard.paddingLeft.dp, top = expCard.paddingTop.dp, end = expCard.paddingRight.dp, bottom = expCard.paddingBottom.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        // Icon in circle
                                        Box(
                                            modifier = Modifier.size(28.dp).clip(CircleShape).background(Color(0xFFEF4444).copy(alpha = 0.15f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.ArrowDownward, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "EXPENSE",
                                            color = parseHexColor(expCard.labelColorHex, Color(0xFF9CA3AF)),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    }

                                    // Pencil icon
                                    if (!isEditLocked && viewModel.hasPremium()) {
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(Color.White)
                                                .clickable { onEditHeaderCard("expense") },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Edit,
                                                contentDescription = "Edit Card",
                                                tint = Color.Black,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = if (hideBalances) "-" + currencySymbol + "•••••" else "-${formatRp(totalExpense)}",
                                    color = parseHexColor(expCard.valueColorHex, Color(0xFFFB7185)),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp
                                )

                            }
                        }
                    }
                }
            }

            // Goal Feature Widget
            item {
                val progressRatio = if (fundGoal.targetAmount > 0) {
                    (fundGoal.currentAmount / fundGoal.targetAmount).toFloat().coerceIn(0f, 1f)
                } else 0f
                val percentageInt = (progressRatio * 100).toInt()
                val remainingAmount = (fundGoal.targetAmount - fundGoal.currentAmount).coerceAtLeast(0.0)

                val labelColor = parseHexColor(fundGoal.labelColorHex, GoldAccent)
                val valueColor = parseHexColor(fundGoal.valueColorHex, if (isDarkMode) Color.White else Color(0xFF121926))

                val fundContainerColor = if (!fundGoal.backgroundImageUri.isNullOrBlank()) Color.Transparent else parseHexColor(fundGoal.backgroundColorHex, Color(0xFF181C26))
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .customCardStyle(
                            shape = RoundedCornerShape(
                                topStart = fundGoal.radiusTopLeft.dp,
                                topEnd = fundGoal.radiusTopRight.dp,
                                bottomEnd = fundGoal.radiusBottomRight.dp,
                                bottomStart = fundGoal.radiusBottomLeft.dp
                            ),
                            backgroundColor = fundContainerColor,
                            useGradient = fundGoal.useGradient,
                            gradientColors = fundGoal.gradientColors.map { parseHexColor(it) },
                            gradientAngle = fundGoal.gradientAngle,
                            borderTop = fundGoal.borderTop.dp,
                            borderRight = fundGoal.borderRight.dp,
                            borderBottom = fundGoal.borderBottom.dp,
                            borderLeft = fundGoal.borderLeft.dp,
                            borderColor = labelColor.copy(alpha = 0.4f)
                        )
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        if (!fundGoal.backgroundImageUri.isNullOrBlank()) {
                            AsyncImage(
                                model = fundGoal.backgroundImageUri,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.matchParentSize()
                            )
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .background(Color.Black.copy(alpha = (fundGoal.dimOpacity / 100f).coerceIn(0f, 0.98f)))
                            )
                        }

                        Column(
                            modifier = Modifier.padding(start = fundGoal.paddingLeft.dp, top = fundGoal.paddingTop.dp, end = fundGoal.paddingRight.dp, bottom = fundGoal.paddingBottom.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Header Row: Title & Percentage Badge & Edit Pencil
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Flag, contentDescription = null, tint = labelColor, modifier = Modifier.size(22.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = fundGoal.name.ifBlank { "My Goal" },
                                            color = valueColor,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                        Text(
                                            text = "TARGET SAVINGS",
                                            color = labelColor.copy(alpha = 0.85f),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp,
                                            letterSpacing = 1.sp
                                        )
                                    }
                                }

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Circular Percentage
                                    Box(contentAlignment = Alignment.Center) {
                                        CircularProgressIndicator(
                                            progress = { 1f },
                                            modifier = Modifier.size(42.dp),
                                            color = labelColor.copy(alpha = 0.2f),
                                            strokeWidth = 3.dp,
                                            strokeCap = StrokeCap.Round
                                        )
                                        CircularProgressIndicator(
                                            progress = { progressRatio },
                                            modifier = Modifier.size(42.dp),
                                            color = labelColor,
                                            strokeWidth = 3.dp,
                                            strokeCap = StrokeCap.Round
                                        )
                                        Text(
                                            text = "$percentageInt%",
                                            color = valueColor,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 12.sp
                                        )
                                    }

                                    // Pin Widget to Android Home Screen
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(labelColor.copy(alpha = 0.25f))
                                            .clickable { GoalAppWidgetProvider.pinWidgetToHomeScreen(context) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Widgets,
                                            contentDescription = "Pin Widget to Home Screen",
                                            tint = labelColor,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }

                                    // Edit Pencil
                                    if (!isEditLocked && viewModel.hasPremium()) {
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(CircleShape)
                                                .background(Color.White)
                                                .clickable { onEditFundGoal() },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Edit,
                                                contentDescription = "Goal settings",
                                                tint = Color.Black,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            if (fundGoal.targetAmount <= 0.0) {
                                Button(
                                    onClick = { onEditFundGoal() },
                                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = labelColor),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("SET A GOAL", color = parseHexColor(fundGoal.btnTextColorHex, Color.White), fontWeight = FontWeight.Bold)
                                }
                            } else {
                                // Value Row
                                Column {
                                    Text(
                                        text = if (hideBalances) currencySymbol + "••••••" else formatRp(fundGoal.currentAmount),
                                        color = valueColor,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 22.sp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Target: ${formatRp(fundGoal.targetAmount)}",
                                            color = labelColor.copy(alpha = 0.85f),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = if (remainingAmount <= 0) "🎉 Goal Reached!" else "Remaining: ${if (hideBalances) currencySymbol + "•••••" else formatRp(remainingAmount)}",
                                            color = if (remainingAmount <= 0) Color(0xFF34D399) else labelColor.copy(alpha = 0.85f),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }

                                // Progress Bar
                                LinearProgressIndicator(
                                    progress = { progressRatio },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    color = labelColor,
                                    trackColor = if (isDarkMode) Color.White.copy(alpha = 0.15f) else Color.Black.copy(alpha = 0.1f)
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    Surface(
                                        onClick = { showGoalDepositModal = true },
                                        shape = RoundedCornerShape(12.dp),
                                        color = parseHexColor(fundGoal.btnBgColorHex, labelColor)
                                    ) {
                                        Text(
                                            text = "+ Deposit",
                                            color = parseHexColor(fundGoal.btnTextColorHex, Color.White),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Envelopes Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val defaultTextColor = if (isDarkMode) Color.White else Color(0xFF121926)
                    Text(
                        text = "Budget Envelopes",
                        color = parseHexColor(globalBg.textColorHex, defaultTextColor),
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp
                    )
                    Surface(
                        onClick = onAddEnvelopeClick,
                        shape = RoundedCornerShape(16.dp),
                        color = Color.Transparent,
                        border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.AddCircle, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Add Envelope", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }

            // Envelopes List
            items(envelopes) { env ->
                val stats = viewModel.getEnvelopeStats(env.id)
                val envContainerColor = if (!env.backgroundImageUri.isNullOrBlank()) Color.Transparent else parseHexColor(env.backgroundColorHex, Color(0xFF1A1E30))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .customCardStyle(
                            shape = RoundedCornerShape(
                                topStart = env.radiusTopLeft.dp,
                                topEnd = env.radiusTopRight.dp,
                                bottomEnd = env.radiusBottomRight.dp,
                                bottomStart = env.radiusBottomLeft.dp
                            ),
                            backgroundColor = envContainerColor,
                            useGradient = env.useGradient,
                            gradientColors = env.gradientColors.map { parseHexColor(it) },
                            gradientAngle = env.gradientAngle,
                            borderTop = env.borderTop.dp,
                            borderRight = env.borderRight.dp,
                            borderBottom = env.borderBottom.dp,
                            borderLeft = env.borderLeft.dp,
                            borderColor = parseHexColor(env.borderColorHex, Color.Gray).copy(alpha = 0.2f)
                        )
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        if (!env.backgroundImageUri.isNullOrBlank()) {
                            AsyncImage(
                                model = env.backgroundImageUri,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.matchParentSize()
                            )
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .background(Color.Black.copy(alpha = (env.dimOpacity / 100f).coerceIn(0f, 0.98f)))
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = env.paddingLeft.dp, top = env.paddingTop.dp, end = env.paddingRight.dp, bottom = env.paddingBottom.dp)
                                .clickable { onEditEnvelope(env) },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(parseHexColor(env.colorHex).copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = getIconVector(env.iconName),
                                    contentDescription = env.name,
                                    tint = parseHexColor(env.colorHex),
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${env.name.uppercase()} (${env.percentage}%)",
                                    color = parseHexColor(env.labelColorHex, Color(0xFF64748B)),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (hideBalances) currencySymbol + "••••••" else formatRp(stats.remaining),
                                    color = parseHexColor(env.valueColorHex, if (isDarkMode) Color.White else Color(0xFF0F172A)),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 22.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (env.id == "savings") "Secured funds. Do not touch!" else "Remaining from ${if (hideBalances) currencySymbol + "•••••" else formatRp(stats.allocated)}",
                                    color = parseHexColor(env.descriptionColorHex, Color(0xFF9CA3AF)),
                                    fontSize = 11.sp
                                )
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
            }

        }


        GoalDepositModal(
            visible = showGoalDepositModal,
            goalName = fundGoal.name.ifBlank { "My Goal" },
            currentAmount = fundGoal.currentAmount,
            targetAmount = fundGoal.targetAmount,
            isDarkMode = isDarkMode,
            onDismiss = { showGoalDepositModal = false },
            onConfirmDeposit = { amount ->
                viewModel.depositToGoal(amount)
            }
        )

        PremiumModal(
            visible = showPremiumModal,
            isDarkMode = isDarkMode,
            monetizationState = monetization,
            onDismiss = { showPremiumModal = false },
            onWatchAd = {
                viewModel.watchAd()
                showPremiumModal = false
            },
            onPurchase = {
                viewModel.purchaseLifetimePremium()
                showPremiumModal = false
            }
        )
    }
}
