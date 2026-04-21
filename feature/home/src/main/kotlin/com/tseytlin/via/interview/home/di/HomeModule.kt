package com.tseytlin.via.interview.home.di

import com.tseytlin.via.interview.home.viewmodel.RequestSharedViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val homeModule = module {
    viewModel { RequestSharedViewModel() }
}
