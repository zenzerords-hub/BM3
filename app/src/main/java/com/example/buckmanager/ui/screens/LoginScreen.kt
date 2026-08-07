package com.example.buckmanager.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.CustomCredential
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch
import android.util.Log
import com.example.buckmanager.BuildConfig

import com.example.buckmanager.ui.GoldAccent

@Composable
fun LoginScreen(
    isDarkMode: Boolean,
    onLoginSuccess: (email: String) -> Unit,
    onContinueAsGuest: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }
    
    val bgColor = if (isDarkMode) Color(0xFF0D0C14) else Color(0xFFF8FAFC)
    val titleColor = if (isDarkMode) Color.White else Color(0xFF0F172A)
    val subtitleColor = if (isDarkMode) Color(0xFF8B92A5) else Color(0xFF64748B)
    val btnBgColor = if (isDarkMode) Color(0xFF1E1B2E) else Color.White
    val btnTextColor = if (isDarkMode) Color.White else Color(0xFF0F172A)
    val guestTextColor = if (isDarkMode) Color(0xFF8B92A5) else Color(0xFF64748B)
    val footerTextColor = if (isDarkMode) Color(0xFF5A5E70) else Color(0xFF94A3B8)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // Big Squircle B Logo
            Surface(
                modifier = Modifier.size(144.dp),
                shape = RoundedCornerShape(32.dp),
                color = GoldAccent,
                shadowElevation = 16.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "B",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 68.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // App Name Title
            Text(
                text = "Buck Manager",
                color = titleColor,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle
            Text(
                text = "SECURE. PRIVATE. ESSENTIAL.",
                color = subtitleColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.5.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Continue with Google Button
            Surface(
                onClick = {
                    coroutineScope.launch {
                        try {
                            val webClientId = BuildConfig.GOOGLE_WEB_CLIENT_ID
                            if (webClientId == "YOUR_GOOGLE_WEB_CLIENT_ID" || webClientId.isEmpty()) {
                                // Fallback for prototype testing if no Web Client ID is configured
                                onLoginSuccess("zen.zero.rds@gmail.com")
                                return@launch
                            }
                            val googleIdOption = GetGoogleIdOption.Builder()
                                .setFilterByAuthorizedAccounts(false)
                                .setServerClientId(webClientId)
                                .setAutoSelectEnabled(true)
                                .build()

                            val request = GetCredentialRequest.Builder()
                                .addCredentialOption(googleIdOption)
                                .build()

                            val result = credentialManager.getCredential(context = context, request = request)
                            val credential = result.credential

                            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                                onLoginSuccess(googleIdTokenCredential.id)
                            }
                        } catch (e: Exception) {
                            Log.e("LoginScreen", "Google sign in failed", e)
                            // Do not auto-login on failure — user must retry or use guest mode
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                color = btnBgColor,
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = "Google Sign In",
                        tint = Color(0xFF388E3C),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Continue with Google",
                        color = btnTextColor,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Guest option
            TextButton(
                onClick = onContinueAsGuest
            ) {
                Text(
                    text = "Continue as Guest",
                    color = guestTextColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Footer note
            Text(
                text = "Google Sign-in is required to enable secure, encrypted backups of your financial data to your personal Google Drive.",
                color = footerTextColor,
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp,
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }
    }
}

