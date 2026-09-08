# Veyro contributor guidance

## Scope

This repository contains the standalone Android client. Shared Protobuf contracts are
kept in `protocol/` and must remain byte-compatible with the Desktop repository.

## Build and verification

- Use JDK 17.
- Run `./gradlew testDebugUnitTest assembleDebug lintDebug` before handing off changes.
- Add unit tests for protocol negotiation and pure Kotlin logic. Add instrumented tests
  only for behavior that requires Android framework APIs.
- Preserve Android 5 (`minSdk 21`) compatibility unless a feature is explicitly gated
  at runtime. Desktop screen streaming may require a newer Android version, but the rest
  of the application must continue to work on supported older devices.

## Architecture and safety

- Keep Android-to-Android traffic on Nearby Connections and desktop interoperability on
  BLE/GATT plus the authenticated fast channel.
- Treat every remote action and incoming payload as untrusted. Enforce explicit size,
  rate, state, and permission checks before allocating resources or changing the device.
- Persist sensitive identities through the existing Keystore-backed components and trust
  stores. Never log PINs, keys, clipboard contents, screen contents, or media payloads.
- Features that expose device data or remote control must be opt-in and independently
  disableable through `FeatureSettings`.
- Keep Portuguese and English UI support aligned when adding user-facing text.

## Desktop screen streaming

- Implement read-only screen mirroring before virtual-display/second-screen support.
- Use the existing mutually authenticated desktop channel for authorization, capability
  negotiation, lifecycle control, and input events.
- Carry encoded video on a dedicated bounded media stream; do not place continuous video
  frames inside `VeyroMessage` or the BLE control channel.
- Prefer hardware H.264 decoding as the baseline. Negotiate optional codecs only after
  both peers advertise support, and always provide a safe fallback.
- Require a trusted desktop, a locally enabled screen-streaming setting, and visible user
  consent before starting a stream. Stopping or disconnecting must promptly release codec,
  surface, network, and wake-lock resources.
- Keep transport, decoder, and Compose UI behind separate interfaces so Linux and Windows
  desktop implementations can evolve independently.

## Working tree hygiene

- Do not edit or delete unrelated user changes.
- Do not commit build outputs, local SDK paths, `.gradle/`, or secrets.
