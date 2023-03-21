package com.github.kokorin.jdbt.domain.model

import java.nio.file.Path

data class Seed(
    val name: String,
    val namespace: List<String>,
    val path: Path
)
