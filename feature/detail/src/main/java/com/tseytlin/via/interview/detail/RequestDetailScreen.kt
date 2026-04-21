package com.tseytlin.via.interview.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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

    LaunchedEffect(viewModel) {
        viewModel.navigationEvent.collect { outcome ->
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
                )

                RequestCard(request = request, modifier = Modifier.fillMaxWidth())

                Spacer(modifier = Modifier.weight(1f))

                OutlinedButton(
                    onClick = { viewModel.reject(request) },
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(8.dp),
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
                )
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
