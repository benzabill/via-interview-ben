package com.tseytlin.via.interview.detail.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

@Composable
fun SlideToApprove(
    onApprove: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val thumbSizeDp = 48.dp
    val thumbSizePx = with(density) { thumbSizeDp.toPx() }
    val paddingPx = with(density) { 4.dp.toPx() }

    var containerWidthPx by remember { mutableStateOf(0f) }
    var offsetX by remember { mutableStateOf(0f) }
    val maxOffset = (containerWidthPx - thumbSizePx - paddingPx * 2).coerceAtLeast(0f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(Color.White.copy(alpha = 0.2f))
            .onSizeChanged { containerWidthPx = it.width.toFloat() },
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(
            text = "Slide to approve",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            color = Color.White,
            fontSize = 16.sp,
        )

        Box(
            modifier = Modifier
                .padding(4.dp)
                .offset { IntOffset(offsetX.roundToInt(), 0) }
                .size(thumbSizeDp)
                .clip(CircleShape)
                .background(if (enabled) Color.White else Color.White.copy(alpha = 0.4f))
                .then(
                    if (enabled) {
                        Modifier.draggable(
                            orientation = Orientation.Horizontal,
                            state = rememberDraggableState { delta ->
                                offsetX = (offsetX + delta).coerceIn(0f, maxOffset)
                            },
                            onDragStopped = {
                                if (maxOffset > 0f && offsetX >= maxOffset * 0.85f) {
                                    onApprove()
                                }
                                offsetX = 0f
                            },
                        )
                    } else Modifier
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = ">>",
                color = Color(0xFF1B5061),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
            )
        }
    }
}
