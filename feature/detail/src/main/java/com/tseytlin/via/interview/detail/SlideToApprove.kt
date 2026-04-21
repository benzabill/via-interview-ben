package com.tseytlin.via.interview.detail

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private val TrackHeight = 64.dp
private val TrackColor = Color(0xFF143C48)
private val ThumbColor = Color.White
private val ThumbContentColor = Color(0xFF143C48)
private val LabelColor = Color.White

@Composable
fun SlideToApprove(
    label: String,
    enabled: Boolean,
    onApprove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(TrackHeight)
            .clip(RoundedCornerShape(TrackHeight / 2))
            .background(TrackColor)
            .alpha(if (enabled) 1f else 0.5f),
        contentAlignment = Alignment.CenterStart,
    ) {
        val density = LocalDensity.current
        val trackWidthPx = with(density) { maxWidth.toPx() }
        val thumbSizePx = with(density) { TrackHeight.toPx() }
        val maxOffsetPx = (trackWidthPx - thumbSizePx).coerceAtLeast(0f)

        val offsetX = remember { Animatable(0f) }
        val scope = rememberCoroutineScope()

        LaunchedEffect(enabled) {
            if (!enabled) offsetX.snapTo(0f)
        }

        Text(
            text = label,
            color = LabelColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.align(Alignment.Center),
        )

        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                .size(TrackHeight)
                .clip(CircleShape)
                .background(ThumbColor)
                .pointerInput(enabled, maxOffsetPx) {
                    if (!enabled) return@pointerInput
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            val reachedMax = offsetX.value >= maxOffsetPx - 1f
                            scope.launch {
                                offsetX.animateTo(0f)
                            }
                            if (reachedMax) onApprove()
                        },
                        onDragCancel = {
                            scope.launch { offsetX.animateTo(0f) }
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            val next = (offsetX.value + dragAmount).coerceIn(0f, maxOffsetPx)
                            scope.launch { offsetX.snapTo(next) }
                        },
                    )
                },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = ">>",
                color = ThumbContentColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}
