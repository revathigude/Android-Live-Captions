package com.revathi.caption

import android.util.Base64
import okhttp3.*
import okio.ByteString
import org.json.JSONObject

class SarvamWebSocketManager(
    private val apiKey: String,
    private val onTranscript: (String) -> Unit
) {

    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null

    fun connect() {

        val url = "wss://api.sarvam.ai/speech-to-text/ws?" +
                "language-code=unknown&" +
                "model=saaras:v3&" +
                "mode=transcribe&" +
                "sample_rate=16000&" +
                "input_audio_codec=pcm_s16le&" +
                "high_vad_sensitivity=true&" +
                "vad_signals=true"

        val request = Request.Builder()
            .url(url)
            .addHeader("Api-Subscription-Key", apiKey)
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {

            override fun onMessage(webSocket: WebSocket, text: String) {

                val json = JSONObject(text)
                val type = json.optString("type")

                if (type == "partial") {
                    val transcript = json
                        .getJSONObject("data")
                        .optString("transcript")

                    onTranscript("PARTIAL:$transcript")
                }

                if (type == "data") {
                    val transcript = json
                        .getJSONObject("data")
                        .optString("transcript")

                    onTranscript("FINAL:$transcript")
                }
            }
        })
    }
    fun sendAudio(audioBytes: ByteArray) {

        val base64Audio = Base64.encodeToString(audioBytes, Base64.NO_WRAP)

        val audioJson = JSONObject()
        val audioData = JSONObject()

        audioData.put("data", base64Audio)
        audioData.put("sample_rate", "16000")
        audioData.put("encoding", "audio/wav")

        audioJson.put("audio", audioData)

        webSocket?.send(audioJson.toString())
    }

    fun disconnect() {
        webSocket?.close(1000, "Stopped")
    }
}