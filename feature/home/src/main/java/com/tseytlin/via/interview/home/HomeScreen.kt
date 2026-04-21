package com.tseytlin.via.interview.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tseytlin.via.interview.domain.model.RequestOutcome
import com.tseytlin.via.interview.home.viewmodel.RequestSharedViewModel
import org.koin.androidx.compose.koinViewModel

private val ViaTeal = Color(0xFF0C3B3A)
private val ViaLightBlue = Color(0xFFD6EAF0)
private val SuccessGreen = Color(0xFF2E7D32)
private val ErrorPink = Color(0xFFE91E63)

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
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { data ->
                    val container = if ((data.visuals as? OutcomeSnackbarVisuals)?.isSuccess == true) {
                        SuccessGreen
                    } else {
                        ErrorPink
                    }
                    Snackbar(
                        snackbarData = data,
                        containerColor = container,
                        contentColor = Color.White,
                    )
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            ViaLogo()
            Spacer(modifier = Modifier.height(48.dp))
            Button(
                onClick = onCreateRequest,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ViaTeal,
                    contentColor = Color.White,
                ),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = "Create new request",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(vertical = 8.dp),
                )
            }
        }
    }
}

@Composable
private fun ViaLogo() {
    Box(
        modifier = Modifier
            .size(160.dp)
            .clip(CircleShape)
            .background(Color.White),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "VIA",
            color = ViaTeal,
            fontSize = 44.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

private fun RequestOutcome.toSnackbarVisuals(): SnackbarVisuals =
    OutcomeSnackbarVisuals(
        message = message,
        isSuccess = this is RequestOutcome.Approved,
    )
