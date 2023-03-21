package com.github.kokorin.jdbt.template

import com.github.kokorin.jdbt.compiler.CompilationContext
import com.github.kokorin.jdbt.domain.model.Resource
import com.github.kokorin.jdbt.exception.JdbtException
import com.github.kokorin.jdbt.template.pebble.JdbtExtension
import io.pebbletemplates.pebble.PebbleEngine
import io.pebbletemplates.pebble.loader.FileLoader
import io.pebbletemplates.pebble.loader.Loader
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.io.Reader
import java.nio.file.Files
import java.nio.file.Path

class TemplateEngine(projectRoot: Path) {
    companion object {
        val jdbtContext = "_jdbt_context"
    }

    private val engine = PebbleEngine.Builder()
        .loader(ModelLoader(projectRoot))
        .newLineTrimming(false)
        .autoEscaping(false)
        .extension(JdbtExtension)
        .build()

    fun evaluateModel(context: CompilationContext, output: OutputStream): Unit {
        val writer = OutputStreamWriter(output)
        val pebbleContext = mutableMapOf(
            "this" to "this",
            jdbtContext to context
        )
        engine.getTemplate(context.model.path.toString()).evaluate(writer, pebbleContext)
    }
}

class ModelLoader(private val root: Path) : Loader<String> {
    override fun getReader(templateName: String?): Reader =
        Files.newBufferedReader(root.resolve(templateName!!))

    override fun setCharset(charset: String?) {
        TODO("Not yet implemented")
    }

    override fun setPrefix(prefix: String?) {
        TODO("Not yet implemented")
    }

    override fun setSuffix(suffix: String?) {
        TODO("Not yet implemented")
    }

    override fun resolveRelativePath(relativePath: String?, anchorPath: String?): String {
        TODO("Not yet implemented")
    }

    override fun createCacheKey(templateName: String?): String =
        templateName!!

    override fun resourceExists(templateName: String?): Boolean =
        true
}