package com.ej.zerdabiyolu2.testing

object RegistrationUtil {

    private val existingUsers = listOf("Peter", "Carl")

    fun validateRegistrationInput(
        username: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        if (username.isEmpty() || password.isEmpty())
            return false

        if (username in existingUsers)
            return false

        if (password != confirmPassword)
            return false

        if (password.count { it.isDigit() } < 2)
            return false

        return true
    }
}