package com.github.kokorin.jdbt.fn

import com.github.kokorin.jdbt.compiler.CompilationContext
import com.github.kokorin.jdbt.domain.model.Relation
import com.github.kokorin.jdbt.exception.JdbtException

fun ref(name: String, context: CompilationContext): Relation  {
    val refConfig = context.modelConfigs[name]
        ?: throw JdbtException("Config for ref $name not found")

    val refModel = context.models[name]
        ?: throw JdbtException("Model for ref $name not found")

    context.dependsOn.add(refModel)

    return Relation(
        database = refConfig.database ?: context.target.database,
        schema = refConfig.schema ?: context.target.schema,
        identifier = name
    )
}
