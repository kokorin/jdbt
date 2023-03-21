package com.github.kokorin.jdbt.parser

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.kokorin.jdbt.domain.props.*
import com.github.kokorin.jdbt.parser.jackson.ProfilesDeserializer
import com.github.kokorin.jdbt.parser.jackson.ProjectPropsDeserializer
import com.github.kokorin.jdbt.parser.jackson.TestPropsDeserializer
import io.github.oshai.KotlinLogging
import java.io.InputStream

private val logger = KotlinLogging.logger { }

object ProjectParser {
    private val mapper = ObjectMapper(YAMLFactory())
        .registerModule(KotlinModule())
        .registerModule(
            SimpleModule()
                .addDeserializer(Profiles::class.java, ProfilesDeserializer)
                .addDeserializer(TestProps::class.java, TestPropsDeserializer)
                .addDeserializer(ProjectProps::class.java, ProjectPropsDeserializer)
        )

    fun parseProjectProps(input: InputStream): ProjectProps =
        mapper.readValue(input)

    fun parseProfiles(inputStream: InputStream): Profiles {
        logger.debug("Parsing profiles")
        return mapper.readValue(inputStream)
    }
    fun parseModelsProps(input: InputStream): ModelsProps =
        mapper.readValue(input)

    fun parseSeedsProps(input: InputStream): SeedsProps =
        mapper.readValue(input)

}