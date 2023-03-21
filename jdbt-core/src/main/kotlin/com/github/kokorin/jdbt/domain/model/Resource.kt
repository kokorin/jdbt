package com.github.kokorin.jdbt.domain.model

import com.github.kokorin.jdbt.domain.Locator
import java.nio.file.Path

/**
 * @param name resource name
 * @param namespace resource namespace (project or module name)
 * @param locator resource locator (directory names from resource root folder to resource, not including resource name)
 * @param path path to resource file
 */
data class Resource(
    val name: String,
    val namespace: String,
    val locator: Locator,
    val path: Path
) {
    init {
        check(!path.isAbsolute) { "Resource path must be relative (to project dir)" }
    }
}