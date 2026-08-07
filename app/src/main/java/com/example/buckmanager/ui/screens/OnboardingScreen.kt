package com.example.buckmanager.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.buckmanager.R
import com.example.buckmanager.ui.GoldAccent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle

data class OnboardingPage(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val highlightWord: String = ""
)

val onboardingPages = listOf(
    OnboardingPage(
        title = "Welcome to Buck Manager",
        description = "Your ultimate companion for managing finances with style and precision.",
        icon = Icons.Default.CheckCircle,
        highlightWord = "Buck Manager"
    ),
    OnboardingPage(
        title = "Smart Categorization",
        description = "Easily group your expenses and incomes into custom envelopes.",
        icon = Icons.Default.AutoAwesome,
        highlightWord = "envelopes"
    ),
    OnboardingPage(
        title = "Rich Customizability",
        description = "Make it yours! Change themes, headers, and backgrounds effortlessly.",
        icon = Icons.Default.ColorLens,
        highlightWord = "Make it yours"
    )
)

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(isDarkMode: Boolean, onFinish: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
    val coroutineScope = rememberCoroutineScope()
    
    val bgColor = if (isDarkMode) Color(0xFF0F1117) else Color(0xFFF8FAFC)
    val surfaceColor = if (isDarkMode) Color(0xFF181C26) else Color(0xFFFFFFFF)
    val textColor = if (isDarkMode) Color.White else Color(0xFF121926)
    val subtitleColor = if (isDarkMode) Color(0xFFA0A0AB) else Color(0xFF5A667A)
    val dotColor = if (isDarkMode) Color(0xFF2A2739) else Color(0xFFCBD5E1)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))
            
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth().weight(3f)
            ) { page ->
                val pageData = onboardingPages[page]
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(surfaceColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = pageData.icon,
                            contentDescription = null,
                            tint = GoldAccent,
                            modifier = Modifier.size(64.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(48.dp))
                    
                    Text(
                        text = buildAnnotatedString {
                            val parts = pageData.title.split(pageData.highlightWord)
                            if (parts.size == 2 && pageData.highlightWord.isNotEmpty()) {
                                append(parts[0])
                                withStyle(SpanStyle(color = GoldAccent)) {
                                    append(pageData.highlightWord)
                                }
                                append(parts[1])
                            } else {
                                append(pageData.title)
                            }
                        },
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = buildAnnotatedString {
                            val parts = pageData.description.split(pageData.highlightWord)
                            if (parts.size == 2 && pageData.highlightWord.isNotEmpty()) {
                                append(parts[0])
                                withStyle(SpanStyle(color = GoldAccent)) {
                                    append(pageData.highlightWord)
                                }
                                append(parts[1])
                            } else {
                                append(pageData.description)
                            }
                        },
                        fontSize = 16.sp,
                        color = subtitleColor,
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp
                    )
                }
            }
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(onboardingPages.size) { iteration ->
                    val color = if (pagerState.currentPage == iteration) GoldAccent else dotColor
                    val width = if (pagerState.currentPage == iteration) 24.dp else 8.dp
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .clip(CircleShape)
                            .background(color)
                            .height(8.dp)
                            .width(width)
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(0.5f))
            
            Button(
                onClick = {
                    if (pagerState.currentPage < onboardingPages.size - 1) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    } else {
                        onFinish()
                    }
                },
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 24.dp)
                    .height(56.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (pagerState.currentPage < onboardingPages.size - 1) "Next" else "Get Started",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
