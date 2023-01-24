package com.example.data

data class UserLoginRequest(val username: String, val password: String)

data class UserSignUpRequest(val username: String,val password: String)


data class MyToken(val accessToken: String,val refreshToken:String)


data class ErrorMessage(val message:String)

data class SuccessMessage(val message: String)