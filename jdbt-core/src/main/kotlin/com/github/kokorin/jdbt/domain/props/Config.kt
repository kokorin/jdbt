package com.github.kokorin.jdbt.domain.props

data class Config(private val data: Map<String, Any>) {
    constructor(vararg data: Pair<String, Any>) : this(data.toMap())

    companion object {
        val empty = Config(mapOf())
    }

    val database: String? get() = string("database")
    val schema: String? get() = string("schema")
    val enabled: Boolean get() = boolean("enabled") ?: true

    fun string(key: String): String? =
        data[key]?.toString()

    fun int(key: String): Int? =
        data[key]?.let {
            when (it) {
                is String -> it.toInt()
                is Number -> it.toInt()
                else -> throw NumberFormatException("Cant convert to integer: $it")
            }
        }

    fun boolean(key: String): Boolean? =
        data[key]?.let {
            when (it) {
                is String -> it == "true" || it == "1"
                is Boolean -> it
                else -> throw NumberFormatException("Cant convert to boolean: $it")
            }
        }

    fun isEmpty() = data.isEmpty()
    fun isNotEmpty() = data.isNotEmpty()

    /**
     * Merges this config with another config. Key from another config is added only
     * if the same key is absent in this config.
     * TODO more appropriate (e.g. addAbsent or addMissing or updateAbsent?)
     */
    fun merge(other: Config): Config =
        Config(other.data + this.data)
}
