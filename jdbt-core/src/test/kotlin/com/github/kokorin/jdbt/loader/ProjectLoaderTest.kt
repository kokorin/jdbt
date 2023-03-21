package com.github.kokorin.jdbt.loader

import com.github.kokorin.jdbt.domain.Locator
import com.github.kokorin.jdbt.domain.model.Resource
import com.github.kokorin.jdbt.domain.props.*
import com.github.kokorin.jdbt.exception.JdbtException
import com.github.kokorin.jdbt.mock.mockFs
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.nio.file.Files

class ProjectLoaderTest {
    @Test
    fun `load throws exception if project not found`() {
        mockFs(mapOf())
            .use { fs ->
                assertThrows<JdbtException> {
                    ProjectLoader(ResourceLocator(fs)).load("test/jaffle_shop")
                }
                assertThrows<JdbtException> {
                    ProjectLoader(ResourceLocator(fs)).load(null)
                }
            }
    }

    @Test
    fun load() {
        mockFs(
            mapOf(
                "test/jaffle_shop/dbt_project.yml" to """
                    name: 'jaffle_shop'
                    config-version: 2
                    version: '0.1'
                    profile: 'jaffle_shop'
                """.trimIndent(),
                "test/jaffle_shop/models/first.sql" to "",
                "test/jaffle_shop/models/second.sql" to "",
                "test/jaffle_shop/models/sub/third.sql" to "",
                "test/jaffle_shop/models/sub/pub/fourth.sql" to "",
                "test/jaffle_shop/models/schema.yml" to """
                    version: 2
                    sources:
                      - name: raw
                        database: any
                        schema: main
                        tables:
                          - name: orders                    
                    models:
                      - name: first
                      - name: second
                """.trimIndent(),
                "test/jaffle_shop/seeds/input1.csv" to "",
                "test/jaffle_shop/seeds/input2.csv" to "",
                "test/jaffle_shop/seeds/schema.yml" to """
                    version: 2
                    seeds:
                      - name: input1
                """.trimIndent(),
            )
        ).use { fs ->
            val project = ProjectLoader(ResourceLocator(fs)).load("test/jaffle_shop")

            assertThat(project).isNotNull

            assertThat(Files.exists(project.rootDir)).isTrue
            assertThat(Files.isDirectory(project.rootDir)).isTrue
            assertThat(project.rootDir.isAbsolute).isTrue

            assertThat(
                project.props
            ).isEqualTo(
                ProjectProps(
                    name = "jaffle_shop",
                    configVersion = 2,
                    version = "0.1",
                    profile = "jaffle_shop"
                )
            )

            assertThat(
                project.models
            ).containsExactlyInAnyOrder(
                Resource(
                    name = "first",
                    namespace = "jaffle_shop",
                    locator = Locator("jaffle_shop", "first"),
                    path = fs.getPath("models/first.sql")
                ),
                Resource(
                    name = "second",
                    namespace = "jaffle_shop",
                    locator = Locator("jaffle_shop", "second"),
                    path = fs.getPath("models/second.sql")
                ),
                Resource(
                    name = "third",
                    namespace = "jaffle_shop",
                    locator = Locator("jaffle_shop", "sub", "third"),
                    path = fs.getPath("models/sub/third.sql")
                ),
                Resource(
                    name = "fourth",
                    namespace = "jaffle_shop",
                    locator = Locator("jaffle_shop", "sub", "pub", "fourth"),
                    path = fs.getPath("models/sub/pub/fourth.sql")
                )
            )

            assertThat(
                project.modelsProps
            ).containsExactlyInAnyOrder(
                ModelsProps(
                    version = 2,
                    models = setOf(
                        ModelProps(name = "first"),
                        ModelProps(name = "second"),
                    ),
                    sources = setOf(
                        SourceProps(
                            name = "raw",
                            database = "any",
                            schema = "main",
                            tables = setOf(
                                TableProps(name = "orders")
                            )
                        )
                    )
                )
            )

            assertThat(
                project.seeds
            ).containsExactlyInAnyOrder(
                Resource(
                    name = "input1",
                    namespace = "jaffle_shop",
                    locator = Locator("jaffle_shop", "input1"),
                    path = fs.getPath("seeds/input1.csv")
                ),
                Resource(
                    name = "input2",
                    namespace = "jaffle_shop",
                    locator = Locator("jaffle_shop", "input2"),
                    path = fs.getPath("seeds/input2.csv")
                )
            )

            assertThat(
                project.seedsProps
            ).containsExactlyInAnyOrder(
                SeedsProps(
                    version = 2,
                    seeds = listOf(
                        SeedProps(name = "input1")
                    )
                )
            )
        }

    }

    @Test
    fun projectResources() {
        mockFs(
            mapOf(
                "test/jaffle_shop/dbt_project.yml" to "",
                "test/jaffle_shop/models/first.sql" to "",
                "test/jaffle_shop/models/second.sql" to "",
                "test/jaffle_shop/models/sub/third.sql" to "",
                "test/jaffle_shop/models/sub/pub/fourth.sql" to "",
                "test/jaffle_shop/models/schema.yml" to "",
                "test/jaffle_shop/seeds/input1.csv" to "",
                "test/jaffle_shop/seeds/input2.csv" to "",
                "test/jaffle_shop/seeds/schema.yml" to "",
            )
        ).use { fs ->
            val projectLoader = ProjectLoader(ResourceLocator(fs))

            assertThat(
                projectLoader.projectResources(
                    "jaffle_shop",
                    fs.getPath("test/jaffle_shop"),
                    listOf("models"),
                    ".sql"
                )
            ).containsExactlyInAnyOrder(
                Resource(
                    name = "first",
                    namespace = "jaffle_shop",
                    locator = Locator("jaffle_shop", "first"),
                    path = fs.getPath("models/first.sql")
                ),
                Resource(
                    name = "second",
                    namespace = "jaffle_shop",
                    locator = Locator("jaffle_shop", "second"),
                    path = fs.getPath("models/second.sql")
                ),
                Resource(
                    name = "third",
                    namespace = "jaffle_shop",
                    locator = Locator("jaffle_shop", "sub", "third"),
                    path = fs.getPath("models/sub/third.sql")
                ),
                Resource(
                    name = "fourth",
                    namespace = "jaffle_shop",
                    locator = Locator("jaffle_shop", "sub", "pub", "fourth"),
                    path = fs.getPath("models/sub/pub/fourth.sql")
                ),
            )
        }

    }
}