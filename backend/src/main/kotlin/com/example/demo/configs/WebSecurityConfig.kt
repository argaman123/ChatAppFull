package com.example.demo.configs

import com.example.demo.auth.GuestAuthenticationProvider
import com.example.demo.auth.JwtAuthFilter
import com.example.demo.services.ActiveUsersManager
import com.example.demo.services.RealUserDetailsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource



@Configuration
@EnableWebSecurity
class WebSecurityConfig @Autowired constructor(
    private val realUserDetailsService: RealUserDetailsService,
    private val jwtAuthFilter: JwtAuthFilter,
    private val activeUsersManager: ActiveUsersManager
) : WebSecurityConfigurerAdapter() {

    companion object {
        val unauthorizedURLS = arrayOf("/auth/**", "/account/renew/**")
        val frontendURL = "http://localhost:4200"
        val allowedOriginsCors = listOf(frontendURL)
    }

    override fun configure(http: HttpSecurity) {
        http.cors()
            .and().csrf().disable()
            .authorizeRequests()
            .antMatchers(*unauthorizedURLS).permitAll()
            .anyRequest().authenticated()
            .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
                // TODO: AUTH DONE IS A TEMPORARY SOLUTION AND SHOULD BE CHANGED LATER
            .logout().logoutUrl("/account/logout").logoutSuccessUrl("/auth/done").deleteCookies("jwt").invalidateHttpSession(true)
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter::class.java)
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.authenticationProvider(GuestAuthenticationProvider(realUserDetailsService, activeUsersManager))
            .authenticationProvider(authProvider())
    }

    // A basic real user authentication provider
    @Bean
    fun authProvider(): DaoAuthenticationProvider? {
        val authProvider = DaoAuthenticationProvider()
        authProvider.setUserDetailsService(realUserDetailsService)
        authProvider.setPasswordEncoder(encoder())
        return authProvider
    }

    @Bean
    fun encoder(): PasswordEncoder {
        return BCryptPasswordEncoder(10)
    }

    @Bean
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = allowedOriginsCors
        configuration.allowedMethods = listOf("*")
        configuration.allowCredentials = true
        configuration.allowedHeaders = listOf("*")
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

}