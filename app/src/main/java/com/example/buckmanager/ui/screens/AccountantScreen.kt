package com.example.buckmanager.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.buckmanager.ui.AppSpacing
import com.example.buckmanager.ui.GoldAccent
import com.example.buckmanager.ui.components.parseHexColor
import com.example.buckmanager.viewmodel.BuckViewModel
import com.example.buckmanager.model.Envelope
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountantScreen(
    viewModel: BuckViewModel,
    onOpenSettings: () -> Unit
) {
    val globalBg by viewModel.globalBackground.collectAsState()
    val envelopes by viewModel.envelopes.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val isEditLocked by viewModel.isEditLocked.collectAsState()

    val textPrimary = if (isDarkMode) Color.White else Color(0xFF121926)
    val textSecondary = if (isDarkMode) Color(0xFF9CA3AF) else Color(0xFF64748B)
    val cardBg = if (isDarkMode) Color(0xFF1E2433) else Color.White
    val screenBg = parseHexColor(globalBg.backgroundColorHex, if (isDarkMode) Color(0xFF0F1117) else Color(0xFFF4F6FB))

    val envelopePercents = remember(envelopes) {
        val map = mutableMapOf<String, Float>()
        envelopes.forEach { if (it.id != "main") map[it.id] = it.percentage.toFloat() }
        mutableStateOf(map)
    }

    val totalAllocated = envelopePercents.value.values.sum().roundToInt()
    val mainPercent = (100 - totalAllocated).coerceAtLeast(0)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(screenBg)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 32.dp, bottom = 220.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Logo Box
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFFBBF24)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("B", color = Color(0xFF121926), fontWeight = FontWeight.Black, fontSize = 24.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("BUCK MANAGER", color = textSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                                Text("Allocation", color = textPrimary, fontSize = 28.sp, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                }
            }

            // Summary Donut Card
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = cardBg,
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Donut Chart
                        Box(
                            modifier = Modifier.size(110.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            DonutChart(
                                envelopes = envelopes,
                                percents = envelopePercents.value,
                                mainPercent = mainPercent,
                                modifier = Modifier.fillMaxSize()
                            )
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("$totalAllocated%", color = textPrimary, fontWeight = FontWeight.Black, fontSize = 24.sp)
                                Text("Allocated", color = textSecondary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                        
                        Spacer(modifier = Modifier.width(24.dp))
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFF8FAFC),
                                modifier = Modifier.padding(bottom = 12.dp).fillMaxWidth()
                            ) {
                                Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFF1D4ED8), modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Smart boundaries help you avoid overspending.", color = textSecondary, fontSize = 10.sp, lineHeight = 12.sp)
                                }
                            }
                            if (totalAllocated == 100) {
                                Text("Your envelope plan is balanced!", color = textPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp, lineHeight = 20.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("You've allocated all your money wisely.", color = textSecondary, fontSize = 13.sp, lineHeight = 18.sp)
                                Spacer(modifier = Modifier.height(12.dp))
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFFECFDF5)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.CheckCircleOutline, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("All set! You're ready to apply.", color = Color(0xFF047857), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }
                            } else {
                                Text("Your plan needs attention", color = textPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp, lineHeight = 20.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Make sure your total allocation equals 100%.", color = textSecondary, fontSize = 13.sp, lineHeight = 18.sp)
                            }
                        }
                    }

                    // Legend
                    @OptIn(ExperimentalLayoutApi::class)
                    FlowRow(
                        modifier = Modifier.padding(horizontal = 24.dp).padding(bottom = 16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val activeEnvelopes = envelopes.filter { it.id != "main" }.sortedBy { it.orderIndex }
                        if (mainPercent > 0) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(end = 12.dp)) {
                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFE2E8F0)))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Unallocated ($mainPercent%)", fontSize = 10.sp, color = textSecondary)
                            }
                        }
                        activeEnvelopes.forEach { env ->
                            val pct = envelopePercents.value[env.id]?.roundToInt() ?: 0
                            if (pct > 0) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(end = 12.dp)) {
                                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(parseHexColor(env.colorHex)))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("${env.name} ($pct%)", fontSize = 10.sp, color = textSecondary)
                                }
                            }
                        }
                    }
                }
            }

            // Envelopes
            items(envelopes.sortedBy { it.orderIndex }) { env ->
                val envColor = parseHexColor(env.colorHex)
                val isMain = env.id == "main"
                val currentVal = if (isMain) mainPercent else envelopePercents.value[env.id]?.roundToInt() ?: 0
                val description = getEnvelopeDescription(env.id)
                val icon = getEnvelopeIcon(env.id)
                val iconBgColor = if (isMain) Color(0xFFF1F5F9) else envColor.copy(alpha = 0.1f)
                val cardBorder = if (isMain) BorderStroke(1.dp, Color(0xFFE2E8F0)) else null

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = cardBg,
                    shadowElevation = if (isMain) 0.dp else 1.dp,
                    border = cardBorder,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Icon
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(iconBgColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(icon, contentDescription = null, tint = if (isMain) Color(0xFF64748B) else envColor, modifier = Modifier.size(24.dp))
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        // Text Info
                        Column(modifier = Modifier.weight(1f)) {
                            val nameText = if (isMain) "${env.name.uppercase()} (Unallocated)" else env.name.uppercase()
                            Text(nameText, color = textPrimary, fontWeight = FontWeight.Black, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(description, color = textSecondary, fontSize = 11.sp, lineHeight = 16.sp)
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        // Slider / Value
                        Column(
                            modifier = Modifier.width(120.dp),
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                "$currentVal%", 
                                color = if (isMain) Color(0xFF64748B) else envColor, 
                                fontWeight = FontWeight.Black, 
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            if (isMain) {
                                // Just a gauge
                                LinearProgressIndicator(
                                    progress = { currentVal / 100f },
                                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                                    color = Color(0xFF94A3B8),
                                    trackColor = Color(0xFFF1F5F9),
                                )
                            } else {
                                val otherSum = envelopePercents.value.filterKeys { it != env.id }.values.sum()
                                val maxAllowed = (100f - otherSum).coerceAtLeast(0f)
                                
                                Slider(
                                    value = envelopePercents.value[env.id] ?: 0f,
                                    onValueChange = { newVal ->
                                        val newMap = envelopePercents.value.toMutableMap()
                                        newMap[env.id] = newVal
                                        envelopePercents.value = newMap
                                    },
                                    valueRange = 0f..maxAllowed,
                                    colors = SliderDefaults.colors(
                                        thumbColor = envColor,
                                        activeTrackColor = envColor,
                                        inactiveTrackColor = if (isDarkMode) Color(0xFF333333) else Color(0xFFE2E8F0)
                                    ),
                                    modifier = Modifier.fillMaxWidth().height(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
        
        // Sticky Bottom Area
        Column(
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            // Decorative Total Bar
            Surface(
                color = cardBg,
                shadowElevation = 8.dp,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(28.dp).clip(CircleShape).background(Color(0xFF2563EB)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("TOTAL ALLOCATED", color = textSecondary, fontWeight = FontWeight.Bold, fontSize = 13.sp, letterSpacing = 1.sp)
                    }
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (totalAllocated == 100) Text("✨ ", fontSize = 20.sp)
                        Text(
                            "$totalAllocated%", 
                            color = Color(0xFF2563EB), 
                            fontWeight = FontWeight.Black, 
                            fontSize = 28.sp
                        )
                        if (totalAllocated == 100) Text(" ✨", fontSize = 20.sp)
                    }
                }
            }
            
            // Original Apply Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(cardBg)
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 90.dp, top = 8.dp)
            ) {
                Button(
                    onClick = {
                        val intMap = envelopePercents.value.mapValues { it.value.roundToInt() }
                        viewModel.applyAllocationStrategy(intMap)
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldAccent,
                        disabledContainerColor = if (isDarkMode) Color(0xFF231F33) else Color(0xFFE2E8F0)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Save, contentDescription = null, tint = Color(0xFF0F172A))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Apply Allocation Strategy", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

fun getEnvelopeDescription(id: String): String {
    return when(id) {
        "needs" -> "Essentials like groceries, bills, rent, and transportation."
        "wants" -> "Things that make life enjoyable and fun."
        "savings" -> "Build your future and achieve your goals."
        "main" -> "Money not yet assigned to any category."
        else -> "Custom budget category for specific expenses."
    }
}

fun getEnvelopeIcon(id: String): ImageVector {
    return when(id) {
        "needs" -> Icons.Default.ShoppingBag
        "wants" -> Icons.Default.FavoriteBorder
        "savings" -> Icons.Default.Savings
        "main" -> Icons.Default.AccountBalanceWallet
        else -> Icons.Default.Folder
    }
}

@Composable
fun DonutChart(
    envelopes: List<Envelope>,
    percents: Map<String, Float>,
    mainPercent: Int,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val strokeWidth = 14.dp.toPx()
        val radius = (size.minDimension - strokeWidth) / 2
        val center = Offset(size.width / 2, size.height / 2)
        val arcSize = Size(radius * 2, radius * 2)
        val arcTopLeft = Offset(center.x - radius, center.y - radius)
        
        var currentStartAngle = -90f
        
        val activeEnvelopes = envelopes.filter { it.id != "main" }.sortedBy { it.orderIndex }
        
        if (mainPercent > 0) {
            val sweepAngle = (mainPercent / 100f) * 360f
            drawArc(
                color = Color(0xFFE2E8F0),
                startAngle = currentStartAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = arcTopLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            currentStartAngle += sweepAngle
        }
        
        activeEnvelopes.forEach { env ->
            val pct = percents[env.id] ?: 0f
            if (pct > 0f) {
                val sweepAngle = (pct / 100f) * 360f
                drawArc(
                    color = parseHexColor(env.colorHex),
                    startAngle = currentStartAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = arcTopLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
                currentStartAngle += sweepAngle
            }
        }
    }
}
