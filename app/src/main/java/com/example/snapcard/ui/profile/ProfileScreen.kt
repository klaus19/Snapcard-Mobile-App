package com.example.snapcard.ui.profile

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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.snapcard.auth.AuthUiState
import com.example.snapcard.auth.AuthViewModel
import com.example.snapcard.data.repository.FlashcardRepository
import kotlinx.coroutines.flow.map

private val LightBg = Color(0xFFF8F9FC)
private val Indigo = Color(0xFF6366F1)
private val IndigoLight = Color(0xFFEEF2FF)
private val IndigoBorder = Color(0xFFC7D2FE)
private val Violet = Color(0xFF8B5CF6)
private val TextDark = Color(0xFF1E1B4B)
private val TextBody = Color(0xFF4B5563)
private val TextMuted = Color(0xFF9CA3AF)
private val CardWhite = Color(0xFFFFFFFF)
private val BorderColor = Color(0xFFE5E7EB)
private val ErrorRed = Color(0xFFEF4444)
private val Green = Color(0xFF10B981)
private val GreenLight = Color(0xFFD1FAE5)

@Composable
fun ProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    val authState by authViewModel.uiState.collectAsState()
    val totalSets by profileViewModel.totalSets.collectAsState()
    val totalCardsStudied by profileViewModel.totalCardsStudied.collectAsState()
    val studyStreak by profileViewModel.studyStreak.collectAsState()

    val user = (authState as? AuthUiState.SignedIn)?.user

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBg)
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Profile",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextDark,
                letterSpacing = (-0.3).sp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // User avatar
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(listOf(Indigo, Violet))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user?.displayName?.firstOrNull()?.uppercase() ?: "U",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = user?.displayName ?: "User",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )

            Text(
                text = user?.email ?: "",
                fontSize = 13.sp,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    icon = "📚",
                    value = "$totalSets",
                    label = "Sets",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    icon = "🧠",
                    value = "$totalCardsStudied",
                    label = "Studied",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    icon = "🔥",
                    value = "$studyStreak",
                    label = "Streak",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Sign out button
            OutlinedButton(
                onClick = {
                    authViewModel.signOut()
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                    brush = Brush.linearGradient(
                        listOf(ErrorRed.copy(alpha = 0.3f), ErrorRed.copy(alpha = 0.3f))
                    )
                ),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed)
            ) {
                Text(
                    text = "Sign Out",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun StatCard(
    icon: String,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = Indigo.copy(alpha = 0.06f)
            )
            .background(CardWhite, RoundedCornerShape(16.dp))
            .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = icon, fontSize = 22.sp)
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = value,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = TextMuted
        )
    }
}