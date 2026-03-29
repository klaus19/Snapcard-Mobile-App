package com.example.snapcard.login

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.snapcard.auth.AuthUiState
import com.example.snapcard.auth.AuthViewModel

private val LightBg = Color(0xFFF8F9FC)
private val WarmWhite = Color(0xFFFFFFFF)
private val Indigo = Color(0xFF6366F1)
private val IndigoBorder = Color(0xFFC7D2FE)
private val Violet = Color(0xFF8B5CF6)
private val Purple = Color(0xFFA855F7)
private val TextDark = Color(0xFF1E1B4B)
private val TextBody = Color(0xFF4B5563)
private val TextMuted = Color(0xFF9CA3AF)
private val ErrorRed = Color(0xFFEF4444)

private val EaseInOutSine = CubicBezierEasing(0.37f, 0f, 0.63f, 1f)

@Composable
fun LoginScreen(authViewModel: AuthViewModel) {
    val authState by authViewModel.uiState.collectAsState()
    val context = LocalContext.current

    val infiniteTransition = rememberInfiniteTransition(label = "float")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatY"
    )

    val gradientPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradient"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBg)
    ) {
        // Ambient glow
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-100).dp)
                .size(350.dp)
                .drawBehind {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Indigo.copy(alpha = 0.06f),
                                Violet.copy(alpha = 0.03f),
                                Color.Transparent
                            )
                        ),
                        radius = size.minDimension / 2
                    )
                }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Icon
            Box(
                modifier = Modifier
                    .graphicsLayer { translationY = floatOffset }
                    .size(88.dp)
                    .shadow(
                        elevation = 12.dp,
                        shape = RoundedCornerShape(24.dp),
                        ambientColor = Indigo.copy(alpha = 0.15f),
                        spotColor = Indigo.copy(alpha = 0.1f)
                    )
                    .clip(RoundedCornerShape(24.dp))
                    .background(WarmWhite)
                    .border(1.dp, IndigoBorder.copy(alpha = 0.4f), RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "📷", fontSize = 40.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "SnapCard",
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextDark,
                letterSpacing = (-0.5).sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Snap a textbook page.\nGet instant AI flashcards.",
                fontSize = 15.sp,
                color = TextBody,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Error message
            if (authState is AuthUiState.Error) {
                Text(
                    text = (authState as AuthUiState.Error).message,
                    fontSize = 13.sp,
                    color = ErrorRed,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Sign-in button
            val isLoading = authState is AuthUiState.Loading
            val buttonGradient = Brush.linearGradient(
                colors = if (gradientPhase < 0.5f)
                    listOf(Indigo, Violet, Purple)
                else
                    listOf(Purple, Violet, Indigo)
            )

            Button(
                onClick = { authViewModel.signInWithGoogle(context) },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .shadow(
                        elevation = 16.dp,
                        shape = RoundedCornerShape(16.dp),
                        ambientColor = Indigo.copy(alpha = 0.25f),
                        spotColor = Indigo.copy(alpha = 0.2f)
                    ),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(buttonGradient, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = WarmWhite,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 3.dp
                        )
                    } else {
                        Text(
                            text = "Continue with Google",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = WarmWhite,
                            letterSpacing = 0.3.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Sign in to save your flashcards",
                fontSize = 12.sp,
                color = TextMuted,
                textAlign = TextAlign.Center
            )
        }
    }
}