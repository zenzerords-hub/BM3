package com.example.buckmanager.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.buckmanager.model.MonetizationState
import com.example.buckmanager.model.formatRp

@Composable
fun PremiumModal(
    visible: Boolean,
    isDarkMode: Boolean,
    monetizationState: MonetizationState,
    onDismiss: () -> Unit,
    onWatchAd: () -> Unit,
    onPurchase: () -> Unit
) {
    if (!visible) return

    val containerColor = if (isDarkMode) Color(0xFF1E1B2E) else Color(0xFFF4F5F9)
    val textColor = if (isDarkMode) Color.White else Color.Black
    val goldColor = Color(0xFFD4A54A)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = containerColor,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = "Premium", tint = goldColor, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Buck Manager Premium", color = textColor, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Unlock custom themes, advanced widgets, and all features!",
                    color = if (isDarkMode) Color(0xFF8B92A5) else Color(0xFF5A667A),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )

                if (monetizationState.isPremium) {
                    Text(
                        "🎉 You have Lifetime Premium! 🎉",
                        color = Color(0xFF4ECB8D),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                } else if (monetizationState.premiumExpiryDate > System.currentTimeMillis()) {
                    Text(
                        "✨ Temporary Premium Active ✨\nEnjoy your 24-hour pass!",
                        color = goldColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                } else {
                    // Option 1: Purchase
                    Button(
                        onClick = onPurchase,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = goldColor),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Buy Lifetime Premium - ${formatRp(15000.0)}", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    Text("OR", color = if (isDarkMode) Color(0xFF8B92A5) else Color(0xFF5A667A), fontWeight = FontWeight.Bold, fontSize = 12.sp)

                    // Option 2: Watch Ads
                    val adsRemaining = 3 - monetizationState.adTickets
                    Button(
                        onClick = onWatchAd,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = if (isDarkMode) Color(0xFF2A273C) else Color(0xFFE2E8F0)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Watch Ad", tint = textColor, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Watch Ad for 24h Premium ($adsRemaining left)", color = textColor, fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = if (isDarkMode) Color(0xFF8B92A5) else Color(0xFF5A667A))
            }
        }
    )
}
