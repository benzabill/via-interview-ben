package com.tseytlin.via.interview.home

import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tseytlin.via.interview.domain.model.RequestOutcome
import kotlinx.coroutines.flow.SharedFlow

private val HomeBackground = Color(0xFFDEEFF5)
private val HomeTitleColor = Color(0xFF1A4E63)
private val ButtonBackground = Color(0xFF1A3A4A)
private val SnackbarSuccess = Color(0xFF90C99A)
private val SnackbarError = Color(0xFFE89090)

@Composable
fun HomeScreen(
    outcomeFlow: SharedFlow<RequestOutcome>,
    onCreateRequest: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var lastOutcome by remember { mutableStateOf<RequestOutcome?>(null) }

    LaunchedEffect(Unit) {
        outcomeFlow.collect { outcome ->
            lastOutcome = outcome
            snackbarHostState.showSnackbar(
                message = when (outcome) {
                    is RequestOutcome.Approved -> "Request approved"
                    is RequestOutcome.Rejected -> outcome.message
                    is RequestOutcome.ApprovalFailed -> outcome.message
                },
            )
            lastOutcome = null
        }
    }

    Scaffold(
        containerColor = HomeBackground,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                val isSuccess = lastOutcome is RequestOutcome.Approved
                Snackbar(
                    snackbarData = data,
                    containerColor = if (isSuccess) SnackbarSuccess else SnackbarError,
                    contentColor = Color(0xFF1A1A1A),
                    dismissActionContentColor = Color(0xFF1A1A1A),
                )
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Home",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = HomeTitleColor,
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                ViaLogo()
            }
            Button(
                onClick = onCreateRequest,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ButtonBackground),
            ) {
                Text("Create new request", color = Color.White, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(32.dp))
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
            text = "_VIA",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A3A4A),
        )
    }
}
