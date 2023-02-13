package com.example.data.dao.token

import com.example.data.dao.DatabaseFactory.dbQuery
import com.example.data.table.token.Token
import com.example.data.table.token.TokenTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class TokenDaoFacadeImpl : TokenDaoFacade {
    private fun resultRowToArticle(row: ResultRow) = Token(
        id = row[TokenTable.userId],
        accessToken = row[TokenTable.accessToken],
        refreshToken = row[TokenTable.refreshToken],
    )

    override suspend fun isRefreshTokenAvailable(userID: Int, refreshToken: String):Boolean= dbQuery {
        TokenTable.select { (TokenTable.userId eq userID) and (TokenTable.refreshToken eq refreshToken) }.map(::resultRowToArticle).isNotEmpty()
    }

    override suspend fun getAllToken(): List<Token> = dbQuery {
        TokenTable.selectAll().map(::resultRowToArticle)
    }

    override suspend fun getTokens(userId: Int): Token?= dbQuery {
        TokenTable.select(where =TokenTable.userId eq userId ).map(::resultRowToArticle).firstOrNull()
    }

    override suspend fun addToken(token: Token): Boolean = dbQuery {
        val insertStatement = TokenTable.insert {
            it[userId] = token.id ?: 0
            it[accessToken] = token.accessToken ?: ""
            it[refreshToken] = token.refreshToken
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToArticle) != null
    }

    override suspend fun replaceAccessToken(userId: Int, accessToken: String) = dbQuery {
        val updateStatement = TokenTable.update(where = { TokenTable.userId eq userId }) {
            it[TokenTable.accessToken] = accessToken
        }
        updateStatement > 0
    }


    override suspend fun isTokenAvailable(userId: Int): Boolean = dbQuery {
        TokenTable.select { TokenTable.userId eq userId }.map(::resultRowToArticle).isNotEmpty()
    }

    override suspend fun deleteToken(tokenType: TokenType, userId: Int) = dbQuery {
        when (tokenType) {
            TokenType.accessToken -> {
                val updateStatement = TokenTable.update(where = { TokenTable.userId eq userId }) {
                    it[accessToken] = ""

                }
                updateStatement > 0
            }

            TokenType.refreshToken -> {
                val updateStatement = TokenTable.update(where = { TokenTable.userId eq userId }) {
                    it[refreshToken] = ""

                }
                updateStatement > 0
            }

            TokenType.allToken -> {
                TokenTable.deleteWhere { TokenTable.userId eq userId } > 0
            }
        }
    }
}

val tokenDao:TokenDaoFacade=TokenDaoFacadeImpl()