package com.example.data.table.blog

import com.example.data.table.user.UsersTable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

data class Blogs(val blogs:List<Blog>)
data class Blog(val id: Int?, val title: String,val createdAt:String ,val updatedAt: String?, val userID: Int)

object BlogTable: Table() {
    val id=integer("id").autoIncrement().uniqueIndex()
    val blogTitle=varchar("title",1028)
    val createdAt=datetime("createAt").default(LocalDateTime.now())
    val updatedAt=datetime("updatedAt").nullable()
    val userId=reference("userID",UsersTable.id)

    override val primaryKey=PrimaryKey(id)
}