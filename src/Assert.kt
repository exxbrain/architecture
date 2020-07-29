package com.exxbrain

object Assert {
    fun isPhone(str: String?) {
        isNotBlank(str)
        if (!str!!.startsWith("+")) {
            throw AssertionError("$str doesn't start from +")
        }
        if (!Regex("\\+\\d{10,12}").matches(str)) {
            throw AssertionError("$str is not a phone")
        }
    }

    fun isEmail(str: String?) {
        if(!Regex(".*@.*").matches(str!!)) {
            throw AssertionError("$str is not an email")
        }
    }

    fun isNotBlank(str: String?) {
        if(str.isNullOrBlank()) {
            throw AssertionError("$str is blank")
        }
    }

    fun lengthIsLessThan(str: String, maxLength: Int) {
        if (str.length > maxLength) {
            throw AssertionError("$str length is more than $maxLength")
        }
    }
}