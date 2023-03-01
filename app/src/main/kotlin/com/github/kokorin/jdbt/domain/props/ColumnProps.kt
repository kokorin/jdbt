package com.github.kokorin.jdbt.domain.props

data class ColumnProps(
    val name: String,
    val description:String? = null,
    val tests: List<TestProps> = listOf()
)