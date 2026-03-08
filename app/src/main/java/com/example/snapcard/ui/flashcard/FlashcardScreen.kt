package com.example.snapcard.ui.flashcard

import com.example.snapcard.model.Flashcard
import com.example.snapcard.model.FlashcardSet
import com.example.snapcard.ui.flashcard.FlashcardUiState
import com.example.snapcard.ui.flashcard.FlashcardViewModel
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

// ── Design System Colors ──
private val ScreenBg = Color(0xFFF8F9FC)
private val CardWhite = Color(0xFFFFFFFF)
private val Indigo = Color(0xFF6366F1)
private val IndigoDeep = Color(0xFF4F46E5)
private val Violet = Color(0xFF8B5CF6)
private val IndigoLight = Color(0xFFEEF2FF)
private val IndigoBorder = Color(0xFFC7D2FE)
private val TextDark = Color(0xFF1E1B4B)
private val TextBody = Color(0xFF4B5563)
private val TextMuted = Color(0xFF9CA3AF)
private val Green = Color(0xFF10B981)
private val GreenLight = Color(0xFFD1FAE5)
private val Amber = Color(0xFFF59E0B)
private val AmberLight = Color(0xFFFEF3C7)
private val BorderColor = Color(0xFFE5E7EB)

@Composable
fun FlashcardScreen(
    navController: NavController,
    viewModel: FlashcardViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val flashcardSet = (uiState as? FlashcardUiState.Success)?.flashcardSet

    var currentIndex by remember { mutableIntStateOf(0) }
    val knownCards = remember { mutableStateSetOf<Int>() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBg)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Ambient glow
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 60.dp, y = (-100).dp)
                .size(300.dp)
                .drawBehind {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Indigo.copy(alpha = 0.04f),
                                Color.Transparent
                            )
                        ),
                        radius = size.minDimension / 2
                    )
                }
        )

        flashcardSet?.let { set ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(12.dp))

                // ── Header ──
                HeaderSection(
                    set = set,
                    onBack = {
                        viewModel.reset()
                        navController.popBackStack()
                    }
                )

                Spacer(modifier = Modifier.height(14.dp))

                // ── Progress ──
                ProgressSection(
                    currentIndex = currentIndex,
                    total = set.flashcards.size,
                    knownCount = knownCards.size
                )

                Spacer(modifier = Modifier.height(20.dp))

                // ── Card ──
                FlipCard(
                    flashcard = set.flashcards[currentIndex],
                    isKnown = knownCards.contains(currentIndex),
                    onToggleKnown = {
                        if (knownCards.contains(currentIndex)) {
                            knownCards.remove(currentIndex)
                        } else {
                            knownCards.add(currentIndex)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // ── Navigation ──
                NavigationSection(
                    currentIndex = currentIndex,
                    total = set.flashcards.size,
                    onPrevious = { if (currentIndex > 0) currentIndex-- },
                    onNext = { if (currentIndex < set.flashcards.size - 1) currentIndex++ }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // ── Dot indicators ──
                DotIndicators(
                    total = set.flashcards.size,
                    currentIndex = currentIndex,
                    knownCards = knownCards,
                    onDotClick = { i -> currentIndex = i }
                )

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

// ═══════════════════════════════════════
// Header
// ═══════════════════════════════════════

@Composable
private fun HeaderSection(set: FlashcardSet, onBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        // Back button
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(CardWhite)
                .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
                .clickable(onClick = onBack),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = TextBody,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = set.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark,
                lineHeight = 24.sp,
                letterSpacing = (-0.3).sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = set.subject,
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                color = TextMuted
            )
        }
    }
}

// ═══════════════════════════════════════
// Progress
// ═══════════════════════════════════════

@Composable
private fun ProgressSection(currentIndex: Int, total: Int, knownCount: Int) {
    val progress = (currentIndex + 1).toFloat() / total
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(400, easing = FastOutSlowInEasing),
        label = "progress"
    )

    Column {
        // Progress bar + counter
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(IndigoLight)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(animatedProgress)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Indigo)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = "${currentIndex + 1}/$total",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Indigo
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Stats pills
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatPill(
                icon = "✓",
                text = "$knownCount known",
                bgColor = if (knownCount > 0) GreenLight else Indigo.copy(alpha = 0.05f),
                borderColor = if (knownCount > 0) Green.copy(alpha = 0.2f) else Indigo.copy(alpha = 0.1f),
                textColor = if (knownCount > 0) Green else TextMuted
            )
            StatPill(
                icon = "○",
                text = "${total - knownCount} remaining",
                bgColor = Amber.copy(alpha = 0.05f),
                borderColor = Amber.copy(alpha = 0.12f),
                textColor = Amber
            )
        }
    }
}

@Composable
private fun StatPill(icon: String, text: String, bgColor: Color, borderColor: Color, textColor: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = icon, fontSize = 11.sp, color = textColor)
            Text(
                text = text,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = textColor
            )
        }
    }
}

// ═══════════════════════════════════════
// Flip Card
// ═══════════════════════════════════════

@Composable
fun FlipCard(
    flashcard: Flashcard,
    isKnown: Boolean,
    onToggleKnown: () -> Unit,
    modifier: Modifier = Modifier
) {
    var flipped by remember { mutableStateOf(false) }

    // Reset flip when card changes
    LaunchedEffect(flashcard) {
        flipped = false
    }

    val rotation by animateFloatAsState(
        targetValue = if (flipped) 180f else 0f,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "card_flip"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { flipped = !flipped }
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 16f * density
            }
    ) {
        if (rotation <= 90f) {
            CardFront(flashcard = flashcard)
        } else {
            Box(modifier = Modifier.graphicsLayer { rotationY = 180f }) {
                CardBack(
                    flashcard = flashcard,
                    isKnown = isKnown,
                    onToggleKnown = onToggleKnown
                )
            }
        }
    }
}

