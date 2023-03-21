package com.github.kokorin.jdbt.fn

import com.github.kokorin.jdbt.compiler.CompilationContext
import com.github.kokorin.jdbt.domain.model.Relation
import com.github.kokorin.jdbt.exception.JdbtException

fun source(sourceName: String, tableName: String, context: CompilationContext): Relation {
    val source = context.sourceProps[sourceName]
        ?: throw JdbtException("Source $sourceName not found")

    return Relation(
        source.database ?: context.target.database,
        source.schema ?: context.target.schema,
        tableName
    )
}
