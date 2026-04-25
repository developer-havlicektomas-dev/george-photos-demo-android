package dev.havlicektomas.photosapp.feature.detail.di

import dev.havlicektomas.photosapp.feature.detail.presentation.DetailViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val detailPresentationModule = module {
    viewModelOf(::DetailViewModel)
}
