package com.drdisagree.iconify.xposed.modules.extras.utils.misc

import android.graphics.Color
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.toColorInt

/**
 * Utility object for handling dual-tone icon colors.
 *
 * This handler provides color values for single-tone, background, and fill layers
 * of icons based on the background intensity or luminance of a base color.
 * It is primarily used to ensure icons remain visible and consistent across
 * different themes and background shades.
 */
object DualToneHandler {

    private val darkColor = Colors(
        single = Color.BLACK,
        background = "#3D000000".toColorInt(),
        fill = "#7A000000".toColorInt()
    )

    private val lightColor = Colors(
        single = Color.WHITE,
        background = "#4DFFFFFF".toColorInt(),
        fill = "#FFFFFF".toColorInt()
    )

    /**
     * Returns the single-tone icon color based on the provided dark intensity.
     *
     * @param darkIntensity A value in the range [0, 1] representing how dark the background is.
     * A value greater than 0.5 favors the dark theme color; otherwise the light theme color is used.
     * @return The resolved single-tone color.
     */
    fun getSingleColor(darkIntensity: Float) =
        if (darkIntensity > 0.5f) darkColor.single else lightColor.single

    /**
     * Returns the color with the alpha component set to fully opaque (255).
     *
     * @param color The color to extract the single color from.
     * @return The resulting color with maximum alpha.
     */
    fun getSingleColor(color: Int) =
        if (ColorUtils.calculateLuminance(color) > 0.5) lightColor.single else darkColor.single

    /**
     * Returns the background color based on the provided dark intensity.
     *
     * @param darkIntensity A value in the range [0, 1] representing how dark the background is.
     * A value greater than 0.5 favors the dark theme background; otherwise the light theme background
     * is used.
     * @return The resolved background color.
     */
    fun getBackgroundColor(darkIntensity: Float) =
        if (darkIntensity > 0.5f) darkColor.background else lightColor.background

    /**
     * Returns the background color with an alpha component set based on the luminance of the
     * provided color.
     *
     * @param color The color to extract the background color from.
     * @return The color with alpha adjusted for background usage.
     */
    fun getBackgroundColor(color: Int) =
        if (ColorUtils.calculateLuminance(color) > 0.5) lightColor.background else darkColor.background

    /**
     * Returns the fill color based on the provided dark intensity.
     *
     * @param darkIntensity A value in the range [0, 1] representing how dark the background is.
     * A value greater than 0.5 favors the dark theme fill color; otherwise the light theme fill color
     * is used.
     * @return The resolved fill color.
     */
    fun getFillColor(darkIntensity: Float) =
        if (darkIntensity > 0.5f) darkColor.fill else lightColor.fill

    /**
     * Returns a fill color based on the provided color, adjusting its alpha transparency
     * based on the color's luminance.
     *
     * @param color The color to extract the fill color from.
     * @return The color with alpha adjusted for fill usage.
     */
    fun getFillColor(color: Int) =
        if (ColorUtils.calculateLuminance(color) > 0.5) lightColor.fill else darkColor.fill

    private data class Colors(val single: Int, val background: Int, val fill: Int)
}