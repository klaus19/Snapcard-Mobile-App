package com.example.snapcard.ui.camera

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.snapcard.navigation.Screen
import com.example.snapcard.ui.flashcard.FlashcardUiState
import com.example.snapcard.ui.flashcard.FlashcardViewModel
import com.example.snapcard.ui.theme.Background
import com.example.snapcard.ui.theme.Primary
import com.example.snapcard.ui.theme.TextPrimary
import com.example.snapcard.ui.theme.TextSecondary
import java.io.File

@Composable
fun CameraScreen(
    navController: NavController,
    viewModel: FlashcardViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    // Create a temp file URI for the camera photo
    var photoUri by remember { mutableStateOf<Uri?>(null) }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && photoUri != null) {
            viewModel.generateFlashcards(context, photoUri!!)
        }
    }

    // Gallery launcher (fallback)
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.generateFlashcards(context, it) }
    }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val file = File(context.cacheDir, "snapcard_photo.jpg")
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )
            photoUri = uri
            cameraLauncher.launch(uri)
        }
    }

    // Navigate to flashcards when ready
    LaunchedEffect(uiState) {
        if (uiState is FlashcardUiState.Success) {
            navController.navigate(Screen.Flashcards.route)
        }
    }

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

            when (uiState) {
                is FlashcardUiState.Loading -> {
                    LoadingContent()
                }
                is FlashcardUiState.Error -> {
                    ErrorContent(
                        message = (uiState as FlashcardUiState.Error).message,
                        onRetry = { viewModel.reset() }
                    )
                }
                else -> {
                    ScanContent(
                        onCameraClick = {
                            val hasCameraPermission = ContextCompat.checkSelfPermission(
                                context, Manifest.permission.CAMERA
                            ) == PackageManager.PERMISSION_GRANTED

                            if (hasCameraPermission) {
                                val file = File(context.cacheDir, "snapcard_photo.jpg")
                                val uri = FileProvider.getUriForFile(
                                    context,
                                    "${context.packageName}.provider",
                                    file
                                )
                                photoUri = uri
                                cameraLauncher.launch(uri)
                            } else {
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        },
                        onGalleryClick = {
                            galleryLauncher.launch("image/*")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ScanContent(onCameraClick: () -> Unit, onGalleryClick: () -> Unit) {
    Text(text = "📚", fontSize = 64.sp)

    Spacer(modifier = Modifier.height(24.dp))

    Text(
        text = "Scan Your Page",
        style = MaterialTheme.typography.headlineMedium,
        color = TextPrimary
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = "Take a clear photo of your\ntextbook or notes page",
        style = MaterialTheme.typography.bodyLarge,
        color = TextSecondary,
        textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(48.dp))

    // Camera button
    Button(
        onClick = onCameraClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Primary)
    ) {
        Text(text = "📷  Open Camera", color = TextPrimary,
            style = MaterialTheme.typography.titleLarge)
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Gallery button
    OutlinedButton(
        onClick = onGalleryClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
    ) {
        Text(text = "🖼️  Choose from Gallery",
            style = MaterialTheme.typography.titleLarge)
    }
}

@Composable
fun LoadingContent() {
    CircularProgressIndicator(
        color = Primary,
        modifier = Modifier.size(56.dp),
        strokeWidth = 4.dp
    )
    Spacer(modifier = Modifier.height(24.dp))
    Text(
        text = "Generating flashcards...",
        style = MaterialTheme.typography.titleLarge,
        color = TextPrimary
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = "AI is reading your page",
        style = MaterialTheme.typography.bodyMedium,
        color = TextSecondary
    )
}

@Composable
fun ErrorContent(message: String, onRetry: () -> Unit) {
    Text(text = "❌", fontSize = 48.sp)
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = "Something went wrong",
        style = MaterialTheme.typography.titleLarge,
        color = TextPrimary
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = message,
        style = MaterialTheme.typography.bodyMedium,
        color = TextSecondary,
        textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(24.dp))
    Button(
        onClick = onRetry,
        colors = ButtonDefaults.buttonColors(containerColor = Primary)
    ) {
        Text("Try Again", color = TextPrimary)
    }
}