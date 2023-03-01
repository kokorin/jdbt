package com.github.kokorin.jdbt.mock

import com.github.kokorin.jdbt.sys.Os
import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import java.nio.file.FileSystem
import java.nio.file.Files

fun mockOs(userHome: String = "", env: Map<String, String> = mapOf()): Os =
    object : Os {
        override val userHome: String
            get() = userHome

        override fun getEnv(name: String): String? =
            env[name]
    }

fun mockFs(files: Map<String, String>, workDir: String? = null): FileSystem {
    val conf = Configuration.forCurrentPlatform()
        .toBuilder()
        .also {
            if (workDir != null) {
                it.setWorkingDirectory(workDir)
            }
        }
        .build()

    return Jimfs.newFileSystem(conf).apply {
        files.forEach { (path, content) ->
            getPath(path).apply {
                parent?.let(Files::createDirectories)
                Files.writeString(this, content)
            }
        }
    }
}