package com.github.kokorin.jdbt.compiler

import com.github.kokorin.jdbt.domain.Locator
import com.github.kokorin.jdbt.domain.model.Model
import com.github.kokorin.jdbt.domain.model.Resource
import com.github.kokorin.jdbt.domain.props.Config
import com.github.kokorin.jdbt.domain.props.ProfileTarget
import com.github.kokorin.jdbt.loader.ProjectLoader
import com.github.kokorin.jdbt.loader.ResourceLocator
import com.github.kokorin.jdbt.mock.mockFs
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import java.nio.file.Files

class CompilerTest {

    @Test
    fun `compile simple model`() {
        mockFs(
            mapOf(
                "dbt_project.yml" to """
                    name: 'jaffle_shop'
                    config-version: 2
                    version: '0.1'
                    profile: 'jaffle_shop'
                """.trimIndent(),
                "models/simple.sql" to "select 1 as id, 'John Doe' as name",
            )
        ).use { fs ->
            val loader = ProjectLoader(ResourceLocator((fs)))
            val project = loader.load(null)
            val target = ProfileTarget(
                type = "sqlite",
                database = "main",
                schema = "main",
                threads = 1
            )
            val modelResource = project.models[0]

            assertNotNull(project)

            val compiledPath = fs.getPath("./target/jaffle_shop/compiled/models/simple.sql")

            assertThat(
                Compiler(project, target).compileModel(
                    modelResource,
                    mapOf("simple" to Config("database" to "main", "schema" to "main"))
                )
            ).isEqualTo(
                Model(
                    modelResource,
                    Config("database" to "main", "schema" to "main"),
                    setOf(),
                    compiledPath.toAbsolutePath()
                )
            )

            assertThat(
                Files.readAllLines(compiledPath).joinToString("\n")
            ).isEqualTo(
                "select 1 as id, 'John Doe' as name"
            )
        }
    }

    @Test
    fun `compile model with ref`() {
        mockFs(
            mapOf(
                "dbt_project.yml" to """
                    name: 'jaffle_shop'
                    config-version: 2
                    version: '0.1'
                    profile: 'jaffle_shop'
                """.trimIndent(),
                "models/simple.sql" to "select 1 as id, 'John Doe' as name",
                "models/complex.sql" to "select id, name from {{ ref('simple') }}",
            )
        ).use { fs ->
            val loader = ProjectLoader(ResourceLocator((fs)))
            val project = loader.load(null)
            val target = ProfileTarget(
                type = "sqlite",
                database = "main",
                schema = "main",
                threads = 1
            )
            val modelResources = project.models.associateBy { it.name }
            val simpleResource = modelResources["simple"]!!
            val complexResource = modelResources["complex"]!!

            assertNotNull(project)
            val actual = Compiler(project, target).compileModel(
                complexResource,
                mapOf(
                    "simple" to Config("database" to "main", "schema" to "main"),
                    "complex" to Config("database" to "main", "schema" to "main")
                )
            )

            val compiledPath = fs.getPath("./target/jaffle_shop/compiled/models/complex.sql")
            val expected = Model(
                complexResource,
                Config("database" to "main", "schema" to "main"),
                setOf(simpleResource),
                compiledPath.toAbsolutePath()
            )
            assertThat(actual).isEqualTo(expected)

            assertThat(
                Files.readAllLines(compiledPath).joinToString("\n")
            ).isEqualTo(
                "select id, name from main.main.simple"
            )
        }
    }

    @Test
    fun `compile simple project`() {
        mockFs(
            mapOf(
                "dbt_project.yml" to """
                    name: 'jaffle_shop'
                    config-version: 2
                    version: '0.1'
                    profile: 'jaffle_shop'
                """.trimIndent(),
                "models/simple.sql" to "select 1 as id, 'John Doe' as name",
            )
        ).use { fs ->
            val loader = ProjectLoader(ResourceLocator((fs)))
            val project = loader.load(null)
            val target = ProfileTarget(
                type = "sqlite",
                database = "main",
                schema = "main",
                threads = 1
            )

            assertNotNull(project)
            val compiled = Compiler(project, target).compile()

            val simplePath = fs.getPath("./target/jaffle_shop/compiled/models/simple.sql")

            assertThat(compiled.project).isEqualTo(project)
            assertThat(
                setOf(
                    Model(
                        Resource(
                            name = "simple",
                            namespace = "jaffle_shop",
                            locator = Locator("jaffle_shop", "simple"),
                            path = fs.getPath("models/simple.sql")
                        ),
                        Config("database" to "main", "schema" to "main"),
                        setOf(),
                        simplePath.toAbsolutePath()
                    )
                )
            ).isEqualTo(
                compiled.models.toSet()
            )

            assertThat(
                Files.readAllLines(simplePath).joinToString("\n")
            ).isEqualTo(
                "select 1 as id, 'John Doe' as name"
            )
        }
    }

