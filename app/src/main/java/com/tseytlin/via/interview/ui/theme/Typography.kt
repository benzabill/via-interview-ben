package com.tseytlin.via.interview.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

// Figma spec calls for Inter 400 / 15px / 19px line-height / center. Using
// FontFamily.SansSerif (Roboto on Android) as the in-repo stand-in so we don't
// need to bundle a TTF; swap to a FontFamily backed by Font(R.font.inter_*)
// here to pick up real Inter.
private val BodyFontFamily = FontFamily.SansSerif

private val SharedBodyStyle = TextStyle(
    fontFamily = BodyFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 15.sp,
    lineHeight = 19.sp,
    textAlign = TextAlign.Center,
    lineHeightStyle = LineHeightStyle(
        alignment = LineHeightStyle.Alignment.Center,
        trim = LineHeightStyle.Trim.None,
    ),
)

val ViaTypography = Typography(
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
    ),
    // Shared 15/19 body style: card descriptions + snackbar messages.
    bodyMedium = SharedBodyStyle,
    // Shared 15/19 label style: action buttons (Reject, Create new request)
    // and the slide-to-approve track label.
    labelLarge = SharedBodyStyle,
)
