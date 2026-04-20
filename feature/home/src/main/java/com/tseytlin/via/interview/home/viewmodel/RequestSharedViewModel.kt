package com.tseytlin.via.interview.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tseytlin.via.interview.domain.model.RequestOutcome
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class RequestSharedViewModel : ViewModel() {

    private val _outcomeFlow = MutableSharedFlow<RequestOutcome>()
    val outcomeFlow: SharedFlow<RequestOutcome> = _outcomeFlow.asSharedFlow()

    fun emitOutcome(outcome: RequestOutcome) {
        viewModelScope.launch {
            _outcomeFlow.emit(outcome)
        }
    }
}
