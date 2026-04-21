package com.tseytlin.via.interview.detail.di

import com.tseytlin.via.interview.detail.viewmodel.RequestDetailViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val detailModule = module {
    viewModel { RequestDetailViewModel(get()) }
}
