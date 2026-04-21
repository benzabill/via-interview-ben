package com.tseytlin.via.interview.detail

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun SlideToApprove(
    label: String,
    enabled: Boolean,
    onApprove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var committed by remember { mutableStateOf(false) }
    val offsetX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    val commitProgress by animateFloatAsState(
        targetValue = if (committed) 1f else 0f,
        animationSpec = tween(durationMillis = 240),
        label = "commitProgress",
    )

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(ThumbHeight),
        contentAlignment = Alignment.CenterStart,
    ) {
        val density = LocalDensity.current
        val trackWidthPx = with(density) { maxWidth.toPx() }
        val thumbWidthPx = with(density) { ThumbWidth.toPx() }
        val thumbStartPx = with(density) { ThumbStartInset.toPx() }
        val maxOffsetPx = (trackWidthPx - thumbWidthPx - thumbStartPx).coerceAtLeast(0f)

        val progress by remember(maxOffsetPx) {
            derivedStateOf {
                if (maxOffsetPx <= 0f) 0f else (offsetX.value / maxOffsetPx).coerceIn(0f, 1f)
            }
        }
        val trackColor by animateColorAsState(
            targetValue = if (committed) TrackLight else lerp(TrackDark, TrackLight, progress),
            label = "trackColor",
        )

        LaunchedEffect(enabled, committed) {
            if (!enabled && !committed) offsetX.snapTo(0f)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(TrackHeight)
                .clip(RoundedCornerShape(TrackCorner))
                .background(trackColor)
                .border(OutlineWidth, TrackBorder, RoundedCornerShape(TrackCorner)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = label,
                color = LabelOnDark,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.alpha((1f - progress).coerceIn(0f, 1f) * if (committed) 0f else 1f),
            )
            Text(
                text = "Approved",
                color = LabelOnLight,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.alpha(if (committed) 1f else 0f),
            )
        }

        Box(
            modifier = Modifier
                .offset { IntOffset((thumbStartPx + offsetX.value).roundToInt(), 0) }
                .size(width = ThumbWidth, height = ThumbHeight)
                .shadow(
                    elevation = ThumbShadowElevation,
                    shape = RoundedCornerShape(ThumbCorner),
                    spotColor = ThumbShadow,
                    ambientColor = ThumbShadow,
                )
                .clip(RoundedCornerShape(ThumbCorner))
                .background(ThumbColor)
                .pointerInput(enabled, committed, maxOffsetPx) {
                    if (!enabled || committed) return@pointerInput
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            val reachedMax = offsetX.value >= maxOffsetPx - 1f
                            if (reachedMax) {
                                scope.launch { offsetX.snapTo(maxOffsetPx) }
                                committed = true
                                onApprove()
                            } else {
                                scope.launch { offsetX.animateTo(0f) }
                            }
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
            Row(
                horizontalArrangement = Arrangement.spacedBy(ChevronSpacing),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .alpha(1f - commitProgress)
                    .scale(1f - commitProgress * 0.4f),
            ) {
                repeat(2) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_slide_chevron),
                        contentDescription = null,
                    )
                }
            }
            Box(
                modifier = Modifier
                    .alpha(commitProgress)
                    .scale(0.5f + commitProgress * 0.5f)
                    .size(CheckBadgeSize)
                    .clip(CircleShape)
                    .background(CheckBadgeColor),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "✓",
                    color = Color.White,
                    fontSize = CheckGlyphSize,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}
