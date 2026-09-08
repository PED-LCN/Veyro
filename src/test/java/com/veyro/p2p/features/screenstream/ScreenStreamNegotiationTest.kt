package com.veyro.p2p.features.screenstream

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ScreenStreamNegotiationTest {
    @Test
    fun h264IsTheInteroperableBaselineWhenBothCodecsAreAvailable() {
        val desktop = capabilities(setOf(ScreenVideoCodec.H264, ScreenVideoCodec.H265))
        val mobile = capabilities(setOf(ScreenVideoCodec.H264, ScreenVideoCodec.H265))

        assertEquals(ScreenVideoCodec.H264, ScreenStreamNegotiator.negotiate(desktop, mobile)?.codec)
    }

    @Test
    fun negotiationUsesTheLowestSafeLimits() {
        val desktop = capabilities(
            codecs = setOf(ScreenVideoCodec.H264),
            width = 2560,
            height = 1440,
            framesPerSecond = 30,
            bitrateKbps = 20_000
        )
        val mobile = capabilities(
            codecs = setOf(ScreenVideoCodec.H264),
            width = 1280,
            height = 800,
            framesPerSecond = 60,
            bitrateKbps = 6_000
        )

        assertEquals(
            ScreenStreamProfile(ScreenVideoCodec.H264, 1280, 800, 30, 6_000),
            ScreenStreamNegotiator.negotiate(desktop, mobile)
        )
    }

    @Test
    fun negotiationFailsWhenThereIsNoCommonCodec() {
        val desktop = capabilities(setOf(ScreenVideoCodec.H265))
        val mobile = capabilities(setOf(ScreenVideoCodec.H264))

        assertNull(ScreenStreamNegotiator.negotiate(desktop, mobile))
    }

    private fun capabilities(
        codecs: Set<ScreenVideoCodec>,
        width: Int = 1920,
        height: Int = 1080,
        framesPerSecond: Int = 60,
        bitrateKbps: Int = 12_000
    ) = ScreenStreamCapabilities(codecs, width, height, framesPerSecond, bitrateKbps)
}
