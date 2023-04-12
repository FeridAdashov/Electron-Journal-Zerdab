package com.ej.zerdabiyolu2.testing

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class RegistrationUtilTest {

    @Test
    fun `empty username returns false`() {
        val result = RegistrationUtil.validateRegistrationInput("", "123", "123")
        assertThat(result).isFalse()
    }

    @Test
    fun `valid username and correctly repeated password return true`() {
        val result = RegistrationUtil.validateRegistrationInput("Philipp", "123", "123")
        assertThat(result).isTrue()

    }

    @Test
    fun `username already exists returns false`() {
        val result = RegistrationUtil.validateRegistrationInput("Carl", "123", "123")
        assertThat(result).isFalse()
    }

    @Test
    fun `empty password returns false`() {
        val result = RegistrationUtil.validateRegistrationInput("Philipp", "", "")
        assertThat(result).isFalse()
    }

    @Test
    fun `password less than 2 digits returns false`() {
        val result = RegistrationUtil.validateRegistrationInput("Philipp", "4fsffdsfd", "4fsffdsfd")
        assertThat(result).isFalse()
    }
}