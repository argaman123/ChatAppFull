package com.example.demo.configs

import com.example.demo.auth.GuestAuthenticationProvider
import com.example.demo.auth.JwtAuthFilter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class WebSecurityConfig @Autowired constructor(
    private val guestAuthenticationProvider: GuestAuthenticationProvider,
    private val jwtAuthFilter: JwtAuthFilter
) : WebSecurityConfigurerAdapter() {

    companion object {
        val unauthorizedURLS = arrayOf("/auth/**", "/account/renew/**")
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
        auth.authenticationProvider(guestAuthenticationProvider)
    }

    @Bean
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOriginPatterns = listOf("*")
        configuration.allowedMethods = listOf("*")
        configuration.allowCredentials = true
        configuration.allowedHeaders = listOf("*")
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

}