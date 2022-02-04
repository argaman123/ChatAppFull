package com.example.demo.entities

import com.example.demo.models.ChatMessage
import com.example.demo.models.ChatUser
import com.example.demo.models.RealUser
import java.util.*
import javax.persistence.*


@Entity
@Table(name="messages")
data class Message(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id :Long? = null,
    val nickname: String,
    val email: String? = null,
    val datetime: Date = Date(),
    val content: String
){
    constructor(chatMessage: ChatMessage, user: ChatUser) :this(nickname = user.getNickname(), email = user.getEmail(), content = chatMessage.content, datetime = chatMessage.datetime)
}
