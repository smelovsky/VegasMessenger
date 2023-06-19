package com.example.vegas.stomp

import com.example.vegas.db.MessageEntity

fun socketToEntity(messageSocket: MessageSocket) : MessageEntity {
    return MessageEntity(
        0,
        messageSocket.datetime,
        messageSocket.text,
        messageSocket.author,
        messageSocket.receiver
    )
}

fun entityToSocket(messageEntity: MessageEntity) : MessageSocket {
    return MessageSocket(
        messageEntity.id,
        messageEntity.text,
        messageEntity.author,
        messageEntity.datetime,
        messageEntity.receiver
    )
}