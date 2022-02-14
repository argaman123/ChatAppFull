package com.example.demo.models

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User as SpringUser

class GuestUser(nickname: String) : SpringUser(
    nickname,
    "none",
    true,
    true,
    true,
    true,
    listOf(GrantedAuthority { "GUEST" })
), ChatUser {
    override fun getNickname(): String = username
    override fun getEmail(): String? = null
    override fun isPremium(): Boolean = false
    override fun getType(): String = "Guest"
}