package com.github.kokorin.jdbt.fn

import com.github.kokorin.jdbt.compiler.CompilationContext
import com.github.kokorin.jdbt.domain.props.Config

fun config(params:Map<String, Any>, context: CompilationContext):Unit {
    context.config = Config(params).merge(context.config)
}