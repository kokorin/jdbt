package com.github.kokorin.jdbt.parser.jackson

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.node.ObjectNode
import com.github.kokorin.jdbt.domain.props.TestProps

private object MapTypeRef : TypeReference<Map<String, Any>>()
object TestPropsDeserializer : StdDeserializer<TestProps>(TestProps::class.java) {
    override fun deserialize(jp: JsonParser?, ctxt: DeserializationContext?): TestProps {
        val tree = jp?.codec?.readTree<JsonNode>(jp)!!

        // TODO add descriptions
        return if (tree.isTextual) {
            TestProps(test = tree.textValue())
        } else {
            val (test, node) = tree.fields().asSequence().first()
            val name = node["name"]?.textValue() ?: ""
            val paramsNode = (node as ObjectNode).without<ObjectNode>("name")
            val paramsParser = jp.codec.treeAsTokens(paramsNode)
            val params = jp.codec?.readValue<Map<String, Any>>(paramsParser, MapTypeRef) ?: mapOf()

            TestProps(test, name, params)
        }
    }
}