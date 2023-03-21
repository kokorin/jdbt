package com.github.kokorin.jdbt.fn

class FunctionTest {
    /*val project = Project(
        root = Paths.get("."),
        projectProps = ProjectProps(
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
            requireDbtVersion = listOf(">=1.0.0", "<2.0.0"),
            models = mapOf(
                "jaffle_shop" to mapOf(
                    "materialized" to "table",
                    "staging" to mapOf(
                        "materialized" to "view"
                    )
                )
            )
        ),
        targetProps = TargetProps(
            type = "sqlite",
            database = "lite_db",
            schema = "lite_schema",
            threads = 1
        ),
        models = listOf(
            Model(
                name = "first_model",
                `package` = Package(listOf()),
                path = Paths.get("models/first_model.sql"),
                props = null
            )
        ).associateBy { it.name },
        sources = mapOf(),
        seeds = mapOf()
    )

    @Test
    fun refFn() {
        val context = CompilationContext(project)
        val expected = Relation("lite_db", "lite_schema", "first_model")
        val actual = ref("first_model", context)

        assertEquals(expected, actual)
        assertContains(context.relations(), expected)
    }*/
}