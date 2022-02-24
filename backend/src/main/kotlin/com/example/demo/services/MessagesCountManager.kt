package com.example.demo.services

import com.example.demo.models.ChatUser
import com.example.demo.models.GuestUser
import com.example.demo.models.RealUser
import com.example.demo.repositories.MessageRepository
import com.example.demo.static.freeUserMessageLimit
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap


/*
So I tried to find most efficient way to get the count of messages sent by a user every time he sends a message.
The obvious solution would be to read it from the database every time a message is sent, but that's a very intensive task.
Another solution would be to save the count inside a column in the user database, but that would still require a database
call every time you send a message, though a much faster one, since there are probably fewer users than messages.
Another solution I thought of is counting the messages once per login, then storing the value inside the Principal,
that would eliminate the need for a database call, but would present an exploit- if you log in with the same account on
multiple windows it will allow you to 100 messages on each window, since the value is stored locally on each Principal,
without being synchronized or stored in any other way.
So that's why I came up with this solution, one "Source of truth" that counts the messages once per login and stores it
here for each user, then clears it after it disconnects.
That would still be pretty heavy on the memory but since we are already storing a list of active users in the RAM, I figured
that shouldn't be an issue in out case.
Also, I know that I could combine MessagesCountManager and ActiveUsersManager, but since I wanted to explain the concept
I separated the files
 */

/**
 * A component that keeps track of the amount of messages sent by each [RealUser] that is currently connected to the chat.
 * Allows for quick access when needed instead of looking in the database every time. (For example, when checking if a
 * free user has reached the [freeUserMessageLimit])
 *
 * The information is stored inside a [ConcurrentHashMap] in the following form:
 *
 * email: [String] -> amount of messages sent thus far: [Long]
 */
@Component
class MessagesCountManager @Autowired constructor(
    private val messageRepository: MessageRepository
) {
    private val messagesCount: ConcurrentHashMap<String, Long> = ConcurrentHashMap()

    /**
     * Saves the message count of a [RealUser] inside of [messagesCount].
     * @param[user] a [ChatUser] who's messages count isn't already being tracked by [messagesCount].
     * Must have an email, and thus must be a [RealUser]
     */
    fun addUser(user :ChatUser){
        user.getEmail()?.let {
            messagesCount[it] ?: run {
                messagesCount[it] = messageRepository.countByEmail(it) ?: 0
            }
            println(it)
            println(messagesCount[it])
        }
    }

    /**
     * Removes the message count of a [RealUser] from [messagesCount].
     * @param[user] a [ChatUser] who's messages count is no longer needed to be tracked (for example, when he disconnects)
     * Must have an email, and thus must be a [RealUser]
     */
    fun removeUser(user: ChatUser){
        user.getEmail()?.let {
            messagesCount.remove(it)
        }
    }

    /**
     * Increases the message count of a [ChatUser] inside of [messagesCount].
     * @param[user] the [ChatUser] that has just sent a message.
     * If the user isn't found inside [messagesCount] uses [addUser] to add him to there.
     * The user must have an email, and thus must be a [RealUser].
     */
    fun increaseCounter(user :ChatUser){
        user.getEmail()?.let { email ->
            messagesCount[email] ?: run {
                addUser(user)
            }
            messagesCount[email]?.let {
                messagesCount[email] = it + 1
            }
        }
    }

    /**
     * @param[user] the [ChatUser] that has just sent a message.
     * If the user isn't found inside [messagesCount] uses [addUser] to add him to there.
     * @return the message count of a [RealUser] or 0 for [GuestUser] - since you can't really keep track of guests, they
     * will just sign out and sign in again to the chat.
     */
    fun getCount(user :ChatUser) :Long {
        if (user.getType() == "Guest") return 0
        user.getEmail()?.let {
            messagesCount[it] ?: run {
                addUser(user)
            }
            return messagesCount[it] ?: 0 // ?
        }
        return 0
    }
}