package com.github.kokorin.jdbt.compiler

import com.github.kokorin.jdbt.domain.model.Project
import com.github.kokorin.jdbt.domain.model.Resource
import com.github.kokorin.jdbt.domain.props.Config
import com.github.kokorin.jdbt.domain.props.ProfileTarget
import com.github.kokorin.jdbt.domain.props.SourceProps

data class CompilationContext(
    val project: Project,
    val target: ProfileTarget,
    val model: Resource,
    val models: Map<String, Resource>,
    val modelConfigs: Map<String, Config>,
    val sourceProps: Map<String, SourceProps>
) {
    val dependsOn: MutableSet<Resource> = mutableSetOf()
    var config: Config = modelConfigs[model.name]!!
}