package com.example.plugins

import com.example.data.UserLoginRequest
import com.example.data.UserSignUpRequest
import com.example.data.dao.user.userDao
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*

fun Application.configureRequestValidation(){
    install(RequestValidation) {
        validate<UserLoginRequest> { userrequest ->
            when {
                userrequest.username.isBlank() -> ValidationResult.Invalid("Username cannot be null")
                userrequest.password.isBlank() -> ValidationResult.Invalid("password cannot be null")
                else -> ValidationResult.Valid
            }
        }
        validate<UserSignUpRequest> { userrequest ->
            when {
                userrequest.username .isBlank() -> ValidationResult.Invalid("Username cannot be null")
                userrequest.password.isBlank() -> ValidationResult.Invalid("password cannot be null")
                else -> when {
                    userDao.getUser(username = userrequest.username)!=null -> {
                        ValidationResult.Invalid("Unable to signup due to username already reserved")
                    }
                    else -> {
                        ValidationResult.Valid
                    }
                }
            }
        }
    }
}