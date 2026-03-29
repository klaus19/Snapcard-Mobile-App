package com.example.snapcard.ui.library

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.snapcard.model.FlashcardSet
import java.text.SimpleDateFormat
import java.util.*

private val LightBg = Color(0xFFF8F9FC)
private val Indigo = Color(0xFF6366F1)
private val Violet = Color(0xFF8B5CF6)
private val IndigoLight = Color(0xFFEEF2FF)
private val IndigoBorder = Color(0xFFC7D2FE)
private val TextDark = Color(0xFF1E1B4B)
private val TextBody = Color(0xFF4B5563)
private val TextMuted = Color(0xFF9CA3AF)
private val CardWhite = Color(0xFFFFFFFF)
private val BorderColor = Color(0xFFE5E7EB)
private val ErrorRed = Color(0xFFEF4444)

@Composable
fun LibraryScreen(
    navController: NavController,
    viewModel: LibraryViewModel = hiltViewModel()
) {
    val sets by viewModel.allSets.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBg)
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Header
            Text(
                text = "My Library",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextDark,
                letterSpacing = (-0.3).sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${sets.size} flashcard sets",
                fontSize = 14.sp,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearch(it) },
                placeholder = {
                    Text("Search sets...", color = TextMuted, fontSize = 14.sp)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Indigo,
                    unfocusedBorderColor = BorderColor,
                    focusedContainerColor = CardWhite,
                    unfocusedContainerColor = CardWhite
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (sets.isEmpty()) {
                // Empty state
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "📚", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (searchQuery.isNotEmpty()) "No sets match your search"
                            else "No flashcard sets yet",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (searchQuery.isNotEmpty()) "Try a different search term"
                            else "Scan a page to create your first set",
                            fontSize = 13.sp,
                            color = TextMuted,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    items(sets, key = { it.id }) { set ->
                        SetCard(
                            set = set,
                            onClick = {
                                navController.navigate("flashcards/${set.id}")
                            },
                            onDelete = {
                                viewModel.deleteSet(set.id)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SetCard(
    set: FlashcardSet,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Set", fontWeight = FontWeight.Bold) },
            text = { Text("Delete \"${set.title}\"? This can't be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete", color = ErrorRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = Indigo.copy(alpha = 0.06f)
            )
            .background(CardWhite, RoundedCornerShape(16.dp))
            .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Subject icon
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(IndigoLight)
                .border(1.dp, IndigoBorder, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "📖", fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = set.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextDark,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = set.subject,
                    fontSize = 12.sp,
                    color = Indigo,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "•",
                    fontSize = 12.sp,
                    color = TextMuted
                )
                Text(
                    text = "${set.totalCards} cards",
                    fontSize = 12.sp,
                    color = TextMuted
                )
                Text(
                    text = "•",
                    fontSize = 12.sp,
                    color = TextMuted
                )
                Text(
                    text = formatDate(set.createdAt),
                    fontSize = 12.sp,
                    color = TextMuted
                )
            }
        }

        // Delete button
        IconButton(
            onClick = { showDeleteDialog = true },
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = TextMuted,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM d", Locale.getDefault())
    return sdf.format(Date(timestamp))
}