package com.example.data.dao.blog

import com.example.data.dao.DatabaseFactory.dbQuery
import com.example.data.table.blog.Blog
import com.example.data.table.blog.BlogTable
import com.example.data.table.blog.Blogs
import io.ktor.server.http.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.time.LocalDateTime

class BlogDaoImpl : BlogDaoFacade {
    private fun resultRowToBlog(row: ResultRow) = Blog(
        id = row[BlogTable.id],
        userID = row[BlogTable.userId],
        title = row[BlogTable.blogTitle],
        createdAt = row[BlogTable.createdAt].toHttpDateString(),
        updatedAt = row[BlogTable.updatedAt]?.toHttpDateString()
    )

    override suspend fun getBlogsByUser(userID: Int): Blogs = dbQuery {
        Blogs(blogs = BlogTable.select(where = BlogTable.userId eq userID).map(::resultRowToBlog))
    }

    override suspend fun createBlog(userID: Int, title: String): Boolean = dbQuery {
        val insertStatement = BlogTable.insert {
            it[userId] = userID
            it[blogTitle] = title
            it[createdAt]=LocalDateTime.now()
            it[updatedAt]=null
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToBlog) != null

    }

    override suspend fun updateBlog(userID: Int, blogId: Int, title: String): Boolean = dbQuery {
        val updateStatement = BlogTable.update(where = { (BlogTable.userId eq userID) and (BlogTable.id eq blogId) }) {
            it[blogTitle] = title
            it[updatedAt] = LocalDateTime.now()

        }
        updateStatement > 0
    }

    override suspend fun deleteBlog(userID: Int, blogId: Int): Boolean = dbQuery {
        val deleteStatement = BlogTable.deleteWhere { (id eq blogId) and (userId eq userID) }
        deleteStatement > 0
    }
}

val blogDao: BlogDaoFacade = BlogDaoImpl()