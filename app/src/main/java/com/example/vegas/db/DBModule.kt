package com.example.vegas.db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DBModule {

    @Provides
    fun provideRoomDao(database: MessageDb): MessageDao {
        return database.messageDao
    }

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext appContext: Context): MessageDb {

        val builder: RoomDatabase.Builder<MessageDb> =
            Room.databaseBuilder(appContext, MessageDb::class.java, "messagedb")

        return builder
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
    }
}
