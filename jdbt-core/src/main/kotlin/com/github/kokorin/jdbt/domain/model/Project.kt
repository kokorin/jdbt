package com.github.kokorin.jdbt.domain.model

import com.github.kokorin.jdbt.domain.props.ModelsProps
import com.github.kokorin.jdbt.domain.props.ProjectProps
import com.github.kokorin.jdbt.domain.props.SeedsProps
import java.nio.file.Path

data class Project(
    val rootDir: Path,
    val props: ProjectProps,
    val models: List<Resource>,
    val modelsProps: List<ModelsProps>,
    val seeds: List<Resource>,
    val seedsProps: List<SeedsProps>
) {
    init {
        check(rootDir.isAbsolute) { "Project root directory must be absolute: $rootDir" }
    }
}
