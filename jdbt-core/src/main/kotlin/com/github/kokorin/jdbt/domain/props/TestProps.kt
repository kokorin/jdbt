package com.github.kokorin.jdbt.domain.props

data class TestProps(
    val test:String,
    val name: String? = null,
    val params: Map<String, Any> = mapOf()
)