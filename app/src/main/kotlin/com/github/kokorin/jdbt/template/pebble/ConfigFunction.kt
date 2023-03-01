package com.github.kokorin.jdbt.template.pebble

import com.github.kokorin.jdbt.compiler.CompilationContext
import com.github.kokorin.jdbt.exception.JdbtException
import com.github.kokorin.jdbt.fn.config
import com.github.kokorin.jdbt.template.TemplateEngine
import io.pebbletemplates.pebble.extension.Function
import io.pebbletemplates.pebble.template.EvaluationContext
import io.pebbletemplates.pebble.template.PebbleTemplate

object ConfigFunction : Function {
    //TODO pebble requires allowed argument names
    // ArgumentsNode.getArgumentMap(ArgumentsNode.java:98)
    override fun getArgumentNames(): MutableList<String> = mutableListOf("database", "schema", "enabled")

    override fun execute(
        args: MutableMap<String, Any>?,
        self: PebbleTemplate?,
        context: EvaluationContext?,
        lineNumber: Int
    ): Unit {
        val params = args?.toMap()
            ?: throw JdbtException("No arguments")
        val compilationContext = context?.getVariable(TemplateEngine.jdbtContext) as CompilationContext

        config(params, compilationContext)
    }
}