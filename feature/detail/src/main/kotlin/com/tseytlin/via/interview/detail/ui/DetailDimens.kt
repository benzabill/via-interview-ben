package com.tseytlin.via.interview.detail.ui

import androidx.compose.ui.unit.dp

// Shared outline width used by both the slide-to-approve track border and
// the reject button outline so they stay visually in sync.
internal val OutlineWidth = 1.dp

// Slider — track
internal val TrackHeight = 48.dp
internal val TrackCorner = 12.dp

// Gap between the track's left edge and the thumb's resting position, so
// the thumb isn't flush with the track border at rest.
internal val ThumbStartInset = 5.dp

// Slider — thumb
internal val ThumbWidth = 53.dp
internal val ThumbHeight = 54.dp
internal val ThumbCorner = 15.dp
internal val CheckBadgeSize = 28.dp
internal val ChevronSpacing = 2.dp

// Screen layout
internal val ScreenPaddingHorizontal = 12.dp
internal val ScreenPaddingVertical = 24.dp
internal val ScreenSectionGap = 24.dp
internal val ActionRowGap = 12.dp

// Request card
internal val CardCorner = 12.dp
internal val CardPadding = 20.dp
internal val CardContentGap = 12.dp

// Reject button — shares height/outline with the slider track so the two
// action affordances read as a single control pair.
internal val RejectButtonWidth = 104.dp
internal val RejectButtonHeight = TrackHeight
internal val RejectButtonCorner = 12.dp
