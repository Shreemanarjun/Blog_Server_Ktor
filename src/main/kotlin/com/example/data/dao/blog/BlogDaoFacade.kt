package com.example.data.dao.blog

import com.example.data.table.blog.Blogs

interface BlogDaoFacade {
    suspend fun getBlogsByUser(userID:Int):Blogs
    suspend fun createBlog(userID: Int,title: String):Boolean
    suspend fun updateBlog(userID:Int,blogId:Int,title:String):Boolean
    suspend fun deleteBlog(userID: Int,blogId:Int):Boolean
}