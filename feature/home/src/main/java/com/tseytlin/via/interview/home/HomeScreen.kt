package com.tseytlin.via.interview.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.ui.unit.dp
import com.tseytlin.via.interview.domain.model.RequestOutcome
import com.tseytlin.via.interview.home.viewmodel.RequestSharedViewModel
import org.koin.androidx.compose.koinViewModel

private val ViaLightBlue = Color(0xFFD6EAF0)
private val ButtonBackground = Color(0xFF285976)
private val ButtonBorder = Color(0xFF87D6CD)
private val SuccessGreen = Color(0xFF2E7D32)
private val ErrorPink = Color(0xFFE91E63)
private val ButtonShadowColor = Color(0x29000000)

private class OutcomeSnackbarVisuals(
    override val message: String,
    val isSuccess: Boolean,
) : SnackbarVisuals {
    override val actionLabel: String? = null
    override val duration: SnackbarDuration = SnackbarDuration.Short
    override val withDismissAction: Boolean = false
}

@Composable
fun HomeScreen(
    onCreateRequest: () -> Unit,
    sharedViewModel: RequestSharedViewModel = koinViewModel(),
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(sharedViewModel) {
        sharedViewModel.outcomeFlow.collect { outcome ->
            snackbarHostState.showSnackbar(outcome.toSnackbarVisuals())
        }
    }

    Scaffold(
        containerColor = ViaLightBlue,
        snackbarHost = { InstantSnackbarHost(snackbarHostState) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.weight(231f))
            ViaLogo()
            Spacer(modifier = Modifier.weight(68f))
            CreateRequestButton(onClick = onCreateRequest)
            Spacer(modifier = Modifier.weight(295f))
        }
    }
}

@Composable
private fun ViaLogo() {
    Box(
        modifier = Modifier
            .size(238.dp)
            .clip(CircleShape)
            .background(Color.White),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(id = R.drawable.via_logo),
            contentDescription = "VIA",
            modifier = Modifier.size(width = 160.dp, height = 53.dp),
            contentScale = ContentScale.Fit,
        )
    }
}

@Composable
private fun CreateRequestButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 41.dp)
            .height(48.dp)
            .shadow(
                elevation = 3.dp,
                shape = RoundedCornerShape(12.dp),
                spotColor = ButtonShadowColor,
                ambientColor = ButtonShadowColor,
            )
            .clip(RoundedCornerShape(12.dp))
            .background(ButtonBackground)
            .border(
                width = 1.dp,
                color = ButtonBorder,
                shape = RoundedCornerShape(12.dp),
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Create new request",
            color = Color.White,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

// Renders the currently-visible snackbar without Material's default fade/scale animation,
// to match the Figma spec's 0ms animation-duration.
@Composable
private fun InstantSnackbarHost(hostState: SnackbarHostState) {
    val data = hostState.currentSnackbarData
    if (data != null) {
        OutcomeSnackbar(data)
    }
}

@Composable
private fun OutcomeSnackbar(data: SnackbarData) {
    val container = if ((data.visuals as? OutcomeSnackbarVisuals)?.isSuccess == true) {
        SuccessGreen
    } else {
        ErrorPink
    }
    Snackbar(
        snackbarData = data,
        modifier = Modifier
            .padding(horizontal = 21.dp)
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(4.dp),
        containerColor = container,
        contentColor = Color.White,
    )
}

private fun RequestOutcome.toSnackbarVisuals(): SnackbarVisuals =
    OutcomeSnackbarVisuals(
        message = message,
        isSuccess = this is RequestOutcome.Approved,
    )
