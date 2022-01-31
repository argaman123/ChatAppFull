package com.example.demo.models

import com.example.demo.entities.Message
import java.util.*

data class ChatMessage(var from: String, val content: String, val datetime: Date = Date()){
    constructor(message: Message) : this(message.nickname, message.content, message.datetime)
}