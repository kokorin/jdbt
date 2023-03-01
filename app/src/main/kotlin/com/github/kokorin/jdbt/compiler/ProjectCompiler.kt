package com.github.kokorin.jdbt.compiler

import com.github.kokorin.jdbt.domain.model.CompiledProject
import com.github.kokorin.jdbt.domain.model.Model
import com.github.kokorin.jdbt.domain.model.Project
import com.github.kokorin.jdbt.domain.model.Resource
import com.github.kokorin.jdbt.domain.props.*
import com.github.kokorin.jdbt.template.TemplateEngine
import java.nio.file.Files
import java.nio.file.StandardOpenOption.*

class ProjectCompiler(
    private val project: Project,
    private val target: ProfileTarget
) {
    private val templateEngine = TemplateEngine(project.rootDir)

    fun compile(): CompiledProject {
        val modelConfigs = project.models.associate { it.name to resolveModelConfig(it) }

        val compileConfigs = project.models
            .associate { it.name to compileModel(it, modelConfigs) }
            .entries.associate { (name, model) ->
                name to model.config
            }

        // TODO optimize: only models which depend on models with updated configs (in model file
        //  using `{{ config(...) }}` function)
        val models = project.models
            .map { compileModel(it, compileConfigs) }

        return CompiledProject(
            project,
            models
        )
    }

    internal fun resolveModelConfig(model: Resource): Config {
        // Config for current model based on dbt_project.yml file
        val projectConfig = ConfigResolver(project.props.models)
            .resolve(model.locator)
            .merge(
                Config(
                    "database" to target.database,
                    "schema" to target.schema,
                )
            )

        val modelConfig = project.modelsProps.flatMap { it.models }
            .filter { it.name == model.name }
            .map(ModelProps::config)
            .firstOrNull() ?: Config.empty

        return modelConfig.merge(projectConfig)
    }

    internal fun compileModel(model: Resource, modelConfigs: Map<String, Config>): Model {
        val path = project.rootDir
            .resolve(project.props.targetPath)
            .resolve(model.namespace)
            .resolve("compiled")
            .resolve(model.path)
        Files.createDirectories(path.parent)

        val models = project.models.associateBy(Resource::name)
        val sourceProps = project.modelsProps.flatMap(ModelsProps::sources)
            .associateBy(SourceProps::name)
        val context = CompilationContext(project, target, model, models, modelConfigs, sourceProps)
        templateEngine.evaluateModel(context, Files.newOutputStream(path, CREATE, TRUNCATE_EXISTING, WRITE))

        return Model(
            model,
            context.config,
            context.dependsOn.toSet(),
            path
        )
    }

}