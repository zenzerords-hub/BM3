package com.example.buckmanager.utils

import androidx.compose.ui.Modifier
import androidx.compose.foundation.border
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

fun Modifier.customCardStyle(
    shape: Shape,
    backgroundColor: Color,
    useGradient: Boolean = false,
    gradientColors: List<Color> = emptyList(),
    gradientAngle: Float = 0f,
    borderTop: Dp,
    borderRight: Dp,
    borderBottom: Dp,
    borderLeft: Dp,
    borderColor: Color
): Modifier {
    val isUniformBorder = borderTop == borderRight && borderRight == borderBottom && borderBottom == borderLeft && borderTop > 0.dp
    
    return this.then(
        Modifier
            .clip(shape)
            .drawWithCache {
                val bgBrush = if (useGradient && gradientColors.isNotEmpty()) {
                    val angleRad = (gradientAngle % 360) * (Math.PI / 180f)
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val radius = kotlin.math.sqrt((size.width / 2) * (size.width / 2) + (size.height / 2) * (size.height / 2))
                    val start = Offset(
                        (center.x - radius * cos(angleRad)).toFloat(),
                        (center.y - radius * sin(angleRad)).toFloat()
                    )
                    val end = Offset(
                        (center.x + radius * cos(angleRad)).toFloat(),
                        (center.y + radius * sin(angleRad)).toFloat()
                    )
                    Brush.linearGradient(
                        colors = if (gradientColors.size == 1) listOf(gradientColors[0], gradientColors[0]) else gradientColors,
                        start = start,
                        end = end
                    )
                } else {
                    SolidColor(backgroundColor)
                }

                onDrawBehind {
                    drawRect(brush = bgBrush)

                    if (!isUniformBorder) {
                        val topPx = borderTop.toPx()
                        val rightPx = borderRight.toPx()
                        val bottomPx = borderBottom.toPx()
                        val leftPx = borderLeft.toPx()

                        if (topPx > 0) {
                            drawRect(color = borderColor, topLeft = Offset.Zero, size = Size(size.width, topPx))
                        }
                        if (rightPx > 0) {
                            drawRect(color = borderColor, topLeft = Offset(size.width - rightPx, 0f), size = Size(rightPx, size.height))
                        }
                        if (bottomPx > 0) {
                            drawRect(color = borderColor, topLeft = Offset(0f, size.height - bottomPx), size = Size(size.width, bottomPx))
                        }
                        if (leftPx > 0) {
                            drawRect(color = borderColor, topLeft = Offset.Zero, size = Size(leftPx, size.height))
                        }
                    }
                }
            }
            .then(if (isUniformBorder) Modifier.border(borderTop, borderColor, shape) else Modifier)
    )
}
