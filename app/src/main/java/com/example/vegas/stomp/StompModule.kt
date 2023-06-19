package com.example.vegas.stomp

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object StompModule {
    @Provides
    fun provideStompClient(): NaiksoftwareStompClient {
        return NaiksoftwareStompClient()
    }
}
