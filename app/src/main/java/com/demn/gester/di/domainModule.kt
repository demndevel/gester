package com.demn.gester.di

import com.demn.domain.usecase.CommandSearcherUseCase
import com.demn.domain.usecase.CommandSearcherUseCaseImpl
import com.demn.domain.usecase.PluginCacheSyncUseCase
import com.demn.domain.usecase.PluginCacheSyncUseCaseImpl
import com.demn.domain.usecase.ProcessInputQueryUseCase
import com.demn.domain.usecase.ProcessInputQueryUseCaseConfig
import org.koin.dsl.module

val domainModule = module {
    single<ProcessInputQueryUseCaseConfig> { ProcessInputQueryUseCaseConfig(50L) }

    single<ProcessInputQueryUseCase> { ProcessInputQueryUseCase(get(), get(), get(), get(), get()) }
    single<CommandSearcherUseCase> { CommandSearcherUseCaseImpl(get(), get()) }
    single<PluginCacheSyncUseCase> { PluginCacheSyncUseCaseImpl(get(), get()) }
}
