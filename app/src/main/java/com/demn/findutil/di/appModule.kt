package com.demn.findutil.di

import com.demn.findutil.ApplicationSearcher
import com.demn.findutil.SearchScreenViewModel
import org.koin.dsl.module

val appModule = module {
    single { ApplicationSearcher(get()) }
    factory { SearchScreenViewModel(get()) }
}