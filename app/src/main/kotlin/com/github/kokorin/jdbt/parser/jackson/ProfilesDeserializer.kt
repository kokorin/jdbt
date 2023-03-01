package com.github.kokorin.jdbt.parser.jackson

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.github.kokorin.jdbt.domain.props.Profile
import com.github.kokorin.jdbt.domain.props.Profiles

object ProfilesDeserializer : StdDeserializer<Profiles>(Profiles::class.java) {
    override fun deserialize(jp: JsonParser?, ctxt: DeserializationContext?): Profiles {
        val profiles = jp?.codec?.readTree<JsonNode>(jp)?.fields()?.asSequence()?.map {
            val name = it.key as String
            val profile = jp.codec.treeToValue(it.value, Profile::class.java) as Profile
            name to profile
        }?.toMap()!!

        return Profiles(profiles)
    }
}