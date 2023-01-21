package com.example

import com.example.data.dao.DatabaseFactory
import io.ktor.server.application.*
import com.example.plugins.*

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused")
fun Application.module() {
    DatabaseFactory.init()
    configureAutoHeadResponse()
    configureCORS()
    configureStatusPage()
    configureSecurity()
    configureHTTP()
    configureSerialization()
    configureRouting()
    configureSwagger()
    configureOpenAPI()
}
