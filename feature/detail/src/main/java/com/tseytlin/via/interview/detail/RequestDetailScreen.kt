package com.tseytlin.via.interview.detail

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tseytlin.via.interview.detail.viewmodel.RequestDetailViewModel
import com.tseytlin.via.interview.domain.model.Request
import com.tseytlin.via.interview.domain.model.RequestOutcome
import org.koin.androidx.compose.koinViewModel

private val DetailBackground = Color(0xFF1B5061)
private val DetailCardBackground = Color(0xFF2A6B7C)
private val CardTitleColor = Color.White
private val CardBodyColor = Color(0xFFDCEAEF)
private val RejectOutline = Color.White
private val RejectLabel = Color.White

@Composable
fun RequestDetailScreen(
    request: Request,
    onNavigateBack: (RequestOutcome) -> Unit,
) {
    val viewModel: RequestDetailViewModel = koinViewModel()
    val isLoading by viewModel.isLoading.collectAsState()

    // Block system back while a service call is in flight so the user can't pop the
    // screen mid-request and miss the outcome snackbar.
    BackHandler(enabled = isLoading) {}

    LaunchedEffect(viewModel) {
        viewModel.outcomeEvent.collect { outcome ->
            onNavigateBack(outcome)
        }
    }

    Scaffold(containerColor = DetailBackground) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                Text(
                    text = "New Request",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                )

                RequestCard(request = request, modifier = Modifier.fillMaxWidth())

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinedButton(
                        onClick = { viewModel.reject(request) },
                        enabled = !isLoading,
                        modifier = Modifier
                            .width(104.dp)
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, RejectOutline),
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
                        onApprove = { viewModel.approve(request) },
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

@Composable
private fun RequestCard(request: Request, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DetailCardBackground),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
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
