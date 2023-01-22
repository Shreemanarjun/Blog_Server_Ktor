package com.example.data.dao.user

import com.example.data.UserRequest
import com.example.data.dao.DatabaseFactory.dbQuery
import com.example.data.table.user.User
import com.example.data.table.user.UsersTable
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class UserDAOFacadeImpl : UserDaoFacade {

    private fun resultRowToArticle(row: ResultRow) = User(
        id = row[UsersTable.id],
        username = row[UsersTable.username],
        password = row[UsersTable.password],
    )
    override suspend fun getAllUser(): List<User> =dbQuery {
        UsersTable.selectAll().map(::resultRowToArticle)
    }
    override suspend fun isUserAvailable(user:UserRequest ):Boolean=dbQuery {
      UsersTable
          .select { (UsersTable.username eq user.username) and (UsersTable.password eq user.password)}
          .map(::resultRowToArticle)
          .isNotEmpty()
  }
    override suspend fun isUserAvailable(id: Int): User? = dbQuery {
        UsersTable
            .select { UsersTable.id eq id }
            .map(::resultRowToArticle)
            .singleOrNull()
    }

    override suspend fun addNewUser(username: String, password: String): User? = dbQuery {
        val insertStatement = UsersTable.insert {
            it[UsersTable.username] = username
            it[UsersTable.password] = password
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToArticle)
    }

    override suspend fun deleteUser(id: Int): Boolean =dbQuery {
        UsersTable.deleteWhere { UsersTable.id eq id } > 0
    }
}
val userDao: UserDaoFacade = UserDAOFacadeImpl().apply {
    runBlocking {
        if(getAllUser().isEmpty()) {
            addNewUser(username = "Arjun", password = "password")

        }
    }
}