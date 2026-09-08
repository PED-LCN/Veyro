package com.veyro.p2p.features.screenstream

enum class ScreenVideoCodec(val wireName: String) {
    H264("h264"),
    H265("h265");

    companion object {
        fun fromWireName(value: String): ScreenVideoCodec? = entries.firstOrNull {
            it.wireName == value.lowercase()
        }
    }
}

data class ScreenStreamCapabilities(
    val codecs: Set<ScreenVideoCodec>,
    val maximumWidth: Int,
    val maximumHeight: Int,
    val maximumFramesPerSecond: Int,
    val maximumBitrateKbps: Int
) {
    init {
        require(codecs.isNotEmpty()) { "At least one video codec is required" }
        require(maximumWidth in MIN_DIMENSION..MAX_DIMENSION)
        require(maximumHeight in MIN_DIMENSION..MAX_DIMENSION)
        require(maximumFramesPerSecond in 1..MAX_FRAMES_PER_SECOND)
        require(maximumBitrateKbps in MIN_BITRATE_KBPS..MAX_BITRATE_KBPS)
    }
}

data class ScreenStreamProfile(
    val codec: ScreenVideoCodec,
    val width: Int,
    val height: Int,
    val framesPerSecond: Int,
    val bitrateKbps: Int
)

object ScreenStreamNegotiator {
    const val BASELINE_WIDTH = 1920
    const val BASELINE_HEIGHT = 1080
    const val BASELINE_FRAMES_PER_SECOND = 60
    const val BASELINE_BITRATE_KBPS = 12_000

    fun negotiate(
        desktop: ScreenStreamCapabilities,
        mobile: ScreenStreamCapabilities
    ): ScreenStreamProfile? {
        val commonCodecs = desktop.codecs intersect mobile.codecs
        val codec = when {
            ScreenVideoCodec.H264 in commonCodecs -> ScreenVideoCodec.H264
            ScreenVideoCodec.H265 in commonCodecs -> ScreenVideoCodec.H265
            else -> return null
        }

        return ScreenStreamProfile(
            codec = codec,
            width = minOf(desktop.maximumWidth, mobile.maximumWidth, BASELINE_WIDTH),
            height = minOf(desktop.maximumHeight, mobile.maximumHeight, BASELINE_HEIGHT),
            framesPerSecond = minOf(
                desktop.maximumFramesPerSecond,
                mobile.maximumFramesPerSecond,
                BASELINE_FRAMES_PER_SECOND
            ),
            bitrateKbps = minOf(
                desktop.maximumBitrateKbps,
                mobile.maximumBitrateKbps,
                BASELINE_BITRATE_KBPS
            )
        )
    }
}

private const val MIN_DIMENSION = 64
private const val MAX_DIMENSION = 8192
private const val MAX_FRAMES_PER_SECOND = 120
private const val MIN_BITRATE_KBPS = 128
private const val MAX_BITRATE_KBPS = 100_000
