package com.github.kokorin.jdbt.domain.model

data class CompiledProject(
    val project: Project,
    // TODO Dag or Set instead of List?
    val models: List<Model>
)
