package com.example.demo.models

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.util.UUID
import org.springframework.security.core.userdetails.User as SpringUser

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
    override fun getEmail(): String? = null
    override fun isPremium(): Boolean = false
    override fun getType(): String = "Guest"
    override fun getID(): String = username
}