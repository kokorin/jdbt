package com.github.kokorin.jdbt.domain.props

data class TableProps(
    val name:String,
    val identifier:String? = null,
    val tests: List<TestProps> = listOf(),
    val columns:List<ColumnProps> = listOf()
)