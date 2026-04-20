package com.tseytlin.via.interview.data.di

import com.tseytlin.via.interview.data.service.MockRequestService
import com.tseytlin.via.interview.domain.service.RequestService
import org.koin.dsl.module

val dataModule = module {
    single<RequestService> { MockRequestService() }
}
