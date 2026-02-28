Why I Built This:

I built this project for a very personal reason.
I have hearing difficulty. Phone calls and real-time conversations often feel isolating. Important words get missed. Context gets lost. Confidence drops.
Using Sarvam AI’s real-time speech-to-text API, I built a live captioning Android app that streams microphone audio and displays real-time captions.
For the first time, conversations felt manageable.
It genuinely feels like I got a part of my hearing back.

While testing this prototype, I also realized how impactful this could be for:

Elderly users
Children with hearing loss
Anyone struggling with phone communication
This project is both a technical exploration and a mission-driven accessibility experiment.

What This App Does :

Streams live microphone audio
Sends audio chunks via WebSocket
Displays low-latency real-time captions
Handles partial and final transcripts
Auto-scrolls conversation
Optimized for reduced delay
Language-forced transcription for better accuracy

Works well for:

Face-to-face conversations
Speaker mode calls (where mic access is allowed)
Technical Architecture
Kotlin
Android AudioRecord API
WebSocket streaming
16kHz mono PCM audio
Chunked audio streaming
Real-time UI updates using Jetpack Compose
Scroll state optimization for live caption display

Key technical challenges explored:

Reducing transcription latency
Handling continuous speech vs sentence breaks
Managing Android audio routing behavior
GSM call microphone restrictions
Real-time UI recomposition efficiency

Current Limitation:

Modern Android systems restrict microphone access during active GSM calls.
This means third-prty apps cannot capture call audio directly due to OS-level telephony policies.
This is a system-level restriction, not a code limitation.

Future Vision:

This project began as a personal accessibility solution.
The long-term vision is bigger:
Deep telephony integration for live call captions
System-level accessibility support
On-device speech processing for privacy
Support for multilingual users
Low-resource optimized models
Integration with Android accessibility services
Real-time telephony captions should not be a luxury feature. They should be standard accessibility infrastructure.

Status:
Prototype stage – actively evolving.

Acknowledgment:

Built using Sarvam AI’s real-time speech-to-text API.
Grateful for technology that enables accessibility innovation.
