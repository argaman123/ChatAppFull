package com.example.demo.service

import com.example.demo.dtos.AppUserDetails
import com.example.demo.entities.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class AppUserDetailsService @Autowired constructor(private val userService: UserService) : UserDetailsService {
    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(s: String): UserDetails {
        val user: User = userService.findUserByUsername(s) ?: throw UsernameNotFoundException("User does not exist")
        return AppUserDetails(user)
    }
}