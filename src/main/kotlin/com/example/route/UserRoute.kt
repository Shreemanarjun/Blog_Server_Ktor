package com.example.route

import com.example.data.dao.userDao
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.userRoute(){
    get("/name") {
        call.respondText("Hello World! Arjuns")
    }
    get("/allUser") {
        call.respond(mapOf("users" to userDao.getAllUser()))
    }
}