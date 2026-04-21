package com.tseytlin.via.interview.detail.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tseytlin.via.interview.domain.model.Request
import com.tseytlin.via.interview.domain.model.RequestOutcome
import com.tseytlin.via.interview.domain.model.RequestResult
import com.tseytlin.via.interview.domain.service.RequestService
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RequestDetailViewModel(
    private val requestService: RequestService,
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<RequestOutcome>()
    val navigationEvent: SharedFlow<RequestOutcome> = _navigationEvent.asSharedFlow()

    fun approve(request: Request) = runAction(
        action = { requestService.approve(request) },
        successMessage = "Request approved",
        outcomeErrorMessage = "Request rejected",
        successOutcome = RequestOutcome::Approved,
        errorOutcome = RequestOutcome::ApprovalFailed,
    )

    fun reject(request: Request) = runAction(
        action = { requestService.reject(request) },
        successMessage = "Request rejected",
        outcomeErrorMessage = "Request rejected",
        successOutcome = RequestOutcome::Rejected,
        errorOutcome = RequestOutcome::Rejected,
    )

    private fun runAction(
        action: suspend () -> RequestResult,
        successMessage: String,
        outcomeErrorMessage: String,
        successOutcome: (String) -> RequestOutcome,
        errorOutcome: (String) -> RequestOutcome,
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _successMessage.value = null
            _errorMessage.value = null
            when (val result = action()) {
                is RequestResult.Success -> {
                    _successMessage.value = successMessage
                    _navigationEvent.emit(successOutcome(successMessage))
                }
                is RequestResult.Error -> {
                    _errorMessage.value = result.message
                    _navigationEvent.emit(errorOutcome(outcomeErrorMessage))
                }
            }
            _isLoading.value = false
        }
    }
}
