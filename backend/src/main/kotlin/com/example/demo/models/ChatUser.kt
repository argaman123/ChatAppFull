package com.example.demo.models

interface ChatUser {
    fun getNickname() :String
    fun getEmail() :String?
    fun getID() :String
    fun isPremium() :Boolean
    fun getType() :String
}