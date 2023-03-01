package com.github.kokorin.jdbt.compiler

import com.github.kokorin.jdbt.domain.Locator
import com.github.kokorin.jdbt.domain.props.Config
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ConfigResolverTest {
    @Test
    fun resolve() {
        assertEquals(
            Config(
                "root_level" to "root",
                "parent_level" to "parent",
                "child_level" to "child",
                "both_levels" to "child",
            ),
            ConfigResolver(
                 mapOf(
                    Locator("parent", "child") to
                            Config(
                                "child_level" to "child",
                                "both_levels" to "child",
                            ),
                    Locator("parent") to
                            Config(
                                "parent_level" to "parent",
                                "both_levels" to "parent",
                            ),
                    Locator.root to Config(
                        "root_level" to "root"
                    )
                )
            ).resolve(Locator("parent", "child"))
        )
    }
}