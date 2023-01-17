package com.example.demo.models

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.util.UUID
import org.springframework.security.core.userdetails.User as SpringUser

/**
 * Represents a guest, who isn't stored in the database, and doesn't need a password.
 * He's identified by a random unique id instead of an email, can never be premium, and can't access any account settings etc.
 */
class GuestUser(private val nickname: String) : SpringUser(
    // TODO: Should work but remember to properly check
    UUID.randomUUID().toString(),
    "none",
    true,
    true,
    true,
    true,
    listOf(GrantedAuthority { "GUEST" })
), ChatUser {
    override fun getNickname(): String = nickname
    override fun getID(): String = username
}