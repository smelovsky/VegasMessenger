package com.example.vegas.stomp

import java.time.LocalDateTime

data class MessageSocket(
    val id: Long = 0,
    val text: String,
    val author: String,
    val datetime: LocalDateTime,
    var receiver: String? = null
)