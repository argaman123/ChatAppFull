package com.example.demo.models

/**
 * Represent an activity in which the user connected or disconnected from the chat
 * The email and nickname are used to differentiate the user from others in the active user manager
 * @param[email] the email of the said user
 * @param[nickname] the nickname of the said user
 * @param[type] the type of the event: connected / disconnected
 */
data class UserConnectionEvent (val email :String, val nickname :String, val type :String)