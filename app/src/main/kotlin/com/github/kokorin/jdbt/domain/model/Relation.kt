package com.github.kokorin.jdbt.domain.model

/**
 * @param database The name of the database for this relation
 * @param schema The name of the schema (or dataset, if on BigQuery) for this relation
 * @param identifier The name of the identifier for this relation
 * @param type Metadata about this relation, eg: "table", "view", "cte"
 */
data class Relation(
    val database: String? = null,
    val schema: String? = null,
    val identifier: String? = null,
    val type: String? = null
) {
    override fun toString(): String = "$database.$schema.$identifier"
}