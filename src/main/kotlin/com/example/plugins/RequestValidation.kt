package com.example.plugins

import com.example.data.UserRequest
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*

fun Application.configureRequestValidation(){
    install(RequestValidation) {
        validate<UserRequest> { userrequest ->
            when {
                userrequest.username .isNullOrBlank() -> ValidationResult.Invalid("Username cannot be null")
                userrequest.password.isNullOrBlank() -> ValidationResult.Invalid("password cannot be null")
                else -> ValidationResult.Valid
            }
        }
    }
}