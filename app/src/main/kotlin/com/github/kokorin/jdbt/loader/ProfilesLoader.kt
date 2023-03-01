package com.github.kokorin.jdbt.loader

import com.github.kokorin.jdbt.parser.ProjectParser
import com.github.kokorin.jdbt.domain.props.Profiles
import com.github.kokorin.jdbt.sys.Os
import io.github.oshai.KotlinLogging
import java.nio.file.FileSystem
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path

private val logger = KotlinLogging.logger { }

open class ProfilesLoader constructor(private val os: Os, private val fileSystem: FileSystem) {
    companion object : ProfilesLoader(Os, FileSystems.getDefault())

    fun loadProfiles(profilesDir: String?): Profiles {
        val profiles = findProfiles(profilesDir)
        logger.info("Loading profiles: $profiles")
        return Files.newInputStream(profiles)
            .use(ProjectParser::parseProfiles)
    }

    internal fun findProfiles(profilesDir: String?): Path = ResourceLocator(fileSystem).findAt(
        filename = "profiles.yml",
        paths = listOfNotNull(
            profilesDir,
            os.getEnv("DBT_PROFILES_DIR"),
            ".",
            "${os.userHome}/.dbt"
        )
    )
}