package com.example.demo.entities

import org.hibernate.annotations.Cascade
import org.hibernate.annotations.CascadeType
import javax.persistence.*

@Entity
@Table(name="users")
data class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id :Long? = null,
    @Column(unique = true, length = 20) var nickname: String = "",
    @Column(unique = true, length = 30) val email: String = "",
    var password: String = "",
    val roles: String,

    @OneToOne(optional = true)
    @JoinColumn(name = "premium_id", referencedColumnName = "id")
    @Cascade(CascadeType.ALL)
    var premium: Premium? = null
)