package com.demn.findutil.di

import com.demn.domain.usecase.CommandSearcherUseCase
import com.demn.domain.usecase.CommandSearcherUseCaseImpl
import com.demn.domain.usecase.PluginCacheSyncUseCase
import com.demn.domain.usecase.PluginCacheSyncUseCaseImpl
import com.demn.domain.usecase.ProcessInputQueryUseCase
import org.koin.dsl.module

val domainModule = module {
    single<ProcessInputQueryUseCase> { ProcessInputQueryUseCase(get(), get(), get(), get()) }
    single<CommandSearcherUseCase> { CommandSearcherUseCaseImpl(get(), get()) }
    single<PluginCacheSyncUseCase> { PluginCacheSyncUseCaseImpl(get(), get()) }
}