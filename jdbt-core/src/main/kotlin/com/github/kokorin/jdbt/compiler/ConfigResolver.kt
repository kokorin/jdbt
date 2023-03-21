package com.github.kokorin.jdbt.compiler

import com.github.kokorin.jdbt.domain.Locator
import com.github.kokorin.jdbt.domain.props.Config

class ConfigResolver(
    private val locatorToConfig: Map<Locator, Config>
) {
    /**
     * Resolves configuration for specified locator and all parent locators.
     */
    fun resolve(locator: Locator): Config =
        generateSequence(locator) { it.parent }
            .map(locatorToConfig::get)
            .filterNotNull()
            .fold(Config.empty) { acc, config ->
                acc.merge(config)
            }

}