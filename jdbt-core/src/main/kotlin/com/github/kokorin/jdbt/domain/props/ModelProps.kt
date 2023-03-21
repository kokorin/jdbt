package com.github.kokorin.jdbt.domain.props

data class ModelsProps(
    val version: Int,
    val models: Set<ModelProps> = setOf(),
    val sources: Set<SourceProps> = setOf()
)

data class ModelProps(
    val name:String,
    val description:String? = null,
    val config: Config = Config.empty,
    val tests: List<TestProps> = listOf(),
    val columns:List<ColumnProps> = listOf()
)

data class SourceProps(
    val name:String,
    val description: String? = null,
    val config: Config = Config.empty,
    val database:String? = null,
    val schema:String? = null,
    val tables: Set<TableProps> = setOf()
)