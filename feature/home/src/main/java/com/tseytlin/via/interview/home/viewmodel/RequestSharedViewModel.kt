package com.tseytlin.via.interview.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tseytlin.via.interview.domain.model.RequestOutcome
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class RequestSharedViewModel : ViewModel() {

    // replay = 1 so the outcome survives the nav pop: detail emits, then the composable
    // is disposed and Home recomposes — replay lets the fresh collector receive the event.
    // Call consumeOutcome() after showing to prevent re-display on later recomposition.
    private val _outcomeFlow = MutableSharedFlow<RequestOutcome>(replay = 1)
    val outcomeFlow: SharedFlow<RequestOutcome> = _outcomeFlow.asSharedFlow()

    fun emitOutcome(outcome: RequestOutcome) {
        viewModelScope.launch {
            _outcomeFlow.emit(outcome)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun consumeOutcome() {
        _outcomeFlow.resetReplayCache()
    }
}
