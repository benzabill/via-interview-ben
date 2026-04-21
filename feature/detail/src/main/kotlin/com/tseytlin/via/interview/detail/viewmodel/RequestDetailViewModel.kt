package com.tseytlin.via.interview.detail.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tseytlin.via.interview.domain.model.Request
import com.tseytlin.via.interview.domain.model.RequestOutcome
import com.tseytlin.via.interview.domain.model.RequestResult
import com.tseytlin.via.interview.domain.repository.RequestRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RequestDetailViewModel(
    private val repository: RequestRepository,
) : ViewModel() {

    // Resolved once from the repository so the screen and the action handlers share one
    // instance. The repo is the source of truth for which request is being reviewed.
    val request: Request = repository.currentRequest()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // extraBufferCapacity = 1 so emit never suspends waiting for a collector. The screen's
    // LaunchedEffect attaches a collector before any user action, so in practice a subscriber
    // is always present — but buffering removes the assumption, keeping the VM's coroutine
    // (and the finally block below) unblocked regardless of collector state.
    private val _outcomeEvent = MutableSharedFlow<RequestOutcome>(extraBufferCapacity = 1)
    val outcomeEvent: SharedFlow<RequestOutcome> = _outcomeEvent.asSharedFlow()

    fun approve() = runAction(
        action = { repository.approve(request) },
        successMessage = APPROVED_MESSAGE,
        onSuccess = RequestOutcome::Approved,
        onFailure = { serviceError -> RequestOutcome.ApprovalFailed(serviceError) },
    )

    fun reject() = runAction(
        action = { repository.reject(request) },
        successMessage = REJECTED_MESSAGE,
        onSuccess = RequestOutcome::Rejected,
        onFailure = { RequestOutcome.Rejected(REJECTED_MESSAGE) },
    )

    private fun runAction(
        action: suspend () -> RequestResult,
        successMessage: String,
        onSuccess: (String) -> RequestOutcome,
        onFailure: (serviceError: String) -> RequestOutcome,
    ) {
        // Guard + state reset run synchronously before the launch. If they lived inside
        // the coroutine body, two rapid taps could both queue their launches before either
        // ran, and both would pass the guard. Setting _isLoading here makes the guard
        // effective against same-thread re-entry.
        if (_isLoading.value) return
        _isLoading.value = true
        _successMessage.value = null
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                when (val result = action()) {
                    is RequestResult.Success -> {
                        _successMessage.value = successMessage
                        _outcomeEvent.emit(onSuccess(successMessage))
                    }
                    is RequestResult.Error -> {
                        _errorMessage.value = result.message
                        _outcomeEvent.emit(onFailure(result.message))
                    }
                }
            } finally {
                // finally (not a trailing statement) so isLoading resets even if the coroutine
                // is cancelled mid-flight (VM cleared, parent scope cancelled) or the service
                // throws an unexpected exception.
                _isLoading.value = false
            }
        }
    }

    private companion object {
        const val APPROVED_MESSAGE = "Request approved"
        const val REJECTED_MESSAGE = "Request rejected"
    }
}
