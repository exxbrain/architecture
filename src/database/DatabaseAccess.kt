package com.exxbrain.database

import com.exxbrain.data.DataAccess
import com.exxbrain.data.Users
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

class DatabaseAccess(url: String, driver: String) : DataAccess {
    init {
        Database.connect(url, driver)
        transaction {
            SchemaUtils.create(UserTable)
        }
    }
    override val users: Users
        get() = DatabaseUsers()
}
