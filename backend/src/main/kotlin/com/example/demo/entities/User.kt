package com.example.demo.entities

import com.example.demo.dtos.UserDto
import org.springframework.security.crypto.password.PasswordEncoder
import javax.persistence.*

@Entity
class User() {
    @Id @GeneratedValue(strategy = GenerationType.AUTO) var id: Long? = null
    @Column(unique = true, length = 20) var username: String? = null
    @Column(unique = true, length = 30) val email: String = ""
    var password: String? = null
    var authority: String? = null

    constructor(dto: UserDto, encoder: PasswordEncoder) : this() {
        username = dto.username
        password = encoder.encode(dto.password)
        authority = "ROLE_USER"
    }
}