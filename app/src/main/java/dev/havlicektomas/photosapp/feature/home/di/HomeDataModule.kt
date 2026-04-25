package dev.havlicektomas.photosapp.feature.home.di

import dev.havlicektomas.photosapp.feature.home.data.KtorPhotoRemoteDataSource
import dev.havlicektomas.photosapp.feature.home.domain.PhotoRemoteDataSource
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val homeDataModule = module {
    singleOf(::KtorPhotoRemoteDataSource) { bind<PhotoRemoteDataSource>() }
}
