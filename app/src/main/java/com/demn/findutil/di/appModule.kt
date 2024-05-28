package com.demn.findutil.di

import com.demn.findutil.plugins.PluginRepository
import com.demn.findutil.plugins.PluginRepositoryImpl
import com.demn.findutil.presentation.SearchScreenViewModel
import com.demn.findutil.usecase.ProcessQueryUseCase
import com.demn.findutil.usecase.ProcessQueryUseCaseImpl
import org.koin.dsl.module

val appModule = module {
    factory<ProcessQueryUseCase> { ProcessQueryUseCaseImpl(get()) }
    single<PluginRepository> { PluginRepositoryImpl(get()) }
    factory { SearchScreenViewModel(get(), get()) }
}