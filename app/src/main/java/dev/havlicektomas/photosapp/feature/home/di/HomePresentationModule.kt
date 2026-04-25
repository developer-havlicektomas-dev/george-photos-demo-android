package dev.havlicektomas.photosapp.feature.home.di

import dev.havlicektomas.photosapp.feature.home.presentation.HomeViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val homePresentationModule = module {
    viewModelOf(::HomeViewModel)
}
