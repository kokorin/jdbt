package com.github.kokorin.jdbt.parser.jackson

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.node.JsonNodeType
import com.github.kokorin.jdbt.domain.Locator
import com.github.kokorin.jdbt.domain.props.Config
import com.github.kokorin.jdbt.domain.props.ProjectProps
import com.github.kokorin.jdbt.exception.JdbtException

object ProjectPropsDeserializer : StdDeserializer<ProjectProps>(ProjectProps::class.java) {
    override fun deserialize(parser: JsonParser?, ctxt: DeserializationContext?): ProjectProps {
        val tree = parser?.codec?.readTree<JsonNode>(parser)
            ?: throw JdbtException("Can't read JSON tree")

        val name = tree.get("name")?.asText()
            ?: throw JdbtException("Property \"name\" not found")

        val configVersion = tree.get("config-version")?.asInt()
            ?: throw JdbtException("Property \"config-version\" not found")

        val version = tree.get("version")?.asText()
            ?: throw JdbtException("Property \"version\" not found")

        val profile = tree.get("profile")?.asText()
            ?: throw JdbtException("Property \"profile\" not found")

        val result = ProjectProps(name = name, configVersion = configVersion, version = version, profile = profile)

        // Use default values if no property is specified
        val modelPaths = asStringList(tree.get("model-paths")) ?: result.modelPaths
        val seedPaths = asStringList(tree.get("seed-paths")) ?: result.seedPaths
        val testPaths = asStringList(tree.get("test-paths")) ?: result.testPaths
        val analysisPaths = asStringList(tree.get("analysis-paths")) ?: result.analysisPaths
        val macroPaths = asStringList(tree.get("macro-paths")) ?: result.macroPaths
        val targetPath = tree.get("target-path")?.asText() ?: result.targetPath
        val cleanTargets = asStringList(tree.get("clean-targets")) ?: result.cleanTargets
        val requireDbtVersion = asStringList(tree.get("require-dbt-version")) ?: result.requireDbtVersion

        val models = parseLocatorToConfig(tree.path("models"))
        val seeds = parseLocatorToConfig(tree.path("seeds"))

        return result.copy(
            modelPaths = modelPaths,
            seedPaths = seedPaths,
            testPaths = testPaths,
            analysisPaths = analysisPaths,
            macroPaths = macroPaths,
            targetPath = targetPath,
            cleanTargets = cleanTargets,
            requireDbtVersion = requireDbtVersion,
            models = models,
            seeds = seeds
        )
    }

    private fun asStringList(node: JsonNode?): List<String>? =
        node?.elements()?.asSequence()?.map { it.asText() }?.toList()

    private fun parseLocatorToConfig(node: JsonNode): Map<Locator, Config> {
        fun asLocatorToConfig(current: Locator, node: JsonNode): Map<Locator, Config> {
            val configValues =
                node.fields().asSequence()
                    .filter { it.key.startsWith("+") }
                    .associate { entry ->
                        val key = entry.key.removePrefix("+")
                        val value = entry.value.let {
                            when (it.nodeType) {
                                JsonNodeType.STRING -> it.textValue()
                                JsonNodeType.BOOLEAN -> it.booleanValue()
                                JsonNodeType.NUMBER -> it.numberValue()
                                else -> it.asText()
                            }
                        }
                        key to value
                    }

            val currentToConfig = mapOf(current to Config(configValues))

            return node.fields().asSequence()
                .filterNot { it.key.startsWith("+") }
                .map { asLocatorToConfig(current.child(it.key), it.value) }
                .fold(currentToConfig) { acc, locationToConf ->
                    acc + locationToConf
                }
        }

        return asLocatorToConfig(Locator(), node)
            .filterValues(Config::isNotEmpty)
    }
}