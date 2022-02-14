package com.example.demo.models

interface ChatUser {
    fun getNickname() :String
    fun getEmail() :String?
    fun isPremium() :Boolean
    fun getType() :String
}