package com.example.plugins

import com.example.data.ErrorMessage
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun Application.configureStatusPage(){
    install(StatusPages) {

        status(HttpStatusCode.Unauthorized){ call, status ->
            call.respond(status, ErrorMessage(message ="Unauthorized Access" ))
        }
        exception<RequestValidationException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest,ErrorMessage(message =cause.reasons.joinToString() ) )
        }
        exception<Throwable> { call, cause ->
            call.respond(status = HttpStatusCode.InternalServerError,ErrorMessage(message ="500: ${cause.localizedMessage} $cause" ) )
        }
    }
}