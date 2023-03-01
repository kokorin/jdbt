package com.github.kokorin.jdbt.domain.model

data class CompiledProject(
    val project: Project,
    // TODO Set instead of List?
    val models: List<Model>
)
