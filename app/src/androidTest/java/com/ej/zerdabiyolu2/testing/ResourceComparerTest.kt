package com.ej.zerdabiyolu2.testing

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.ej.zerdabiyolu2.R
import com.google.common.truth.Truth.assertThat
import org.junit.Test


class ResourceComparerTest {
    private lateinit var resourceComparer : ResourceComparer

    @Test
    fun stringResourceSameAsGivenString_returnsTrue() {
        resourceComparer = ResourceComparer()

        val context = ApplicationProvider.getApplicationContext<Context>()
        val result = resourceComparer.isEqual(context, R.string.app_name, "EJ-Zərdabi Yolu")

        assertThat(result).isTrue()
    }

    @Test
    fun stringResourceDifferentAsGivenString_returnsFalse() {
        resourceComparer = ResourceComparer()

        val context = ApplicationProvider.getApplicationContext<Context>()
        val result = resourceComparer.isEqual(context, R.string.app_name, "UnitTesting")

        assertThat(result).isFalse()
    }
}