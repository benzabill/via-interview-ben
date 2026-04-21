package com.tseytlin.via.interview.home.viewmodel

import androidx.lifecycle.ViewModel
import com.tseytlin.via.interview.domain.model.RequestOutcome
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

class RequestSharedViewModel : ViewModel() {

    private val outcomeChannel = Channel<RequestOutcome>(Channel.BUFFERED)
    val outcomeFlow: Flow<RequestOutcome> = outcomeChannel.receiveAsFlow()

    fun emitOutcome(outcome: RequestOutcome) {
        outcomeChannel.trySend(outcome)
    }
}
