package com.github.kokorin.jdbt.domain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class LocatorTest {

    @Test
    fun getParent() {
        assertEquals(
            Locator(listOf("parent")),
            Locator(listOf("parent", "child")).parent
        )
        assertEquals(null, Locator(listOf()).parent)
    }
}