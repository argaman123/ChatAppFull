package com.example.demo.models

import com.example.demo.entities.User
import com.example.demo.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import java.util.*
import org.springframework.security.core.userdetails.User as SpringUser

class RealUser(
    user: User,
    private var userRepository: UserRepository
) : SpringUser(
    user.email,
    user.password,
    true,
    true,
    true,
    true,
    user.roles.split(",").map { SimpleGrantedAuthority(it) }),
    ChatUser {

    private var _premium = user.premium
    private val _nickname = user.nickname

    override fun getNickname(): String = _nickname
    override fun getEmail(): String? = username
    override fun getID(): String = username
    override fun isPremium(): Boolean {
        _premium?.let {
            return when (it.plan) {
                "subscription", "one-month" -> {
                    if (it.expiration.before(Date())) {
                        userRepository.findByEmail(username)?.let { user ->
                            user.premium?.let { premium ->
                                _premium = premium
                                return premium.expiration.after(Date())
                            }
                        }
                        return false
                    } else {
                        return true
                    }
                }
                else -> false
            }
        } ?: run {
            return false
        }
    }

    override fun getType(): String = "User"
}