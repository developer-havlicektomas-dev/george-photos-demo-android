package dev.havlicektomas.photosapp.core.di

import dev.havlicektomas.photosapp.core.network.HttpClientFactory
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO
import org.koin.dsl.module

val coreNetworkModule = module {
    single<HttpClientEngine> { CIO.create() }
    single { HttpClientFactory.create(get()) }
}
