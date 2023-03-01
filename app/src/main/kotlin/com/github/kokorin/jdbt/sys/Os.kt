package com.github.kokorin.jdbt.sys

/**
 * Represents Operating System abstraction.
 */
interface Os {
    companion object : Os {
        override fun getEnv(name: String): String? =
            System.getenv(name)

        override val userHome: String
            get() = System.getProperty("user.home")!!
    }

    /**
     * Resolves OS environment variable.
     * @param name env var name
     */
    fun getEnv(name: String): String?

    /**
     * Current user home directory
     */
    val userHome: String
}