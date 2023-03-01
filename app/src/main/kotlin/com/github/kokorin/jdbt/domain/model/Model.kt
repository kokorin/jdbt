package com.github.kokorin.jdbt.domain.model

import com.github.kokorin.jdbt.domain.props.Config
import java.nio.file.Path

data class Model(
    val resource: Resource,
    val config:Config,
    val dependsOn: Set<Resource>,
    val compiledPath: Path
)