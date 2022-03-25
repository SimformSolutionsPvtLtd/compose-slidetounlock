package com.simform.slidetounlock

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

/**
 * SlideToUnlock allows to swipe in left, right or in both direction to perform action when its dragged to its threshold.
 *
 * [leftThumbIcon] and [rightThumbIcon] should typically be an [Icon], using an icon from
 * [androidx.compose.material.icons.Icons]. If using a custom icon, note that the typical size for the
 * internal icon is 24 x 24 dp.
 * @param onSwiped the lambda to be invoked when slider reaches the threshold position
 * @param modifier optional [Modifier] for this SlideToUnlock
 * @param sliderAppearance [SliderAppearance] to be applied
 * @param sliderColors [SliderColors] track and thumb colors
 * @param swipeThreshold Threshold value in between 0f to 1f
 * @param sliderThumb Thumbs which needs to be displayed
 * @param content should typically be an [Text]
 *
 * If you need to add an icon just put it inside the [content] slot together with a spacing
 * and a text:
 * [content] should typically be an [Icon], using an icon from
 *
 */
@Composable
fun SlideToUnlock(
    onSwiped: (SliderThumb) -> Unit,
    modifier: Modifier = Modifier,
    sliderAppearance: SliderAppearance = SliderAppearance(),
    sliderColors: SliderColors = SliderDefaults.colors(),
    swipeThreshold: Float = 0.9f,
    sliderThumb: Set<SliderThumb> = setOf(SliderThumb.Right),
    contentPadding: PaddingValues = SliderDefaults.ContentPadding,
    leftThumbIcon: @Composable BoxScope.() -> Unit = {},
    rightThumbIcon: @Composable BoxScope.() -> Unit = {},
    content: @Composable RowScope.() -> Unit
) {
    BoxWithConstraints(
        modifier
            .requiredSizeIn(
                minWidth = sliderAppearance.thumbSize.times(3),
                minHeight = sliderAppearance.thumbSize
            )
    ) {
        //var size by remember { mutableStateOf(0) }
        val size = constraints.maxWidth
        val density = LocalDensity.current
        val offsetLeftX = remember { mutableStateOf(0f) }
        val offsetRightX = remember(sliderAppearance) {
            val offset = size - with(density) { sliderAppearance.thumbSize.toPx() }
            mutableStateOf(offset)
        }

        val shape = RoundedCornerShape(sliderAppearance.cornerRadius)
        val leftThumbModifier = Modifier
            .shadow(
                elevation = sliderAppearance.elevation,
                shape = shape,
                clip = false
            )
            .background(
                color = sliderColors.leftThumbColor().value,
                shape = shape
            )
            .clip(shape)

        val rightThumbModifier = Modifier
            .shadow(
                elevation = sliderAppearance.elevation,
                shape = shape,
                clip = false
            )
            .background(
                color = sliderColors.rightThumbColor().value,
                shape = shape
            )
            .clip(shape)

        Surface(
            elevation = sliderAppearance.elevation,
            shape = shape
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(sliderAppearance.thumbSize)
                    .background(color = sliderColors.trackColor().value)
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(sliderAppearance.thumbSize)
                        .padding(contentPadding),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    content = content
                )
                sliderThumb.forEach {
                    Slider(
                        modifier = when (it) {
                            SliderThumb.Right -> rightThumbModifier
                            SliderThumb.Left -> leftThumbModifier
                        },
                        icon = when (it) {
                            SliderThumb.Right -> rightThumbIcon
                            SliderThumb.Left -> leftThumbIcon
                        },
                        thumbSize = sliderAppearance.thumbSize,
                        trackSize = size,
                        swipeThreshold = swipeThreshold,
                        offsetX = when (it) {
                            SliderThumb.Right -> offsetRightX.value
                            SliderThumb.Left -> offsetLeftX.value
                        },
                        onSwiped = {
                            onSwiped(it)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun Slider(
    modifier: Modifier,
    icon: @Composable BoxScope.() -> Unit,
    thumbSize: Dp,
    trackSize: Int,
    swipeThreshold: Float,
    offsetX: Float,
    onSwiped: () -> Unit
) {
    val offsetXState = remember(offsetX) { mutableStateOf(offsetX) }
    //offsetXState.value = offsetX

    Box(
        modifier = Modifier
            .offset { IntOffset(offsetXState.value.roundToInt(), 0) }
            .size(thumbSize)
            .pointerInput(thumbSize, trackSize, offsetX) {
                detectDragGestures(onDrag = { change, dragAmount ->
                    change.consumeAllChanges()
                    offsetXState.value = (offsetXState.value + dragAmount.x)
                        .coerceIn(0f, trackSize - thumbSize.toPx())
                }, onDragEnd = {
                    val thumbSizePx = thumbSize.toPx()
                    val progress = if (offsetX > 0) {
                        // it's right
                        offsetX - (offsetXState.value - thumbSizePx)
                    } else {
                        // definitely left
                        offsetXState.value + thumbSizePx
                    }
                    if ((progress) > (trackSize * swipeThreshold)) {
                        onSwiped()
                    }
                    offsetXState.value = offsetX
                }, onDragCancel = {
                    offsetXState.value = offsetX
                })
            }
            .then(modifier)
    ) {
        icon()
    }
}

enum class SliderThumb {
    Right,
    Left;
}

/*data class SliderColors(
    val leftThumbColor: Color = MaterialTheme.colors.primaryColor(0xFFE16876),
    val rightThumbColor: Color = Color(0xFFE16876),
    val trackColor: Color = Color(0xFFE4A8AF),
)*/

data class SliderAppearance(
    val thumbSize: Dp = SliderDefaults.ThumbSize,
    val cornerRadius: Dp = SliderDefaults.CornerRadius,
    val elevation: Dp = SliderDefaults.Elevation,
)

object SliderDefaults {
    val ThumbSize = 50.dp
    val CornerRadius = ThumbSize / 2
    val Elevation = 8.dp

    val ContentPadding = PaddingValues(
        start = ThumbSize,
        top = 0.dp,
        end = ThumbSize,
        bottom = 0.dp
    )

    @Composable
    fun ThumbIcon(painter: Painter): SliderThumbIcon = DefaultSliderIcons(painter)

    @Composable
    fun ThumbIcon(imageVector: ImageVector): SliderThumbIcon = DefaultSliderIcons.FromImageVector(
        imageVector = imageVector
    )

    @Composable
    fun ThumbIcon(bitmap: ImageBitmap): SliderThumbIcon = DefaultSliderIcons.FromImageBitmap(
        bitmap = bitmap
    )

    @Composable
    fun ThumbIcon(): SliderThumbIcon = DefaultSliderIcons.none()

    @Composable
    fun colors(
        leftThumbColor: Color = MaterialTheme.colors.primary,
        rightThumbColor: Color = MaterialTheme.colors.primary,
        trackColor: Color = MaterialTheme.colors.primary.copy(alpha = 0.24f)
    ): SliderColors = DefaultSliderColors(
        leftThumbColor,
        rightThumbColor,
        trackColor,
    )
}

private class DefaultSliderIcons(private var painter: Painter? = null) : SliderThumbIcon {
    companion object {
        @Composable
        fun FromImageVector(imageVector: ImageVector): DefaultSliderIcons {
            val painter = rememberVectorPainter(imageVector)
            return DefaultSliderIcons(painter)
        }

        @Composable
        fun FromImageBitmap(bitmap: ImageBitmap): DefaultSliderIcons {
            val painter = remember(bitmap) { BitmapPainter(bitmap) }
            return DefaultSliderIcons(painter)
        }

        fun none(): DefaultSliderIcons {
            return DefaultSliderIcons()
        }
    }

    @Composable
    override fun painter(): Painter? {
        return painter
    }
}

interface SliderThumbIcon {
    @Composable
    fun painter(): Painter?
}

@Immutable
private class DefaultSliderColors(
    private val leftThumbColor: Color,
    private val rightThumbColor: Color,
    private val trackColor: Color,
): SliderColors {

    @Composable
    override fun leftThumbColor(): State<Color> {
        return rememberUpdatedState(leftThumbColor)
    }
    @Composable
    override fun rightThumbColor(): State<Color> {
        return rememberUpdatedState(rightThumbColor)
    }
    @Composable
    override fun trackColor(): State<Color> {
        return rememberUpdatedState(trackColor)
    }
}

interface SliderColors {
    @Composable
    fun leftThumbColor(): State<Color>

    @Composable
    fun rightThumbColor(): State<Color>

    @Composable
    fun trackColor(): State<Color>
}

private fun Modifier.defaultSizeFor(painter: Painter) =
    this.then(
        if (painter.intrinsicSize == Size.Unspecified || painter.intrinsicSize.isInfinite()) {
            DefaultIconSizeModifier
        } else {
            Modifier
        }
    )

private fun Size.isInfinite() = width.isInfinite() && height.isInfinite()

// Default icon size, for icons with no intrinsic size information
private val DefaultIconSizeModifier = Modifier.size(24.dp)
