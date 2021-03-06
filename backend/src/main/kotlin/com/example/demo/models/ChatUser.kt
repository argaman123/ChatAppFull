package com.example.demo.models

/**
 * This class is used to pull quick information about all users without the need to access the database
 */
interface ChatUser {
    /**
     * @return the nickname of the chat user, used when sending a message
     */
    fun getNickname() :String

    /**
     * @return the email of the chat user, if any (null for guests)
     */
    fun getEmail() :String?

    /**
     * @return a unique identifier of a any chat user
     */
    fun getID() :String

    /**
     * @return true if the chat user is currently premium
     */
    fun isPremium() :Boolean

    /**
     * @return the type/name of the chat user
     */
    fun getType() :String
}