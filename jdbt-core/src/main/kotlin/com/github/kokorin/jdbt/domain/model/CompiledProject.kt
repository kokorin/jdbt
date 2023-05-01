package com.github.kokorin.jdbt.domain.model

import com.github.kokorin.jdbt.domain.dag.Dag

data class CompiledProject(
    val project: Project,
    val modelDag: Dag<Model>
)
