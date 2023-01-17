package com.example.demo.entities

import com.example.demo.responses.ChatMessageResponse
import com.example.demo.models.ChatUser
import java.util.*
import javax.persistence.*

/**
 * Represents a simple message
 * @param[nickname] the nickname of the user who sent the message
 * @param[datetime] the date and time that the message was sent
 * @param[content] the contents of the message
 */
@Entity
@Table(name="messages")
data class Message(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id :Long? = null,
    val nickname: String,
    val datetime: Date = Date(),
    val content: String
){
    constructor(chatMessageResponse: ChatMessageResponse, user: ChatUser) :this(nickname = user.getNickname(), content = chatMessageResponse.content, datetime = chatMessageResponse.datetime)
}
