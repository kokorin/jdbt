package com.github.kokorin.jdbt.parser

import com.github.kokorin.jdbt.domain.Locator
import com.github.kokorin.jdbt.domain.props.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ProjectParserTest {

    @Test
    fun `parseProjectConf with default properties explicitly set`() {
        assertThat(
            ProjectParser.parseProjectProps(
                """
                    name: 'jaffle_shop'
        
                    config-version: 2
                    version: '0.1'
        
                    profile: 'jaffle_shop'
        
                    model-paths: [ "models" ]
                    seed-paths: [ "seeds" ]
                    test-paths: [ "tests" ]
                    analysis-paths: [ "analysis" ]
                    macro-paths: [ "macros" ]
        
                    target-path: "target"
                    clean-targets:
                      - "target"
                      - "dbt_modules"
                      - "logs"
        
                    require-dbt-version: [ ]
        
                    models: [ ]
                    seeds: [ ]
                """.trimIndent().byteInputStream()
            )
        ).isEqualTo(
            ProjectProps(
                name = "jaffle_shop",
                configVersion = 2,
                version = "0.1",
                profile = "jaffle_shop",
                modelPaths = listOf("models"),
                seedPaths = listOf("seeds"),
                testPaths = listOf("tests"),
                analysisPaths = listOf("analysis"),
                macroPaths = listOf("macros"),
                targetPath = "target",
                cleanTargets = listOf("target", "dbt_modules", "logs"),
                requireDbtVersion = listOf(),
                models = mapOf(),
                seeds = mapOf(),
            )
        )
    }


    @Test
    fun `parseProjectConf with all properties set to non-default values`() {
        assertThat(
            ProjectParser.parseProjectProps(
                """
                name: 'jaffle_shop'
    
                config-version: 2
                version: '0.1'
    
                profile: 'jaffle_shop'
    
                model-paths: [ "xmodels" ]
                seed-paths: [ "xseeds" ]
                test-paths: [ "xtests" ]
                analysis-paths: [ "xanalysis" ]
                macro-paths: [ "xmacros" ]
    
                target-path: "xtarget"
                clean-targets:
                  - "xtarget"
                  - "xdbt_modules"
                  - "xlogs"
    
                require-dbt-version: [ ">=1.0.0", "<2.0.0" ]
    
                models:
                  jaffle_shop:
                    +database: my_db
                    +schema: my_schema
                    staging:
                      +schema: my_schema2
    
                seeds:
                  jaffle_shop:
                    +database: seed_db
                    +schema: seed_schema
                    staging:
                      +schema: seed_schema2    
                """.trimIndent().byteInputStream()
            )
        ).isEqualTo(
            ProjectProps(
                name = "jaffle_shop",
                configVersion = 2,
                version = "0.1",
                profile = "jaffle_shop",
                modelPaths = listOf("xmodels"),
                seedPaths = listOf("xseeds"),
                testPaths = listOf("xtests"),
                analysisPaths = listOf("xanalysis"),
                macroPaths = listOf("xmacros"),
                targetPath = "xtarget",
                cleanTargets = listOf("xtarget", "xdbt_modules", "xlogs"),
                requireDbtVersion = listOf(">=1.0.0", "<2.0.0"),
                models = mapOf(
                    Locator(listOf("jaffle_shop")) to Config(
                        mapOf(
                            "database" to "my_db",
                            "schema" to "my_schema"
                        )
                    ),
                    Locator(listOf("jaffle_shop", "staging")) to Config(
                        mapOf(
                            "schema" to "my_schema2"
                        ),
                    )
                ),
                seeds = mapOf(
                    Locator(listOf("jaffle_shop")) to Config(
                        mapOf(
                            "database" to "seed_db",
                            "schema" to "seed_schema"
                        )
                    ),
                    Locator(listOf("jaffle_shop", "staging")) to Config(
                        mapOf(
                            "schema" to "seed_schema2"
                        )
                    ),
                )
            )
        )
    }


    @Test
    fun `parseProjectConf with required properties only`() {
        assertThat(
            ProjectParser.parseProjectProps(
                """
                name: 'jaffle_shop'
                config-version: 2
                version: '0.1'
                profile: 'jaffle_shop'
                """.trimIndent().byteInputStream()
            )
        ).isEqualTo(
            ProjectProps(
                name = "jaffle_shop",
                configVersion = 2,
                version = "0.1",
                profile = "jaffle_shop"
            )
        )
    }

    @Test
    fun parseProfilesConf() {
        assertThat(
            ProjectParser.parseProfiles(
                """
                main:
                  target: default
                  outputs:
                    default:
                      type: snowflake
                      database: my_db
                      schema: my_schema
                      threads: 1
                    production:
                      type: snowflake
                      database: prod
                      schema: analytics
                      threads: 3

                test:
                  target: default
                  outputs:
                    default:
                      type: sqlite
                      database: lite_db
                      schema: lite_schema
                      threads: 1
                """.trimIndent().byteInputStream()
            )
        ).isEqualTo(
            Profiles(
                mapOf(
                    "main" to Profile(
                        target = "default",
                        outputs = mapOf(
                            "default" to ProfileTarget(
                                type = "snowflake",
                                database = "my_db",
                                schema = "my_schema",
                                threads = 1
                            ),
                            "production" to ProfileTarget(
                                type = "snowflake",
                                database = "prod",
                                schema = "analytics",
                                threads = 3
                            )
                        )
                    ),
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

    @Test
    fun parseModelsSourcesProps() {
        assertThat(
            ProjectParser.parseModelsProps(
                """
                version: 2
    
                models:
                  - name: stg_orders
                    description: test desc
                    config:
                      database: dbname
                      schema: schemaname
                    columns:
                      - name: order_id
                        tests:
                          - unique
                          - not_null
                      - name: status
                        tests:
                          - accepted_values:
                              name: unexpected_order_status_today
                              values: ['placed', 'shipped', 'completed', 'return_pending', 'returned']
    
                sources:
                  - name: jaffle_shop
                    description: source desc
                    database: raw
                    schema: public
                    tables:
                      - name: orders
                        identifier: Orders_
                        columns:
                          - name: id
                            tests:
                              - unique
                          - name: price_in_usd
                            tests:
                              - not_null
    
                """.trimIndent().byteInputStream()
            )
        ).isEqualTo(
            ModelsProps(
                version = 2,
                models = setOf(
                    ModelProps(
                        name = "stg_orders",
                        description = "test desc",
                        config = Config(
                            mapOf(
                                "database" to "dbname",
                                "schema" to "schemaname",
                            )
                        ),
                        columns = listOf(
                            ColumnProps(
                                name = "order_id",
                                tests = listOf(
                                    TestProps(test = "unique"),
                                    TestProps(test = "not_null")
                                )
                            ),
                            ColumnProps(
                                name = "status",
                                tests = listOf(
                                    TestProps(
                                        test = "accepted_values",
                                        name = "unexpected_order_status_today",
                                        params = mapOf(
                                            "values" to listOf(
                                                "placed",
                                                "shipped",
                                                "completed",
                                                "return_pending",
                                                "returned"
                                            )
                                        )
                                    )
                                )
                            )
                        )
                    )
                ),
                sources = setOf(
                    SourceProps(
                        name = "jaffle_shop",
                        description = "source desc",
                        database = "raw",
                        schema = "public",
                        tables = setOf(
                            TableProps(
                                name = "orders",
                                identifier = "Orders_",
                                columns = listOf(
                                    ColumnProps(
                                        name = "id",
                                        tests = listOf(
                                            TestProps(test = "unique")
                                        )
                                    ),
                                    ColumnProps(
                                        name = "price_in_usd",
                                        tests = listOf(
                                            TestProps(test = "not_null")
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            )
        )

    }

    @Test
    fun parseSeedsProps() {
        assertThat(
            ProjectParser.parseSeedsProps(
                """
                    version: 2

                    seeds:
                      - name: my_seed
                        description: first seed
                        columns:
                          - name: id
                            tests:
                              - unique
                          - name: message
                """.trimIndent().byteInputStream()
            )
        ).isEqualTo(
            SeedsProps(
                version = 2,
                seeds = listOf(
                    SeedProps(
                        name = "my_seed",
                        description = "first seed",
                        columns = listOf(
                            ColumnProps(
                                name = "id",
                                tests = listOf(
                                    TestProps(
                                        test = "unique"
                                    )
                                )
                            ),
                            ColumnProps(
                                name = "message"
                            )
                        )
                    )
                )
            )
        )
    }
}