package com.github.kokorin.jdbt.domain.props

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class ConfigTest {

    @Test
    fun `string and int`() {
        val config = Config(mapOf(
            "key" to "value",
            "key2" to "value2",
            "key3" to 3,
            "key4" to 4.5,
            "key5" to "5",
            "else" to object {}
        ))

        assertEquals("value", config.string("key"))
        assertEquals("value2", config.string("key2"))
        assertEquals("3", config.string("key3"))
        assertEquals("4.5", config.string("key4"))
        assertNotNull(config.string("else"))

        assertEquals(3, config.int("key3"))
        assertEquals(5, config.int("key5"))
    }

    @Test
    fun merge() {
        assertEquals(
            Config(
                "this" to "this",
                "that" to "that",
                "both" to "this"
            ),
            Config(
                "this" to "this",
                "both" to "this"
            ).merge(
                Config(
                    "that" to "that",
                    "both" to "that"
                )
            )
        )
    }


}