    @Test
    fun `compile project with ref`() {
        mockFs(
            mapOf(
                "dbt_project.yml" to """
                    name: 'jaffle_shop'
                    config-version: 2
                    version: '0.1'
                    profile: 'jaffle_shop'
                    models:
                      jaffle_shop:
                        model_with_conf:
                          +database: project_file_db
                        sub:                        
                          +schema: sub_schema
                """.trimIndent(),
                "models/simple.sql" to "select 1 as id, 'John Doe' as name",
                "models/complex.sql" to "select id, name from {{ ref('simple') }}",
            )
        ).use { fs ->
            val loader = ProjectLoader(ResourceLocator((fs)))
            val project = loader.load(null)
            val target = ProfileTarget(
                type = "sqlite",
                database = "main",
                schema = "main",
                threads = 1
            )

            assertNotNull(project)
            val compiled = Compiler(project, target).compile()

            val simplePath = fs.getPath("./target/jaffle_shop/compiled/models/simple.sql")
            val complexPath = fs.getPath("./target/jaffle_shop/compiled/models/complex.sql")

            assertThat(compiled.project).isEqualTo(project)
            assertThat(
                compiled.models
            ).containsExactlyInAnyOrder(
                Model(
                    Resource(
                        name = "simple",
                        namespace = "jaffle_shop",
                        locator = Locator("jaffle_shop", "simple"),
                        path = fs.getPath("models/simple.sql")
                    ),
                    Config("database" to "main", "schema" to "main"),
                    setOf(),
                    simplePath.toAbsolutePath()
                ),
                Model(
                    Resource(
                        name = "complex",
                        namespace = "jaffle_shop",
                        locator = Locator("jaffle_shop", "complex"),
                        path = fs.getPath("models/complex.sql")
                    ),
                    Config("database" to "main", "schema" to "main"),
                    setOf(
                        Resource(
                            name = "simple",
                            namespace = "jaffle_shop",
                            locator = Locator("jaffle_shop", "simple"),
                            path = fs.getPath("models/simple.sql")
                        )
                    ),
                    complexPath.toAbsolutePath()
                )
            )

            assertThat(
                Files.readAllLines(simplePath).joinToString("\n")
            ).isEqualTo(
                "select 1 as id, 'John Doe' as name"
            )
            assertThat(
                Files.readAllLines(complexPath).joinToString("\n")
            ).isEqualTo(
                "select id, name from main.main.simple"
            )
        }
    }

    @Test
    fun `compile project with source and ref`() {
        mockFs(
            mapOf(
                "dbt_project.yml" to """
                    name: 'jaffle_shop'
                    config-version: 2
                    version: '0.1'
                    profile: 'jaffle_shop'
                """.trimIndent(),
                "models/simple.sql" to "select id, name from {{ source('raw', 'users') }}",
                "models/complex.sql" to "select id, name from {{ ref('simple') }}",
                "models/sources.yml" to """
                    version: 2
                    sources:
                      - name: raw
                        database: raw_db
                        schema: raw_schema
                        tables:
                          - name: users
                """.trimIndent(),
            )
        ).use { fs ->
            val loader = ProjectLoader(ResourceLocator((fs)))
            val project = loader.load(null)
            val target = ProfileTarget(
                type = "sqlite",
                database = "main",
                schema = "main",
                threads = 1
            )

            assertNotNull(project)
            val compiled = Compiler(project, target).compile()

            val simplePath = fs.getPath("./target/jaffle_shop/compiled/models/simple.sql")
            val complexPath = fs.getPath("./target/jaffle_shop/compiled/models/complex.sql")

            assertThat(compiled.project).isEqualTo(project)
            assertThat(
                compiled.models
            ).containsExactlyInAnyOrder(
                Model(
                    Resource(
                        name = "simple",
                        namespace = "jaffle_shop",
                        locator = Locator("jaffle_shop", "simple"),
                        path = fs.getPath("models/simple.sql")
                    ),
                    Config("database" to "main", "schema" to "main"),
                    setOf(),
                    simplePath.toAbsolutePath()
                ),
                Model(
                    Resource(
                        name = "complex",
                        namespace = "jaffle_shop",
                        locator = Locator("jaffle_shop", "complex"),
                        path = fs.getPath("models/complex.sql")
                    ),
                    Config("database" to "main", "schema" to "main"),
                    setOf(
                        Resource(
                            name = "simple",
                            namespace = "jaffle_shop",
                            locator = Locator("jaffle_shop", "simple"),
                            path = fs.getPath("models/simple.sql")
                        )
                    ),
                    complexPath.toAbsolutePath()
                )
            )

            assertThat(
                Files.readAllLines(simplePath).joinToString("\n")
            ).isEqualTo(
                "select id, name from raw_db.raw_schema.users"
            )
            assertThat(
                Files.readAllLines(complexPath).joinToString("\n")
            ).isEqualTo(
                "select id, name from main.main.simple"
            )
        }
    }

