package com.example.vegas.stomp

import android.util.Log
import com.example.vegas.db.GsonLocalDateTimeAdapter
import com.example.vegas.db.MessageEntity
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.dto.LifecycleEvent
import ua.naiksoftware.stomp.dto.StompMessage
import ua.naiksoftware.stomp.provider.OkHttpConnectionProvider
import java.time.LocalDateTime

class NaiksoftwareStompClient:StompClient {

    private var mStompClient: ua.naiksoftware.stomp.StompClient? = null
    private var compositeDisposable: CompositeDisposable? = null

    companion object{
        const val SOCKET_URL = "ws://192.168.1.158:8002/api/v1/chat/websocket"
        const val CHAT_TOPIC = "/topic/chat"
        const val CHAT_LINK_SOCKET = "/api/v1/chat/sock"
    }

    private val gson: Gson = GsonBuilder().registerTypeAdapter(
        LocalDateTime::class.java,
        GsonLocalDateTimeAdapter()
    ).create()

    override fun initStompClient(addMessage: (message: MessageEntity)-> Unit) {

        mStompClient = Stomp.over(
            Stomp.ConnectionProvider.OKHTTP,
            SOCKET_URL/*, headerMap*/)
            .withServerHeartbeat(30000)

        if (compositeDisposable != null) {
            compositeDisposable!!.dispose()
        }
        compositeDisposable = CompositeDisposable()

        if (mStompClient != null) {
            val topicSubscribe = mStompClient!!.topic(CHAT_TOPIC)
                .subscribeOn(Schedulers.io(), false)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ topicMessage: StompMessage ->
                    Log.d(OkHttpConnectionProvider.TAG, topicMessage.payload)
                    val messageSocket: MessageSocket =
                        gson.fromJson(topicMessage.payload, MessageSocket::class.java)
                    val message = socketToEntity(messageSocket)
                    Log.d("zzz", "StompClient: ${messageSocket.author}")
                    addMessage(message)
                },
                    {
                        Log.e(OkHttpConnectionProvider.TAG, "Error!", it)
                    }
                )

            val lifecycleSubscribe = mStompClient!!.lifecycle()
                .subscribeOn(Schedulers.io(), false)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { lifecycleEvent: LifecycleEvent ->
                    when (lifecycleEvent.type!!) {
                        LifecycleEvent.Type.OPENED -> Log.d(OkHttpConnectionProvider.TAG, "Stomp connection opened")
                        LifecycleEvent.Type.ERROR -> Log.e(OkHttpConnectionProvider.TAG, "Error", lifecycleEvent.exception)
                        LifecycleEvent.Type.FAILED_SERVER_HEARTBEAT,
                        LifecycleEvent.Type.CLOSED -> {
                            Log.d(OkHttpConnectionProvider.TAG, "Stomp connection closed")
                        }
                    }
                }

            compositeDisposable!!.add(lifecycleSubscribe)
            compositeDisposable!!.add(topicSubscribe)

            if (!mStompClient!!.isConnected) {
                mStompClient!!.connect()
            }


        } else {
            Log.e(OkHttpConnectionProvider.TAG, "mStompClient is null!")
        }
    }

    override fun sendMessage(messageSocket: MessageSocket) {

        val request = mStompClient!!.send(CHAT_LINK_SOCKET, gson.toJson(messageSocket))

        compositeDisposable?.add(
            request.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        Log.d(OkHttpConnectionProvider.TAG, "Stomp sended")
                    },
                    {
                        Log.e(OkHttpConnectionProvider.TAG, "Stomp error", it)
                    }
                )
        )
    }

}
