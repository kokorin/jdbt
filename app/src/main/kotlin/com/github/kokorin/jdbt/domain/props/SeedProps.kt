package com.github.kokorin.jdbt.domain.props

data class SeedsProps(
    val version: Int,
    val seeds:List<SeedProps>
)

data class SeedProps(
    val name:String,
    val description: String? = null,
    val config: Config = Config.empty,
    val columns: List<ColumnProps> = listOf()
)
