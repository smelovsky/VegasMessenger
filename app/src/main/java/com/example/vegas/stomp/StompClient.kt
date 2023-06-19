package com.example.vegas.stomp

import com.example.vegas.db.MessageEntity


interface StompClient {

    fun initStompClient(addMessage: (message: MessageEntity)-> Unit)

    fun sendMessage(messageSocket: MessageSocket)
}