package com.example.snapcard.ui.camera

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.snapcard.navigation.Screen
import com.example.snapcard.ui.flashcard.FlashcardUiState
import com.example.snapcard.ui.flashcard.FlashcardViewModel
import java.io.File

// ── Light Color Palette (shared with HomeScreen) ───────────────
private val LightBg = Color(0xFFF8F9FC)
private val WarmWhite = Color(0xFFFFFFFF)
private val Indigo = Color(0xFF6366F1)
private val IndigoBorder = Color(0xFFC7D2FE)
private val Violet = Color(0xFF8B5CF6)
private val Purple = Color(0xFFA855F7)
private val TextDark = Color(0xFF1E1B4B)
private val TextBody = Color(0xFF4B5563)
private val TextMuted = Color(0xFF9CA3AF)
private val CardBorder = Color(0xFFF3F4F6)
private val ErrorRed = Color(0xFFEF4444)
private val ErrorRedLight = Color(0xFFFEE2E2)
private val ErrorRedBorder = Color(0xFFFCA5A5)

private val EaseInOutSine = CubicBezierEasing(0.37f, 0f, 0.63f, 1f)

@Composable
fun CameraScreen(
    navController: NavController,
    viewModel: FlashcardViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    var photoUri by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && photoUri != null) {
            viewModel.generateFlashcards(photoUri!!)
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.generateFlashcards(it) }
    }

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

    LaunchedEffect(uiState) {
        if (uiState is FlashcardUiState.Success) {
            navController.navigate(Screen.Flashcards.route)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBg)
    ) {
        // Soft ambient indigo glow
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-80).dp)
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

    // Float animation for icon
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

    // Icon
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
            .border(
                width = 1.dp,
                color = IndigoBorder.copy(alpha = 0.4f),
                shape = RoundedCornerShape(24.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "📚", fontSize = 40.sp)
    }

    Spacer(modifier = Modifier.height(24.dp))

    Text(
        text = "Scan Your Page",
        fontSize = 28.sp,
        fontWeight = FontWeight.ExtraBold,
        color = TextDark,
        letterSpacing = (-0.3).sp
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = "Take a clear photo of your\ntextbook or notes page",
        fontSize = 15.sp,
        color = TextBody,
        textAlign = TextAlign.Center,
        lineHeight = 22.sp
    )

    Spacer(modifier = Modifier.height(48.dp))

    // Camera button — gradient
    val buttonGradient = Brush.linearGradient(
        colors = if (gradientPhase < 0.5f)
            listOf(Indigo, Violet, Purple)
        else
            listOf(Purple, Violet, Indigo)
    )

    Button(
        onClick = onCameraClick,
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
            Text(
                text = "📷  Open Camera",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = WarmWhite,
                letterSpacing = 0.3.sp
            )
        }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Gallery button — outlined
    OutlinedButton(
        onClick = onGalleryClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp),
        shape = RoundedCornerShape(16.dp),
        border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
            brush = Brush.linearGradient(
                colors = listOf(
                    IndigoBorder.copy(alpha = 0.6f),
                    Violet.copy(alpha = 0.2f)
                )
            )
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = TextDark
        )
    ) {
        Text(
            text = "🖼️  Choose from Gallery",
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.3.sp
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = "For best results, ensure good lighting",
        fontSize = 12.sp,
        color = TextMuted,
        textAlign = TextAlign.Center
    )
}

@Composable
fun LoadingContent() {

    val infiniteTransition = rememberInfiniteTransition(label = "loading")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing)
        ),
        label = "rotate"
    )

    // Pulsing glow behind spinner
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.05f,
        targetValue = 0.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = Modifier
            .size(100.dp)
            .drawBehind {
                drawCircle(
                    color = Indigo.copy(alpha = pulseAlpha),
                    radius = size.minDimension / 2
                )
            },
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = Indigo,
            modifier = Modifier.size(56.dp),
            strokeWidth = 4.dp
        )
    }

    Spacer(modifier = Modifier.height(24.dp))

    Text(
        text = "Generating flashcards...",
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = TextDark
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = "AI is reading your page",
        fontSize = 14.sp,
        color = TextBody
    )
}

@Composable
fun ErrorContent(message: String, onRetry: () -> Unit) {

    // Error icon container
    Box(
        modifier = Modifier
            .size(80.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = ErrorRed.copy(alpha = 0.15f)
            )
            .clip(RoundedCornerShape(20.dp))
            .background(ErrorRedLight)
            .border(
                width = 1.dp,
                color = ErrorRedBorder.copy(alpha = 0.5f),
                shape = RoundedCornerShape(20.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "❌", fontSize = 36.sp)
    }

    Spacer(modifier = Modifier.height(20.dp))

    Text(
        text = "Something went wrong",
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        color = TextDark
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = message,
        fontSize = 14.sp,
        color = TextBody,
        textAlign = TextAlign.Center,
        lineHeight = 20.sp
    )

    Spacer(modifier = Modifier.height(28.dp))

    Button(
        onClick = onRetry,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(14.dp),
                ambientColor = Indigo.copy(alpha = 0.2f)
            ),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(listOf(Indigo, Violet)),
                    RoundedCornerShape(14.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Try Again",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = WarmWhite
            )
        }
    }
}
