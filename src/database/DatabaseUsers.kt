package com.exxbrain.database

import com.exxbrain.data.User
import com.exxbrain.data.Users
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.lang.AssertionError
import java.util.*

object UserTable : UUIDTable() {
    val username = varchar("name", 256).index(isUnique = true)
    val firstName = varchar("firstName", 256)
    val lastName = varchar("lastName", 256)
    val email = varchar("email", 256).index(isUnique = true)
    val phone = decimal("phone", 12, 0).index(isUnique = true)
}

class DatabaseUsers : Users {

    override fun deleteById(id: UUID) {
        transaction {
            UserTable.deleteWhere { UserTable.id eq id }
        }
    }

    override fun save(user: User) {
        try {
            if (user.id != null) {
                update(user)
            } else {
                insert(user)
            }
        } catch (e: Exception) {
            val msg = e.cause?.message
            throw if (msg != null && (msg.contains("duplicate")
                            || msg.contains("Unique index or primary key violation")))
                AssertionError(msg)
            else e
        }
    }

    private fun insert(user: User) {
        val id = transaction {
            UserTable.insertAndGetId {
                it[username] = user.username
                it[firstName] = user.firstName
                it[lastName] = user.lastName
                it[email] = user.email
                it[phone] = user.phone
            }
        }
        user.id = id.value
    }

    private fun update(user: User) {
        transaction {
            UserTable.update({ UserTable.id eq user.id }) {
                it[username] = user.username
                it[firstName] = user.firstName
                it[lastName] = user.lastName
                it[email] = user.email
                it[phone] = user.phone
            }
        }
    }

    override fun findById(id: UUID): User? {
        return transaction {
            UserTable.select { UserTable.id eq id }.limit(1).map {
                User(
                    id = it[UserTable.id].value,
                    firstName = it[UserTable.firstName],
                    lastName = it[UserTable.lastName],
                    username = it[UserTable.username],
                    email = it[UserTable.email],
                    phone = it[UserTable.phone]
                )
            }
        }.firstOrNull()
    }
}