package com.example.plugins

import com.example.data.ErrorMessage
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun Application.configureStatusPage(){
    install(StatusPages) {

        status(HttpStatusCode.Unauthorized){ call, status ->
            call.respond(status, ErrorMessage(message ="Unauthorized Access" ))
        }
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause" , status = HttpStatusCode.InternalServerError)
        }
    }
}