// ── Card Front (Question) ──

@Composable
private fun CardFront(flashcard: Flashcard) {
    var showHint by remember { mutableStateOf(false) }

    // Reset hint when card changes
    LaunchedEffect(flashcard) {
        showHint = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = Indigo.copy(alpha = 0.08f)
            )
            .background(CardWhite, RoundedCornerShape(24.dp))
            .border(1.dp, BorderColor, RoundedCornerShape(24.dp))
            .padding(28.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Topic chip
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(IndigoLight)
                    .border(1.dp, IndigoBorder, RoundedCornerShape(20.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = flashcard.topic,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Indigo,
                    letterSpacing = 0.3.sp
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Tap indicator
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(IndigoLight),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🤔", fontSize = 22.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "TAP CARD TO REVEAL",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextMuted,
                letterSpacing = 1.5.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Question
            Text(
                text = flashcard.question,
                fontSize = 19.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextDark,
                textAlign = TextAlign.Center,
                lineHeight = 28.sp,
                letterSpacing = (-0.2).sp,
                modifier = Modifier.weight(1f, fill = false)
            )

            Spacer(modifier = Modifier.weight(1f))

            // Hint section
            if (flashcard.hint.isNotBlank()) {
                AnimatedVisibility(
                    visible = !showHint,
                    exit = fadeOut()
                ) {
                    OutlinedButton(
                        onClick = { showHint = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, IndigoBorder),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Indigo
                        )
                    ) {
                        Text(
                            text = "💡 Show hint",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                AnimatedVisibility(
                    visible = showHint,
                    enter = fadeIn() + slideInVertically { it / 2 }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(AmberLight.copy(alpha = 0.5f))
                            .border(1.dp, Amber.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = buildString {
                                append("Hint: ")
                                append(flashcard.hint)
                            },
                            fontSize = 13.sp,
                            color = TextBody,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

// ── Card Back (Answer) ──

@Composable
private fun CardBack(
    flashcard: Flashcard,
    isKnown: Boolean,
    onToggleKnown: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = Indigo.copy(alpha = 0.15f)
            )
            .background(IndigoDeep, RoundedCornerShape(24.dp))
    ) {
        // Decorative circles
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 40.dp, y = (-40).dp)
                .size(160.dp)
                .background(
                    Color.White.copy(alpha = 0.06f),
                    CircleShape
                )
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = (-30).dp, y = 30.dp)
                .size(120.dp)
                .background(
                    Color.White.copy(alpha = 0.04f),
                    CircleShape
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Answer chip
            Box(
                modifier = Modifier
                    .align(Alignment.Start)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.15f))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "✓ ANSWER",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White.copy(alpha = 0.9f),
                    letterSpacing = 0.8.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Answer text
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = flashcard.answer,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = 28.sp,
                    letterSpacing = 0.1.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mark as known button
            OutlinedButton(
                onClick = onToggleKnown,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f)),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (isKnown) Color.White.copy(alpha = 0.2f) else Color.Transparent,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = if (isKnown) "✓ Marked as known" else "Mark as known",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

// ═══════════════════════════════════════
// Navigation
// ═══════════════════════════════════════

@Composable
private fun NavigationSection(
    currentIndex: Int,
    total: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    val isFirst = currentIndex == 0
    val isLast = currentIndex == total - 1

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Previous
        OutlinedButton(
            onClick = onPrevious,
            enabled = !isFirst,
            modifier = Modifier
                .weight(1f)
                .height(54.dp),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, if (isFirst) BorderColor else IndigoBorder),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = CardWhite,
                contentColor = if (isFirst) TextMuted else Indigo,
                disabledContainerColor = CardWhite,
                disabledContentColor = TextMuted
            )
        ) {
            Text(
                text = "← Previous",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Next
        Button(
            onClick = onNext,
            enabled = !isLast,
            modifier = Modifier
                .weight(1f)
                .height(54.dp)
                .then(
                    if (!isLast) Modifier.shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(16.dp),
                        spotColor = Indigo.copy(alpha = 0.25f)
                    ) else Modifier
                ),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Indigo,
                contentColor = Color.White,
                disabledContainerColor = TextMuted.copy(alpha = 0.4f),
                disabledContentColor = Color.White.copy(alpha = 0.6f)
            )
        ) {
            Text(
                text = "Next →",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// ═══════════════════════════════════════
// Dot Indicators
// ═══════════════════════════════════════

@Composable
private fun DotIndicators(
    total: Int,
    currentIndex: Int,
    knownCards: Set<Int>,
    onDotClick: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        for (i in 0 until total) {
            val isActive = i == currentIndex
            val isKnown = knownCards.contains(i)
            val width by animateDpAsState(
                targetValue = if (isActive) 24.dp else 8.dp,
                animationSpec = tween(300, easing = FastOutSlowInEasing),
                label = "dot_width"
            )

            Box(
                modifier = Modifier
                    .padding(horizontal = 3.dp)
                    .height(8.dp)
                    .widthIn(min = 8.dp)
                    .width(width)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        color = when {
                            isKnown -> Green
                            isActive -> Indigo
                            else -> Indigo.copy(alpha = 0.15f)
                        }
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onDotClick(i) }
            )
        }
    }
}