package com.exxbrain.data

import java.util.*

interface Users {
    fun save(user: User)
    fun deleteById(id: UUID)
    fun findById(id: UUID): User?
}
