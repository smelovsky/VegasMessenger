package com.example.vegas.screens

import com.example.vegas.db.MessageEntity

data class ChatScreenState(
    val messages: List<MessageEntity>,
    val isLoading: Boolean,
    val error: String? = null
)
