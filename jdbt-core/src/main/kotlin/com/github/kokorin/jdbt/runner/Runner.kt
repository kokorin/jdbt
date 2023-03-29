package com.github.kokorin.jdbt.runner

import com.github.kokorin.jdbt.adapter.Adapter
import com.github.kokorin.jdbt.domain.model.CompiledProject

class Runner(private val project:CompiledProject, private val adapter:Adapter) {
    fun run():Unit {
        project.models
    }
}