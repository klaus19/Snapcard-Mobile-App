package com.example.snapcard.ui.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.snapcard.auth.AuthUiState
import com.example.snapcard.auth.AuthViewModel
import com.example.snapcard.navigation.Screen

// ── Light Color Palette ────────────────────────────────────────
private val LightBg = Color(0xFFF8F9FC)
private val WarmWhite = Color(0xFFFFFFFF)
private val Indigo = Color(0xFF6366F1)
private val IndigoBorder = Color(0xFFC7D2FE)    // indigo-200
private val Violet = Color(0xFF8B5CF6)
private val Purple = Color(0xFFA855F7)
private val Amber = Color(0xFFF59E0B)
private val Orange = Color(0xFFF97316)
private val TextDark = Color(0xFF1E1B4B)         // indigo-950
private val TextBody = Color(0xFF4B5563)         // gray-600
private val TextMuted = Color(0xFF9CA3AF)        // gray-400
private val CardBg = Color(0xFFFFFFFF)
private val CardBorder = Color(0xFFF3F4F6)       // gray-100
private val FeatureLabel = Color(0xFF312E81)     // indigo-900
private val FeatureDesc = Color(0xFF6B7280)      // gray-500
private val SoftShadow = Color(0x0D6366F1)       // indigo at ~5%

// ── Feature Data ───────────────────────────────────────────────
private data class Feature(
    val icon: String,
    val label: String,
    val desc: String
)

private val features = listOf(
    Feature("📸", "Smart Extract", "OCR-powered text recognition"),
    Feature("🧠", "AI Powered", "AI Vision generates cards"),
    Feature("⚡", "Instant Cards", "Ready in seconds"),
    Feature("💡", "With Hints", "Built-in learning aids"),
)

// ── Home Screen ────────────────────────────────────────────────
@Composable
fun HomeScreen(navController: NavController,authViewModel: AuthViewModel = hiltViewModel()) {

    // Float animation for camera icon
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

    // Gradient shift for button
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
        // Soft ambient indigo glow
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

            val authState by authViewModel.uiState.collectAsState()
            val userName = (authState as? AuthUiState.SignedIn)?.user?.displayName?.split(" ")?.firstOrNull() ?: "User"

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Hi, $userName",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextBody
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(CardBg)
                        .border(1.dp, CardBorder, RoundedCornerShape(10.dp))
                        .clickable {
                            authViewModel.signOut()
                            navController.navigate("login") {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Sign Out",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextBody
                    )
                }
            }

            // ── Camera Icon ────────────────────────────────
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.graphicsLayer { translationY = floatOffset }
            ) {
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .shadow(
                            elevation = 12.dp,
                            shape = RoundedCornerShape(24.dp),
                            ambientColor = Indigo.copy(alpha = 0.15f),
                            spotColor = Indigo.copy(alpha = 0.1f)
                        )
                        .clip(RoundedCornerShape(24.dp))
                        .background(WarmWhite)
                        .border(
                            width = 1.dp,
                            color = IndigoBorder.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(24.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "📷", fontSize = 40.sp)
                }

                // Sparkle badge
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 4.dp, y = (-4).dp)
                        .size(24.dp)
                        .shadow(
                            elevation = 4.dp,
                            shape = CircleShape,
                            ambientColor = Amber.copy(alpha = 0.3f)
                        )
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Amber, Orange)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "✨", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── App Title ──────────────────────────────────
            Text(
                text = "SnapCard",
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextDark,
                letterSpacing = (-0.5).sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ── Tagline ────────────────────────────────────
            Text(
                text = "Snap a textbook page.\nGet instant AI flashcards.",
                fontSize = 15.sp,
                color = TextBody,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(36.dp))

            // ── Feature Grid (2×2) ─────────────────────────
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                for (row in features.chunked(2)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        for (feature in row) {
                            FeatureCard(
                                feature = feature,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // ── CTA Button ─────────────────────────────────
            val buttonGradient = Brush.linearGradient(
                colors = if (gradientPhase < 0.5f)
                    listOf(Indigo, Violet, Purple)
                else
                    listOf(Purple, Violet, Indigo)
            )

            Button(
                onClick = { navController.navigate(Screen.Camera.route) },
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
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                contentPadding = PaddingValues()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(buttonGradient, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "📸  Scan a Page",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = WarmWhite,
                        letterSpacing = 0.3.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Helper text ────────────────────────────────
            Text(
                text = "Supports textbooks, notes & handwritten pages",
                fontSize = 12.sp,
                color = TextMuted,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ── Feature Card Composable ────────────────────────────────────
@Composable
private fun FeatureCard(
    feature: Feature,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(14.dp),
                ambientColor = SoftShadow,
                spotColor = SoftShadow
            )
            .clip(RoundedCornerShape(14.dp))
            .background(CardBg)
            .border(
                width = 1.dp,
                color = CardBorder,
                shape = RoundedCornerShape(14.dp)
            )
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(text = feature.icon, fontSize = 18.sp)

        Column {
            Text(
                text = feature.label,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = FeatureLabel,
                lineHeight = 16.sp
            )
            Text(
                text = feature.desc,
                fontSize = 11.sp,
                color = FeatureDesc,
                lineHeight = 14.sp
            )
        }
    }
}

private val EaseInOutSine = CubicBezierEasing(0.37f, 0f, 0.63f, 1f)