package com.github.kokorin.jdbt.template.pebble

import com.github.kokorin.jdbt.compiler.CompilationContext
import com.github.kokorin.jdbt.domain.model.Relation
import com.github.kokorin.jdbt.fn.ref
import com.github.kokorin.jdbt.template.TemplateEngine
import io.pebbletemplates.pebble.extension.Function
import io.pebbletemplates.pebble.template.EvaluationContext
import io.pebbletemplates.pebble.template.PebbleTemplate

object RefFunction : Function {
    override fun getArgumentNames(): MutableList<String> = mutableListOf("name")

    override fun execute(
        args: MutableMap<String, Any>?,
        self: PebbleTemplate?,
        context: EvaluationContext?,
        lineNumber: Int
    ): Relation {
        val name = args?.get("name") as String
        val compilationContext = context?.getVariable(TemplateEngine.jdbtContext) as CompilationContext

        return ref(name, compilationContext)
    }
}