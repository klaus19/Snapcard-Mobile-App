package com.example.snapcard.ui.home

import androidx.compose.ui.graphics.Color
import com.example.snapcard.navigation.Screen
import com.example.snapcard.ui.theme.Background
import com.example.snapcard.ui.theme.Primary
import com.example.snapcard.ui.theme.TextPrimary
import com.example.snapcard.ui.theme.TextSecondary
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController


@Composable
fun HomeScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // Icon / Emoji
            Text(
                text = "📸",
                fontSize = 72.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Title
            Text(
                text = "SnapCard",
                style = MaterialTheme.typography.headlineLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle
            Text(
                text = "Click a photo of any textbook page\nand get instant flashcards",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Feature pills
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FeaturePill(text = "📖 Smart Extract")
                FeaturePill(text = "🧠 AI Powered")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FeaturePill(text = "⚡ Instant Cards")
                FeaturePill(text = "💡 Hints Included")
            }

            Spacer(modifier = Modifier.height(56.dp))

            // CTA Button
            Button(
                onClick = { navController.navigate(Screen.Camera.route) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary
                )
            ) {
                Text(
                    text = "📷  Scan a Page",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary
                )
            }
        }
    }
}

@Composable
fun FeaturePill(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color.Red)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
    }
}