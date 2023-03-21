package com.github.kokorin.jdbt.loader

import com.github.kokorin.jdbt.domain.Locator
import com.github.kokorin.jdbt.domain.model.Project
import com.github.kokorin.jdbt.domain.model.Resource
import com.github.kokorin.jdbt.parser.ProjectParser
import io.github.oshai.KotlinLogging
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.name

private val logger = KotlinLogging.logger { }

open class ProjectLoader(private val resourceLocator: ResourceLocator) {

    companion object : ProjectLoader(ResourceLocator)

    fun load(projectDir: String?): Project {
        val projectFilePath = if (projectDir != null) {
            logger.info { "Loading project: $projectDir" }
            resourceLocator.findAt("dbt_project.yml", listOf(projectDir))
        } else {
            logger.info("Looking for a project bottom up")
            resourceLocator.findBottomUp("dbt_project.yml", ".")
        }
        val projectDirPath = projectFilePath.parent.normalize().toAbsolutePath()

        val projectProps = Files.newInputStream(projectFilePath).use(ProjectParser::parseProjectProps)

        val models = projectResources(projectProps.name, projectDirPath, projectProps.modelPaths, ".sql")

        val modelsProps = projectResources(projectProps.name, projectDirPath, projectProps.modelPaths, ".yml")
            .map(Resource::path)
            .map(projectDirPath::resolve)
            .map {
                Files.newInputStream(it).use(ProjectParser::parseModelsProps)
            }

        val seeds = projectResources(projectProps.name, projectDirPath, projectProps.seedPaths, ".csv")

        val seedsProps = projectResources(projectProps.name, projectDirPath, projectProps.seedPaths, ".yml")
            .map(Resource::path)
            .map(projectDirPath::resolve)
            .map {
                Files.newInputStream(it).use(ProjectParser::parseSeedsProps)
            }
        logger.debug { "Seed properties found: ${modelsProps.size}" }

        return Project(
            projectDirPath,
            projectProps,
            models,
            modelsProps,
            seeds,
            seedsProps
        )
    }

    internal fun projectResources(
        namespace: String,
        projectDir: Path,
        subpaths: List<String>,
        suffix: String
    ): List<Resource> =
        resourceLocator.findAllInDir(projectDir, subpaths, suffix)
            .flatMap { (subdir, paths) ->
                logger.debug { "Looking for files in ${subdir} with suffix $suffix" }
                paths.map { path ->
                    val name = path.fileName.name.removeSuffix(suffix)
                    val locator = Locator(listOf(namespace) + path.toList().dropLast(1).map(Path::toString) + name)
                    val fullPath = subdir.resolve(path)
                    Resource(
                        name = name,
                        namespace = namespace,
                        locator = locator,
                        path = fullPath
                    )
                }
            }

}
