package com.example.data

data class UserRequest(val username: String, val password: String)


data class MyToken(val token: String)


data class ErrorMessage(val message:String)