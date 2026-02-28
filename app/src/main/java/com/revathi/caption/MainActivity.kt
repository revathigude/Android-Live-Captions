package com.revathi.caption

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.runtime.LaunchedEffect

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CaptionScreen()
        }
    }
}

@Composable
fun CaptionScreen() {

    var isListening by remember { mutableStateOf(false) }
    var finalTranscript by remember { mutableStateOf("") }
    var liveTranscript by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    LaunchedEffect(finalTranscript, liveTranscript) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    val context = LocalContext.current

    val audioRecorder = remember { AudioRecorder(context) }

    val socketManager = remember {
        SarvamWebSocketManager(
            apiKey = "YOUR_API_KEY",
            onTranscript = { text ->
                when {
                    text.startsWith("PARTIAL:") -> {
                        liveTranscript = text.removePrefix("PARTIAL:")
                    }

                    text.startsWith("FINAL:") -> {
                        val finalText = text.removePrefix("FINAL:")
                        finalTranscript += "\n$finalText"
                        liveTranscript = ""
                    }
                }
            })
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                isListening = true
                finalTranscript = "🎙 Listening...\n"
                liveTranscript = ""
                socketManager.connect()

                if (audioRecorder.start()) {

                    CoroutineScope(Dispatchers.IO).launch {
                        while (isListening) {
                            val audioChunk = audioRecorder.readAudio()
                            if (audioChunk != null) {
                                socketManager.sendAudio(audioChunk)
                            }
                        }
                    }
                }
            }
        }   // ✅ CLOSED permissionLauncher block properly


    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            Text(
                text = finalTranscript + "\n" + liveTranscript,
                color = Color.White,
                fontSize = 22.sp,
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (!isListening) {
                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    } else {
                        isListening = false
                        finalTranscript += "\n🔴 Stopped."
                        liveTranscript = ""

                        audioRecorder.stop()
                        socketManager.disconnect()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Text(
                    text = (finalTranscript + "\n" + liveTranscript)
                        .ifEmpty { "Captions will appear here..." },
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}