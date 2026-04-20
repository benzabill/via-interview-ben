package com.tseytlin.via.interview.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tseytlin.via.interview.detail.ui.SlideToApprove
import com.tseytlin.via.interview.detail.viewmodel.RequestDetailViewModel
import com.tseytlin.via.interview.domain.model.Request
import com.tseytlin.via.interview.domain.model.RequestOutcome
import org.koin.androidx.compose.koinViewModel

private val DetailBackground = Color(0xFF1B5061)
private val DetailCardBackground = Color(0xFF2A6B7C)

@Composable
fun RequestDetailScreen(
    request: Request,
    onNavigateBack: (RequestOutcome) -> Unit,
    viewModel: RequestDetailViewModel = koinViewModel(),
) {
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { outcome ->
            onNavigateBack(outcome)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DetailBackground),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "New Request",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(24.dp))
            RequestCard(request = request)
            Spacer(modifier = Modifier.weight(1f))
            ActionBar(
                isLoading = isLoading,
                onReject = { viewModel.reject(request) },
                onApprove = { viewModel.approve(request) },
            )
            Spacer(modifier = Modifier.height(32.dp))
        }

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }
    }
}

@Composable
private fun RequestCard(request: Request) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(DetailCardBackground, RoundedCornerShape(12.dp))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = request.title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
        )
        Text(
            text = request.description,
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.85f),
            lineHeight = 20.sp,
        )
    }
}

@Composable
private fun ActionBar(
    isLoading: Boolean,
    onReject: () -> Unit,
    onApprove: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OutlinedButton(
            onClick = onReject,
            enabled = !isLoading,
            border = BorderStroke(1.dp, Color.White),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.height(56.dp),
        ) {
            Text("Reject", fontSize = 16.sp)
        }
        SlideToApprove(
            onApprove = onApprove,
            enabled = !isLoading,
            modifier = Modifier.weight(1f),
        )
    }
}
