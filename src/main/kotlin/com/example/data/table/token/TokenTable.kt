package com.example.data.table.token

import com.example.data.table.user.UsersTable
import org.jetbrains.exposed.sql.Table


data class Token(val id:Int?,val accessToken:String?,val refreshToken:String)

object TokenTable: Table() {
    val userId =reference("userID",UsersTable.id)
    val accessToken=varchar("accessToken", length = 1024)
    val refreshToken=varchar("refreshToken", length = 1024)

    override val primaryKey=PrimaryKey(userId)

}