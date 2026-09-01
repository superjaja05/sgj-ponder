package jjs.sgj_ponder.ponderstuff

import kotlin.math.abs
import kotlin.math.floor
import net.createmod.ponder.foundation.PonderScene
import net.createmod.ponder.foundation.ui.PonderUI
import net.minecraft.client.Minecraft
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.TickEvent

/**
 * Keeps every SGJ Ponder storyboard framed consistently during live window resizing.
 *
 * The target depends only on the GUI-scaled viewport and Minecraft's effective GUI scale.
 * Scene identity is used solely to avoid changing other mods' Ponder scenes; all six SGJ
 * storyboards receive the same result for the same viewport.
 */
object PonderSceneScaler {
    /** Moves 35% of the remaining distance per tick to hide resize and GUI-scale jumps. */
    private const val RESIZE_EASING = 0.35f

    /** Avoids insignificant writes once the scene has converged on its target. */
    private const val SCALE_EPSILON = 0.001f

    /** Protects Ponder's transforms from invalid or excessively large extrapolated values. */
    private const val MINIMUM_SCALE = 0.10f
    private const val MAXIMUM_SCALE = 3.0f

    /*
     * These affine profiles were fitted from manually approved viewport samples. Linear
     * profiles intentionally smooth the small amount of human and 0.01-slider measurement
     * noise; a quadratic fit validated worse and was discarded. GUI scale 4 is height-only
     * because its samples remained unchanged across the measured width range.
     */
    private val scaleProfiles = listOf(
        ScaleProfile(
            guiScale = 1.0,
            intercept = -0.015f,
            widthSlope = 0.0f,
            heightSlope = 0.002221f,
        ),
        ScaleProfile(
            guiScale = 2.0,
            intercept = -0.568f,
            widthSlope = 0.000413f,
            heightSlope = 0.002921f,
        ),
        ScaleProfile(
            guiScale = 3.0,
            intercept = -0.771f,
            widthSlope = 0.000918f,
            heightSlope = 0.002857f,
        ),
        ScaleProfile(
            guiScale = 4.0,
            intercept = 0.0f,
            widthSlope = 0.0f,
            heightSlope = 0.001300f,
        ),
    )

    private var registered = false
    private var activeScene: PonderScene? = null
    private var lastWidth = -1
    private var lastHeight = -1
    private var lastGuiScale = Double.NaN
    private var targetScale = Float.NaN

    /** Registers the client tick listener once, even if client setup is invoked repeatedly. */
    fun register() {
        if (registered) return

        registered = true
        MinecraftForge.EVENT_BUS.addListener(::onClientTick)
    }

    /** Updates after each client tick so the scene state is stable before it is measured. */
    private fun onClientTick(event: TickEvent.ClientTickEvent) {
        if (event.phase != TickEvent.Phase.END) return

        val minecraft = Minecraft.getInstance()
        val ponder = minecraft.screen as? PonderUI ?: run {
            clearActiveScene()
            return
        }
        val scene = ponder.activeScene

        if (!PonderScenes.isSgjPonderScene(scene.id)) {
            clearActiveScene()
            return
        }

        val width = minecraft.window.guiScaledWidth
        val height = minecraft.window.guiScaledHeight
        val guiScale = minecraft.window.guiScale
        val sceneChanged = scene !== activeScene
        val viewportChanged = width != lastWidth || height != lastHeight ||
            abs(guiScale - lastGuiScale) > SCALE_EPSILON

        if (sceneChanged || viewportChanged) {
            activeScene = scene
            lastWidth = width
            lastHeight = height
            lastGuiScale = guiScale
            targetScale = calculateTargetScale(width, height, guiScale)
        }

        if (sceneChanged) {
            // Apply immediately so the storyboard's first tick is already fitted.
            applyScale(scene, targetScale)
            return
        }

        // Recheck every tick so storyboard actions cannot leave an SGJ scene at a stale scale.
        val difference = targetScale - scene.scaleFactor
        if (abs(difference) <= SCALE_EPSILON) {
            if (scene.scaleFactor != targetScale) applyScale(scene, targetScale)
            return
        }

        applyScale(scene, scene.scaleFactor + difference * RESIZE_EASING)
    }

    /** Evaluates and bounds the scene-independent target for the current viewport. */
    private fun calculateTargetScale(width: Int, height: Int, guiScale: Double): Float {
        return interpolatedProfileScale(width, height, guiScale)
            .coerceIn(MINIMUM_SCALE, MAXIMUM_SCALE)
    }

    /**
     * Interpolates adjacent profiles when a platform reports a fractional GUI scale. Values
     * beyond the calibrated 1-4 range reuse the nearest profile; viewport dimensions still
     * shrink or grow naturally and therefore continue driving the result.
     */
    private fun interpolatedProfileScale(width: Int, height: Int, guiScale: Double): Float {
        val boundedGuiScale = guiScale.coerceIn(
            scaleProfiles.first().guiScale,
            scaleProfiles.last().guiScale,
        )
        val lowerIndex = floor(boundedGuiScale).toInt()
            .coerceIn(1, scaleProfiles.size) - 1
        val upperIndex = (lowerIndex + 1).coerceAtMost(scaleProfiles.lastIndex)
        val lower = scaleProfiles[lowerIndex]
        val upper = scaleProfiles[upperIndex]

        if (lowerIndex == upperIndex) return lower.evaluate(width, height)

        val interpolation = (boundedGuiScale - lower.guiScale).toFloat()
            .coerceIn(0.0f, 1.0f)
        val lowerScale = lower.evaluate(width, height)
        val upperScale = upper.evaluate(width, height)
        return lowerScale + (upperScale - lowerScale) * interpolation
    }

    /** Applies the scale through Ponder's public scene-builder API. */
    private fun applyScale(scene: PonderScene, scale: Float) {
        scene.builder().scaleSceneView(scale)
    }

    /** Drops cached viewport state whenever Ponder closes or another mod's scene becomes active. */
    private fun clearActiveScene() {
        activeScene = null
        lastWidth = -1
        lastHeight = -1
        lastGuiScale = Double.NaN
        targetScale = Float.NaN
    }

    /** One affine viewport model for a calibrated effective GUI scale. */
    private data class ScaleProfile(
        val guiScale: Double,
        val intercept: Float,
        val widthSlope: Float,
        val heightSlope: Float,
    ) {
        /** Evaluates the profile against GUI-scaled, rather than physical, dimensions. */
        fun evaluate(width: Int, height: Int): Float {
            return intercept + widthSlope * width + heightSlope * height
        }
    }
}
