package com.example.demo.models

import com.example.demo.entities.User
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.util.*
import org.springframework.security.core.userdetails.User as SpringUser


class RealUser(
    user: User
) : SpringUser(
    user.email,
    user.password,
    true,
    true,
    true,
    true,
    user.roles.split(",").map { SimpleGrantedAuthority(it) }),
    ChatUser {
    private val _premium = user.premium
    private val _nickname = user.nickname

    override fun getNickname(): String = _nickname
    override fun getEmail(): String? = username
    override fun isPremium(): Boolean {
        _premium?.let {
            return when (it.plan) {
                "subscription" -> true // Not necessarily, depends on implementation
                "one-month" -> it.expiration.after(Date())
                else -> false
            }
        } ?: run {
            return false
        }
    }

    override fun getType(): String = "User"
}