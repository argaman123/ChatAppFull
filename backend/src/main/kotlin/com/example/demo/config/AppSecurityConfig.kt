package com.example.demo.config

import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.beans.factory.annotation.Autowired
import com.example.demo.service.AppUserDetailsService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import kotlin.Throws
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.messaging.simp.config.ChannelRegistration
import java.lang.Exception

@Configuration
@EnableWebSecurity
class AppSecurityConfig : WebSecurityConfigurerAdapter() {
    @Autowired
    private val userDetailsService: AppUserDetailsService? = null
    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        // Set up simplified security settings requiring all requests to be authenticated
        http.csrf().disable()
            .authorizeRequests()
            .anyRequest()
            .fullyAuthenticated()
    }

    @Throws(Exception::class)
    override fun configure(web: WebSecurity) {
        // Tell Spring to ignore securing the handshake endpoint. This allows the handshake to take place unauthenticated
        web.ignoring().antMatchers("/chat-connection/**")
    }

    @Throws(Exception::class)
    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder())
    }

    // Create an AuthenticationManager bean to Authenticate users in the ChannelInterceptor
    @Bean
    @Throws(Exception::class)
    fun authManager(): AuthenticationManager {
        return authenticationManager()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder(10)
    }
}