    @Test
    fun `compile project with config`() {
        mockFs(
            mapOf(
                "dbt_project.yml" to """
                    name: 'jaffle_shop'
                    config-version: 2
                    version: '0.1'
                    profile: 'jaffle_shop'
                    models:
                      jaffle_shop:
                        model_with_conf:
                          +database: project_file_db
                        sub:                        
                          +schema: sub_schema
                """.trimIndent(),
                "models/model_without_conf.sql" to "select 1",
                "models/model_with_conf.sql" to "select 1",
                "models/sub/model_in_subdir.sql" to "select 1",
                "models/model_inplace_conf.sql" to """
                    {{ config(database = 'inplace_db', schema = 'inplace_schema') }}
                    select 1
                """.trimIndent(),
                "models/models.yml" to """
                    version: 2
                    models:
                      - name: model_with_conf
                        config:
                          schema: yaml_file_schema
                """.trimIndent(),
            )
        ).use { fs ->
            val loader = ProjectLoader(ResourceLocator((fs)))
            val project = loader.load(null)
            val target = ProfileTarget(
                type = "sqlite",
                database = "main",
                schema = "main",
                threads = 1
            )

            assertNotNull(project)
            val compiled = Compiler(project, target).compile()

            assertThat(compiled.project).isEqualTo(project)

            assertThat(
                compiled.models
            ).containsExactlyInAnyOrder(
                Model(
                    Resource(
                        name = "model_without_conf",
                        namespace = "jaffle_shop",
                        locator = Locator("jaffle_shop", "model_without_conf"),
                        path = fs.getPath("models/model_without_conf.sql")
                    ),
                    Config("database" to "main", "schema" to "main"),
                    setOf(),
                    fs.getPath("./target/jaffle_shop/compiled/models/model_without_conf.sql").toAbsolutePath()
                ),
                Model(
                    Resource(
                        name = "model_with_conf",
                        namespace = "jaffle_shop",
                        locator = Locator("jaffle_shop", "model_with_conf"),
                        path = fs.getPath("models/model_with_conf.sql")
                    ),
                    Config("database" to "project_file_db", "schema" to "yaml_file_schema"),
                    setOf(),
                    fs.getPath("./target/jaffle_shop/compiled/models/model_with_conf.sql").toAbsolutePath()
                ),
                Model(
                    Resource(
                        name = "model_in_subdir",
                        namespace = "jaffle_shop",
                        locator = Locator("jaffle_shop", "sub", "model_in_subdir"),
                        path = fs.getPath("models/sub/model_in_subdir.sql")
                    ),
                    Config("database" to "main", "schema" to "sub_schema"),
                    setOf(),
                    fs.getPath("./target/jaffle_shop/compiled/models/sub/model_in_subdir.sql").toAbsolutePath()
                ),
                Model(
                    Resource(
                        name = "model_inplace_conf",
                        namespace = "jaffle_shop",
                        locator = Locator("jaffle_shop", "model_inplace_conf"),
                        path = fs.getPath("models/model_inplace_conf.sql")
                    ),
                    Config("database" to "inplace_db", "schema" to "inplace_schema"),
                    setOf(),
                    fs.getPath("./target/jaffle_shop/compiled/models/model_inplace_conf.sql").toAbsolutePath()
                )
            )
        }
    }


}