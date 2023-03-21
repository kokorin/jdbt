package com.github.kokorin.jdbt.template.pebble

import io.pebbletemplates.pebble.extension.AbstractExtension
import io.pebbletemplates.pebble.extension.Function

object JdbtExtension : AbstractExtension() {
    override fun getFunctions(): MutableMap<String, Function> {
        return mutableMapOf(
            "ref" to RefFunction,
            "source" to SourceFunction,
            "config" to ConfigFunction
        )
    }

}