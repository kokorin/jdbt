package com.github.kokorin.jdbt.domain.model

import com.github.kokorin.jdbt.domain.dag.Dag

data class CompiledProject(
    val project: Project,
    // TODO Dag or Set instead of List?
    val models: Dag<Model>
)
