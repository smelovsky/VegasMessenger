package com.example.vegas.db

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MessageDao {
    @Query("SELECT * FROM message WHERE id=:id")
    suspend fun getOneById(id: Long): MessageEntity?

    @Query("SELECT * FROM message")
    suspend fun getAll(): List<MessageEntity>

    @Query("SELECT * FROM message ORDER BY date_time DESC")
    fun getItems(): DataSource.Factory<Int, MessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAll(messages: List<MessageEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(message: MessageEntity): Long

    @Query("DELETE FROM message")
    suspend fun deleteAll()

}
