package com.github.kokorin.jdbt.loader

import com.github.kokorin.jdbt.exception.JdbtException
import io.github.oshai.KotlinLogging
import java.nio.file.FileSystem
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes
import java.util.*

private val logger = KotlinLogging.logger { }

private val maxSearchDepth = 128

open class ResourceLocator(private val fileSystem: FileSystem) {
    companion object : ResourceLocator(FileSystems.getDefault())

    /**
     * Finds for a file at specified path or at any parent directory
     * @param filename name of a file to look for
     * @param path path to start looking at
     * @throws JdbtException if no file found
     */
    fun findBottomUp(filename: String, path: String): Path {
        var current: Path? = fileSystem.getPath(path)

        while (current != null) {
            val candidate = current.resolve(filename)
            if (Files.exists(candidate)) {
                return candidate
            }
            logger.debug { "File $filename not found at $current, continue with parent" }
            current = current.parent
        }

        throw JdbtException("File $filename not found at path $path or at any parent directory")
    }

    /**
     * Finds for a file at specified paths (in order)
     * @param filename name of a file to look for
     * @param paths paths to look at
     * @throws JdbtException if no file found
     * @return first file found at specified paths (in order)
     */
    fun findAt(filename: String, paths: List<String>): Path {
        for (path in paths) {
            val candidate = fileSystem.getPath(path).resolve(filename)
            if (Files.exists(candidate)) {
                return candidate
            }
            logger.debug { "File $candidate not found, continue search" }
        }

        throw JdbtException("File $filename not found at any of ${paths.joinToString()}")
    }

    /**
     * Finds all files in specified directory with specified suffix (including subdirectories).
     * @param dir directory to look for files
     * @param subdirs subdirectory paths (relative to directory)
     * @param suffix filename suffix, e.g. '.sql'
     * @return map of subdirectory path to list of file paths (relative to subdirectory)
     */
    fun findAllInDir(dir: Path, subdirs: List<String>, suffix: String): Map<Path, Set<Path>> {
        fun filterBySuffix(path: Path, attr: BasicFileAttributes): Boolean =
            path.fileName.toString().endsWith(suffix)

        return subdirs.map(fileSystem::getPath)
            .associateWith { subPath ->
                val subdir = dir.resolve(subPath)
                listOf(subdir)
                    .filter(Files::exists)
                    .flatMap { Files.find(it, maxSearchDepth, ::filterBySuffix).toList() }
                    .map(subdir::relativize)
                    .toSet()
            }
    }
}