package com.exxbrain.data

import java.util.*

data class User(
    var id: UUID? = null,
    val username: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String
)