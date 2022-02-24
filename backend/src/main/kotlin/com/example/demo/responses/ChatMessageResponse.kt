package com.example.demo.responses

import com.example.demo.entities.Message
import java.util.*

data class ChatMessageResponse(var from: String, val content: String, val datetime: Date = Date(), val type: String = "message"){
    constructor(message: Message) : this(message.nickname, message.content, message.datetime)
}