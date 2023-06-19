package com.example.vegas

import android.util.Log
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vegas.db.MessageEntity
import com.example.vegas.db.Repository
import com.example.vegas.screens.ChatScreenState
import com.example.vegas.stomp.MessageSocket
import com.example.vegas.stomp.NaiksoftwareStompClient
import com.example.vegas.stomp.entityToSocket
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ContactItem(
    var id: Int,
    var name: String,
    val res_id: Int = 0,
    var show: MutableState<Boolean>,
    var isSelected: Boolean = false,
    var isEditMode: Boolean = false,
)

@HiltViewModel
class VegasViewModel @Inject constructor(
    private val rep: Repository,
    private val stompClient: NaiksoftwareStompClient,
) : ViewModel() {

    ////////////////////////////////////////////////////////////////////////////////////////////////

    var currentTheme: Int = 0
    var askToExitFromApp: Boolean = true

    var contactLorik: Boolean = true
    var contactAlyona: Boolean = true
    var contactNastik: Boolean = true

    var message = ""
    fun contactReadAllItems() {

        var id: Int = 1
        contactListDetails.add(ContactItem(id++, "Lorik", R.drawable.lorik_01_400x400, mutableStateOf(true)))
        contactListDetails.add(ContactItem(id++, "Alyona", R.drawable.alyona_01_400x400, mutableStateOf(true)))
        contactListDetails.add(ContactItem(id++, "Nastik", R.drawable.nastik_01_400x400, mutableStateOf(true)))

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private val _state = mutableStateOf(
        ChatScreenState(
            messages = listOf(),
            isLoading = false)
    )
    val state: State<ChatScreenState>
        get() = _state

    private val errorHandler = CoroutineExceptionHandler { _, exception ->
        exception.printStackTrace()
        _state.value = _state.value.copy(error = exception.message, isLoading = false)
    }

    init {
        stompClient.initStompClient(::addMessage)

        getMessages()
    }

    private fun getMessages() {

        viewModelScope.launch(errorHandler) {
            rep.loadMessages()
            val messages = rep.getMessages()
            _state.value = _state.value.copy(
                messages = messages,
                isLoading = false)
        }
    }

    private fun addMessage(message: MessageEntity) {
        viewModelScope.launch(errorHandler) {
            rep.addMessage(message)
            val messages = rep.getMessages()
            _state.value = _state.value.copy(
                messages = messages,
                isLoading = false)
        }
    }

    /*
    private fun sendMessage(messageSocket: MessageSocket) {
        stompClient.sendMessage(messageSocket)
    }

    private fun addMessageAndSend(message: MessageEntity) {
        viewModelScope.launch(errorHandler) {
            rep.addMessageAndSend(message, ::sendMessage)

            val messages = rep.getMessages()
            _state.value = _state.value.copy(
                messages = messages,
                isLoading = false)
        }
    }

     */

    private fun sendMessageToEveryOne(messageSocket: MessageSocket) {
        for(contact_index in 0..2) {
            if (viewModel.contactListDetails[contact_index].show.value) {
                messageSocket.receiver = viewModel.contactListDetails[contact_index].name
                stompClient.sendMessage(messageSocket)
            }
        }
    }

    private fun sendMessageToEveryOneUndoubtedly(messageSocket: MessageSocket) {
        for(contact_index in 0..2) {
            messageSocket.receiver = viewModel.contactListDetails[contact_index].name
            stompClient.sendMessage(messageSocket)
        }
    }

    fun addAndSendMessageToEveryOne(text: String, checkShowFlag: Boolean = true) {
        viewModelScope.launch(errorHandler) {
            val message = MessageEntity(text = text, author = "MS")

            if (checkShowFlag) {
                rep.addMessageAndSend(message, ::sendMessageToEveryOne)
            } else {
                rep.addMessageAndSend(message, ::sendMessageToEveryOneUndoubtedly)
            }


            val messages = rep.getMessages()
            _state.value = _state.value.copy(
                messages = messages,
                isLoading = false)

        }

    }


    ////////////////////////////////////////////////////////////////////////////////////////////////

    var contactListDetails = mutableListOf<ContactItem>()

    ////////////////////////////////////////////////////////////////////////////////////////////////



}