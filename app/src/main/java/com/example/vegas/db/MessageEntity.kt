package com.example.vegas.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.time.ZoneOffset

@Entity(
    tableName = "message"
)

data class MessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "date_time")
    var datetime: LocalDateTime = LocalDateTime.now(ZoneOffset.UTC),
    val text: String,
    val author: String,
    var receiver: String? = null
)