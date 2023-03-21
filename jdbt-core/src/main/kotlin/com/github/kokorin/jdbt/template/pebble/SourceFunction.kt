package com.github.kokorin.jdbt.template.pebble

import com.github.kokorin.jdbt.compiler.CompilationContext
import com.github.kokorin.jdbt.domain.model.Relation
import com.github.kokorin.jdbt.exception.JdbtException
import com.github.kokorin.jdbt.fn.source
import com.github.kokorin.jdbt.template.TemplateEngine
import io.pebbletemplates.pebble.extension.Function
import io.pebbletemplates.pebble.template.EvaluationContext
import io.pebbletemplates.pebble.template.PebbleTemplate

object SourceFunction : Function {
    override fun getArgumentNames(): MutableList<String> = mutableListOf("source_name", "table_name")

    override fun execute(
        args: MutableMap<String, Any>?,
        self: PebbleTemplate?,
        context: EvaluationContext?,
        lineNumber: Int
    ): Relation {
        val sourceName = args?.get("source_name")?.toString()
            ?: throw JdbtException("Parameter source_name not specified")
        val tableName = args["table_name"]?.toString()
            ?: throw JdbtException("Parameter table_name not specified")
        val compilationContext = context?.getVariable(TemplateEngine.jdbtContext) as CompilationContext

        return source(sourceName, tableName, compilationContext)
    }
}
