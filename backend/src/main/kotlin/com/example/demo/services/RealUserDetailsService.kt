package com.example.demo.services

import com.example.demo.models.RealUser
import com.example.demo.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class RealUserDetailsService @Autowired constructor(
    private val userRepository: UserRepository
) : UserDetailsService {
    override fun loadUserByUsername(email: String): UserDetails {
        userRepository.findByEmail(email)?.let {
            return RealUser(it, userRepository)
        } ?: run {
            throw UsernameNotFoundException("Email not found: $email")
        }
    }

    fun loadUserByNickname(nickname :String) :UserDetails {
        userRepository.findByNickname(nickname)?.let {
            return RealUser(it, userRepository)
        } ?: run {
            throw UsernameNotFoundException("Nickname not found: $nickname")
        }
    }
}