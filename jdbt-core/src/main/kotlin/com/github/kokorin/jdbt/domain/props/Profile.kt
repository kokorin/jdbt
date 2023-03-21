package com.github.kokorin.jdbt.domain.props

import com.github.kokorin.jdbt.exception.JdbtException

data class Profiles(
    val profiles: Map<String, Profile>
) {
    fun selectTarget(profileName: String, targetName: String?): ProfileTarget {
        val profile = profiles[profileName]
            ?: throw JdbtException("Profile $profileName not found")
        val targetNameOrDefault = targetName ?: profile.target
        return profile.outputs[targetNameOrDefault]
            ?: throw JdbtException("Target not found: $targetNameOrDefault")
    }
}

data class Profile(
    val target: String,
    val outputs: Map<String, ProfileTarget>
)

data class ProfileTarget(
    val type: String,
    val database: String,
    val schema: String,
    val threads: Int
)
