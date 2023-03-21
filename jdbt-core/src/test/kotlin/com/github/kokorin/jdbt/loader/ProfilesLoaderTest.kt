package com.github.kokorin.jdbt.loader

import com.github.kokorin.jdbt.domain.props.Profile
import com.github.kokorin.jdbt.domain.props.ProfileTarget
import com.github.kokorin.jdbt.domain.props.Profiles
import com.github.kokorin.jdbt.exception.JdbtException
import com.github.kokorin.jdbt.mock.mockFs
import com.github.kokorin.jdbt.mock.mockOs
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ProfilesLoaderTest {

    @Test
    fun loadProfiles() {
        mockFs(
            mapOf(
                "users/home/user/.dbt/profiles.yml" to """
                    test:
                      target: default
                      outputs:
                        default:
                          type: sqlite
                          database: lite_db
                          schema: lite_schema
                          threads: 1

                """.trimIndent()
            )
        ).use { fs ->
            val os = mockOs("users/home/user")
            val loader = ProfilesLoader(os, fs)
            assertThat(
                loader.loadProfiles(null)
            ).isEqualTo(
                Profiles(
                    mapOf(
                        "test" to Profile(
                            target = "default",
                            outputs = mapOf(
                                "default" to ProfileTarget(
                                    type = "sqlite",
                                    database = "lite_db",
                                    schema = "lite_schema",
                                    threads = 1
                                )
                            )
                        )
                    )
                )
            )
        }
    }

    @Test
    fun findProfiles() {
        mockFs(
            mapOf(
                "users/home/user/.dbt/profiles.yml" to "",
                "var/path/profiles.yml" to ""
            )
        ).use { fs ->
            val os = mockOs("users/home/user")
            val loader = ProfilesLoader(os, fs)
            assertThat(
                loader.findProfiles(null)
            ).isEqualTo(
                fs.getPath("users/home/user/.dbt/profiles.yml")
                )
            assertThat(
                loader.findProfiles("etc")
            ).isEqualTo(
                fs.getPath("users/home/user/.dbt/profiles.yml")
            )
            assertThat(
                loader.findProfiles("var/path")
            ).isEqualTo(
                fs.getPath("var/path/profiles.yml")
            )
        }

        mockFs(
            mapOf(
                "users/home/user/.dbt/profiles.yml" to "",
                "custom/path/profiles.yml" to "",
            )
        ).use { fs ->
            val os = mockOs("users/home/user", mapOf("DBT_PROFILES_DIR" to "custom/path"))
            val loader = ProfilesLoader(os, fs)
            assertThat(
                loader.findProfiles(null)
            ).isEqualTo(
                fs.getPath("custom/path/profiles.yml")
            )
        }

        mockFs(mapOf()).use { fs ->
            val os = mockOs("users/home/user", mapOf("DBT_PROFILES_DIR" to "custom/path"))
            val loader = ProfilesLoader(os, fs)
            assertThrows<JdbtException> {
                loader.findProfiles(null)
            }
        }
    }
}