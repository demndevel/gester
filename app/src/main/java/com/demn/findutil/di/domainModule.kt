package com.demn.findutil.di

import com.demn.domain.usecase.ProcessInputQueryUseCase
import org.koin.dsl.module

val domainModule = module {
    factory<ProcessInputQueryUseCase> { ProcessInputQueryUseCase(get(), get(), get()) }
}