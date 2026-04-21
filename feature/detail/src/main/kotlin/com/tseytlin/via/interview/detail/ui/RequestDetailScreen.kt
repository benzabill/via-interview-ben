package com.tseytlin.via.interview.detail.ui

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.tseytlin.via.interview.detail.viewmodel.RequestDetailViewModel
import com.tseytlin.via.interview.domain.model.Request
import com.tseytlin.via.interview.domain.model.RequestOutcome
import org.koin.androidx.compose.koinViewModel

@Composable
fun RequestDetailScreen(
    onNavigateBack: (RequestOutcome) -> Unit,
) {
    val viewModel: RequestDetailViewModel = koinViewModel()
    val isLoading by viewModel.isLoading.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    StatusBarIconsLight()

    // Block system back while a service call is in flight so the user can't pop the
    // screen mid-request and miss the outcome snackbar.
    BackHandler(enabled = isLoading) {}

    LaunchedEffect(viewModel) {
        viewModel.outcomeEvent.collect { outcome ->
            // Approval failures keep the user on this screen so they can retry
            // (or reject). Success and rejection both navigate back so the
            // snackbar fires on Home.
            when (outcome) {
                is RequestOutcome.ApprovalFailed -> snackbarHostState.showSnackbar(outcome.message)
                else -> onNavigateBack(outcome)
            }
        }
    }

    Scaffold(
        containerColor = DetailBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) { ErrorSnackbar(it) } },
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = ScreenPaddingHorizontal, vertical = ScreenPaddingVertical),
                verticalArrangement = Arrangement.spacedBy(ScreenSectionGap),
            ) {
                Text(
                    text = "New Request",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                )

                RequestCard(request = viewModel.request, modifier = Modifier.fillMaxWidth())

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(ActionRowGap),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinedButton(
                        onClick = { viewModel.reject() },
                        enabled = !isLoading,
                        modifier = Modifier
                            .width(RejectButtonWidth)
                            .height(RejectButtonHeight),
                        shape = RoundedCornerShape(RejectButtonCorner),
                        border = BorderStroke(OutlineWidth, TrackBorder),
                    ) {
                        Text(
                            text = "Reject",
                            color = RejectLabel,
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }

                    SlideToApprove(
                        label = "Slide to approve",
                        enabled = !isLoading,
                        onApprove = { viewModel.approve() },
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            }
        }
    }
}

// Flip the status-bar icons to white while this screen is in composition,
// then restore the previous appearance on dispose so other screens keep
// their defaults. Uses the modern WindowInsetsController API so the choice
// is per-screen rather than a global theme flag.
@Composable
private fun StatusBarIconsLight() {
    val view = LocalView.current
    if (view.isInEditMode) return
    val window = (view.context as? Activity)?.window ?: return
    DisposableEffect(window, view) {
        val controller = WindowCompat.getInsetsController(window, view)
        val previous = controller.isAppearanceLightStatusBars
        controller.isAppearanceLightStatusBars = false
        onDispose { controller.isAppearanceLightStatusBars = previous }
    }
}

@Composable
private fun RequestCard(request: Request, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(CardCorner),
        colors = CardDefaults.cardColors(containerColor = DetailCardBackground),
    ) {
        Column(
            modifier = Modifier.padding(CardPadding),
            verticalArrangement = Arrangement.spacedBy(CardContentGap),
        ) {
            Text(
                text = request.title,
                color = CardTitleColor,
                style = MaterialTheme.typography.titleLarge,
            )
            Text(
                text = request.description,
                color = CardBodyColor,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

// Matches the visual language of Home's error snackbar (red bar, dark-grey
// text, trailing ×), but a little tighter — this is a transient retry cue,
// not a terminal confirmation, so it shouldn't dominate the screen.
@Composable
private fun ErrorSnackbar(data: SnackbarData) {
    Row(
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = ErrorSnackbarHorizontalMargin)
            .fillMaxWidth()
            .height(ErrorSnackbarHeight)
            .clip(RoundedCornerShape(ErrorSnackbarCorner))
            .background(ErrorSnackbarBackground)
            .padding(horizontal = ErrorSnackbarContentPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = data.visuals.message,
            color = ErrorSnackbarTextColor,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
        )
        Box(
            modifier = Modifier
                .size(ErrorSnackbarDismissHitTarget)
                .clickable { data.dismiss() },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "×",
                color = ErrorSnackbarTextColor,
                fontSize = ErrorSnackbarDismissGlyphSize,
            )
        }
    }
}
