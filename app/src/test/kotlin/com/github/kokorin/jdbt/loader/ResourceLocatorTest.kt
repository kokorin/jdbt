package com.github.kokorin.jdbt.loader

import com.github.kokorin.jdbt.exception.JdbtException
import com.github.kokorin.jdbt.mock.mockFs
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.nio.file.Path

class ResourceLocatorTest {


    @Test
    fun findBottomUp() {
        mockFs(
            mapOf(
                "jaffle_shop/dbt_project.yml" to "",
                "jaffle_shop/models/first.sql" to ""
            )
        ).use { fs ->
            val locator = ResourceLocator(fs)

            assertEquals(
                fs.getPath("jaffle_shop/dbt_project.yml"),
                locator.findBottomUp("dbt_project.yml", "jaffle_shop")
            )
            assertEquals(
                fs.getPath("jaffle_shop/dbt_project.yml"),
                locator.findBottomUp("dbt_project.yml", "jaffle_shop/models")
            )
            assertThrows<JdbtException> {
                locator.findBottomUp("dbt_project.yml", "etc")
            }
        }
    }

    @Test
    fun findAt() {
        mockFs(
            mapOf(
                "jaffle_shop/profiles.yml" to "",
                "users/me/.dbt/profiles.yml" to ""
            )
        ).use { fs ->
            val locator = ResourceLocator(fs)

            assertEquals(
                fs.getPath("jaffle_shop/profiles.yml"),
                locator.findAt("profiles.yml", listOf("jaffle_shop"))
            )
            assertEquals(
                fs.getPath("users/me/.dbt/profiles.yml"),
                locator.findAt("profiles.yml", listOf("users/me/.dbt"))
            )
            assertEquals(
                fs.getPath("jaffle_shop/profiles.yml"),
                locator.findAt("profiles.yml", listOf("etc", "jaffle_shop"))
            )
            assertEquals(
                fs.getPath("jaffle_shop/profiles.yml"),
                locator.findAt("profiles.yml", listOf("jaffle_shop", "users/me/.dbt"))
            )
            assertThrows<JdbtException> {
                locator.findAt("dbt_project.yml", listOf("etc"))
            }
        }
    }

    @Test
    fun `findAllInDir ignores absent sub-directories`() {
        mockFs(
            mapOf(
                "not_a_directory" to ""
            )
        ).use { fs ->
            val locator = ResourceLocator(fs)
            assertEquals(
                mapOf(fs.getPath("absent") to setOf<Path>()),
                locator.findAllInDir(dir = fs.getPath("."), subdirs = listOf("absent"), ".sql")
            )
            assertEquals(
                mapOf(fs.getPath("not_a_directory/absent") to setOf<Path>()),
                locator.findAllInDir(dir = fs.getPath("."), subdirs = listOf("not_a_directory/absent"), ".sql")
            )
        }
    }

    @Test
    fun findAllInDir() {
        mockFs(
            mapOf(
                "test/jaffle_shop/dbt_project.yml" to "",
                "test/jaffle_shop/models/first.sql" to "",
                "test/jaffle_shop/models/second.sql" to "",
                "test/jaffle_shop/models/sub/third.sql" to "",
                "test/jaffle_shop/models/sub/pub/fourth.sql" to "",
                "test/jaffle_shop/models/schema.yml" to "",
                "test/jaffle_shop/extra_models/extra.sql" to "",
            )
        ).use { fs ->
            val locator = ResourceLocator(fs)
            assertEquals(
                mapOf(
                    fs.getPath("models") to listOf(
                        "first.sql",
                        "second.sql",
                        "sub/third.sql",
                        "sub/pub/fourth.sql",
                    ).map(fs::getPath).toSet()
                ),
                locator.findAllInDir(fs.getPath("test/jaffle_shop"), listOf("models"), ".sql")
            )
            assertEquals(
                mapOf(
                    fs.getPath("models") to listOf(
                        "first.sql",
                        "second.sql",
                        "sub/third.sql",
                        "sub/pub/fourth.sql",
                    ).map(fs::getPath).toSet(),
                    fs.getPath("extra_models") to listOf(
                        "extra.sql"
                    ).map(fs::getPath).toSet()
                ),
                locator.findAllInDir(fs.getPath("test/jaffle_shop"), listOf("models", "extra_models"), ".sql")
            )
        }
    }
}