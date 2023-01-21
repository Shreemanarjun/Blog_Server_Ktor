package com.example.data.table

import org.jetbrains.exposed.sql.Table


data class User(val id: Int?, val username: String, val password: String)


object UsersTable : Table() {
    val id = integer("id").autoIncrement()
    val username = varchar("username", 128)
    val password = varchar("password", 1024)

    override val primaryKey = PrimaryKey(id)
}