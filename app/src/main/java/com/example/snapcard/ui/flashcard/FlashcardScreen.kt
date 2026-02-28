package com.example.snapcard.ui.flashcard

import com.example.snapcard.model.Flashcard
import com.example.snapcard.ui.flashcard.FlashcardUiState
import com.example.snapcard.ui.flashcard.FlashcardViewModel
import com.example.snapcard.ui.theme.Accent
import com.example.snapcard.ui.theme.Background
import com.example.snapcard.ui.theme.Primary
import com.example.snapcard.ui.theme.SurfaceLight
import com.example.snapcard.ui.theme.TextPrimary
import com.example.snapcard.ui.theme.TextSecondary
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.snapcard.ui.theme.Surface


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardScreen(
    navController: NavController,
    viewModel: FlashcardViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val flashcardSet = (uiState as? FlashcardUiState.Success)?.flashcardSet

    var currentIndex by remember { mutableIntStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        flashcardSet?.let { set ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                // Top bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        viewModel.reset()
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = set.title,
                            style = MaterialTheme.typography.titleLarge,
                            color = TextPrimary
                        )
                        Text(
                            text = set.subject,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Progress bar
                LinearProgressIndicator(
                    progress = { (currentIndex + 1).toFloat() / set.flashcards.size },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = Primary,
                    trackColor = Surface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${currentIndex + 1} of ${set.flashcards.size}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    modifier = Modifier.align(Alignment.End)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Flashcard
                FlipCard(
                    flashcard = set.flashcards[currentIndex],
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Navigation buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { if (currentIndex > 0) currentIndex-- },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        enabled = currentIndex > 0,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = TextPrimary
                        )
                    ) {
                        Text("← Previous")
                    }

                    Button(
                        onClick = {
                            if (currentIndex < set.flashcards.size - 1) currentIndex++
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        enabled = currentIndex < set.flashcards.size - 1,
                        colors = ButtonDefaults.buttonColors(containerColor = Primary)
                    ) {
                        Text("Next →", color = TextPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun FlipCard(flashcard: Flashcard, modifier: Modifier = Modifier) {
    var flipped by remember { mutableStateOf(false) }

    val rotation by animateFloatAsState(
        targetValue = if (flipped) 180f else 0f,
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
        label = "card_flip"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .clickable { flipped = !flipped }
            .graphicsLayer { rotationY = rotation }
    ) {
        if (rotation <= 90f) {
            // Front — Question
            CardFace(
                emoji = "❓",
                label = "TAP TO REVEAL",
                topicTag = flashcard.topic,
                mainText = flashcard.question,
                hintText = "Hint: ${flashcard.hint}",
                cardColor = Surface
            )
        } else {
            // Back — Answer (counter-rotate so text is readable)
            Box(modifier = Modifier.graphicsLayer { rotationY = 180f }) {
                CardFace(
                    emoji = "✅",
                    label = "ANSWER",
                    topicTag = flashcard.topic,
                    mainText = flashcard.answer,
                    hintText = null,
                    cardColor = SurfaceLight
                )
            }
        }
    }
}

@Composable
fun CardFace(
    emoji: String,
    label: String,
    topicTag: String,
    mainText: String,
    hintText: String?,
    cardColor: androidx.compose.ui.graphics.Color
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(cardColor)
            .padding(28.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Topic tag
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Background)
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = topicTag,
                    style = MaterialTheme.typography.labelLarge,
                    color = Accent
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(text = emoji, fontSize = 40.sp)

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = TextSecondary,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = mainText,
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                lineHeight = 28.sp
            )

            hintText?.let {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
