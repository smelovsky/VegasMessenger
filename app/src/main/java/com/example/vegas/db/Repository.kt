package com.example.vegas.db

import android.util.Log
import com.example.vegas.stomp.MessageSocket
import com.example.vegas.stomp.entityToSocket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Repository @Inject constructor(
    private val messageDao: MessageDao,
) {

    suspend fun loadMessages() {
        return withContext(Dispatchers.IO) {

            //val messages = listOf<MessageEntity>(
            //    MessageEntity(1, LocalDateTime.now(ZoneOffset.UTC), "text1", "author1", "receiver1"),
            //    MessageEntity(2, LocalDateTime.now(ZoneOffset.UTC), "text2", "author2", "receiver2"),
            //)

            messageDao.deleteAll()
            //messageDao.addAll(messages)

            //messageDao.insert(Message(0, LocalDateTime.now(ZoneOffset.UTC), "textX", "authorX", "receiverX"))
        }
    }

    suspend fun getMessages() : List<MessageEntity> {
        return withContext(Dispatchers.IO) {
            return@withContext messageDao.getAll().map {
                MessageEntity(it.id, it.datetime, it.text, it.author, it.receiver)
            }
        }
    }

    suspend fun addMessage(message: MessageEntity) {
        return withContext(Dispatchers.IO) {
            messageDao.insert(message)
        }
    }

    suspend fun addMessageAndSend(message: MessageEntity, sendMessage: (message: MessageSocket)-> Unit) {
        return withContext(Dispatchers.IO) {
            var id = messageDao.insert(message)
            Log.d("zzz", "addMessageAndSend: id={$id}")
            val messageSocket = entityToSocket(message)
            messageSocket.apply { id = id }
            sendMessage(messageSocket)
        }
    }
}