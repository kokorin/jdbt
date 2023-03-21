package com.github.kokorin.jdbt.domain

data class Locator(private val path: List<String>) {
    constructor(vararg path: String) : this(path.toList())

    val parent: Locator?
        get() = if (path.isNotEmpty()) {
            Locator(path.dropLast(1))
        } else {
            null
        }

    fun child(pathElement: String): Locator =
        Locator(path + pathElement)
}