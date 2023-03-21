package com.github.kokorin.jdbt.domain.props

import com.github.kokorin.jdbt.domain.Locator


data class ProjectProps(
    val name: String,
    val configVersion: Int,
    val version: String,
    val profile: String,

    val modelPaths: List<String> = listOf("models"),
    val seedPaths: List<String> = listOf("seeds"),
    val testPaths: List<String> = listOf("tests"),
    val analysisPaths: List<String> = listOf("analysis"),
    val macroPaths: List<String> = listOf("macros"),

    val targetPath: String = "target",
    val cleanTargets: List<String> = listOf("target", "dbt_modules", "logs"),

    val requireDbtVersion: List<String> = listOf(),

    val models: Map<Locator, Config> = mapOf(),
    val seeds: Map<Locator, Config> = mapOf()
)

