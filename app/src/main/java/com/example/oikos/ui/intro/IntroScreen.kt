package com.example.oikos.ui.intro

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.oikos.R
import com.example.oikos.theme.OikosBackground
import com.example.oikos.theme.OikosForestGreen
import com.example.oikos.theme.OikosForestGreenContainer
import com.example.oikos.theme.OikosForestGreenLight
import com.example.oikos.theme.OikosSurface
import com.example.oikos.theme.OikosSurfaceBorder
import com.example.oikos.theme.OikosSurfaceVariant
import com.example.oikos.theme.OikosTextPrimary
import com.example.oikos.theme.OikosTextSecondary
import com.example.oikos.theme.OikosTextTertiary
import com.example.oikos.theme.OikosWarmHoney
import com.example.oikos.theme.OikosWarmHoneyContainer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun IntroScreen(
    modifier: Modifier = Modifier,
    onIntroFinished: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    var isFinished by remember { mutableStateOf(false) }

    fun triggerFinish() {
        if (!isFinished) {
            isFinished = true
            onIntroFinished()
        }
    }

    // Logo entrance & breathing
    val logoAlpha = remember { Animatable(0f) }
    val logoScale = remember { Animatable(0.72f) }

    // Breathing pulse
    val infiniteTransition = rememberInfiniteTransition(label = "IntroBreathing")
    val breathingScale by infiniteTransition.animateFloat(
        initialValue = 0.99f,
        targetValue = 1.025f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "BreathingScale"
    )

    // Text & Subtitle entrance
    val textAlpha = remember { Animatable(0f) }
    val textSlideY = remember { Animatable(24f) }

    // Progress bar animation
    val progress = remember { Animatable(0f) }
    var statusText by remember { mutableStateOf("Preparing your kitchen...") }
    val footerAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Step 1: Logo spring in
        launch {
            logoAlpha.animateTo(1f, tween(450, easing = EaseOutCubic))
        }
        launch {
            logoScale.animateTo(
                1f,
                spring(dampingRatio = 0.62f, stiffness = Spring.StiffnessLow)
            )
        }

        // Step 2: Text fades and slides in
        delay(220)
        launch {
            textAlpha.animateTo(1f, tween(400, easing = EaseOutCubic))
        }
        launch {
            textSlideY.animateTo(0f, tween(400, easing = EaseOutCubic))
        }

        // Step 3: Footer progress
        delay(150)
        launch {
            footerAlpha.animateTo(1f, tween(300))
        }

        // Step 4: Progress stages
        launch {
            progress.animateTo(0.45f, tween(450, easing = FastOutSlowInEasing))
            statusText = "Organizing pantry aisles..."
            progress.animateTo(0.85f, tween(450, easing = FastOutSlowInEasing))
            statusText = "Syncing grocery intelligence..."
            progress.animateTo(1f, tween(300, easing = FastOutSlowInEasing))
            statusText = "Welcome to Oikos"
            try {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            } catch (_: Exception) {}
            delay(240)
            triggerFinish()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(OikosBackground)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                triggerFinish()
            },
        contentAlignment = Alignment.Center
    ) {
        // Decorative ambient glow backdrop
        Box(
            modifier = Modifier
                .size(320.dp)
                .scale(breathingScale)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            OikosForestGreenContainer.copy(alpha = 0.65f),
                            OikosWarmHoneyContainer.copy(alpha = 0.25f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        // Main Brand Content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            // Animated Card Logo
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .scale(logoScale.value * breathingScale)
                    .alpha(logoAlpha.value)
                    .shadow(
                        elevation = 16.dp,
                        shape = RoundedCornerShape(38.dp),
                        spotColor = OikosForestGreen.copy(alpha = 0.18f),
                        ambientColor = OikosWarmHoney.copy(alpha = 0.12f)
                    )
                    .clip(RoundedCornerShape(38.dp))
                    .background(OikosSurface)
                    .padding(18.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.app_logo),
                    contentDescription = "Oikos Brand Emblem",
                    modifier = Modifier.size(136.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Animated Typography
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .offset { IntOffset(0, textSlideY.value.roundToInt()) }
                    .alpha(textAlpha.value)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Eco,
                        contentDescription = null,
                        tint = OikosForestGreen,
                        modifier = Modifier
                            .size(20.dp)
                            .padding(end = 6.dp)
                    )
                    Text(
                        text = "O I K O S",
                        style = MaterialTheme.typography.displayMedium,
                        color = OikosForestGreen,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 6.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "ORGANIC · MINDFUL · PANTRY",
                    style = MaterialTheme.typography.labelMedium,
                    color = OikosWarmHoney,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 3.sp
                )
            }
        }

        // Bottom Loading Bar & Tagline
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 44.dp)
                .alpha(footerAlpha.value)
        ) {
            // Gradient Capsule Progress Track
            Box(
                modifier = Modifier
                    .width(170.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(OikosSurfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction = progress.value.coerceIn(0f, 1f))
                        .height(4.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    OikosForestGreen,
                                    OikosForestGreenLight,
                                    OikosWarmHoney
                                )
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = statusText,
                style = MaterialTheme.typography.bodySmall,
                color = OikosTextSecondary,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.3.sp
            )
        }
    }
}
