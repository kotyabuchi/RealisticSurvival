package com.github.kotyabuchi.RealisticSurvival

import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.context.startKoin
import org.koin.dsl.module

class Main: JavaPlugin() {

    override fun onEnable() {
        setupKoin()
        println("Enabled")
    }

    override fun onDisable() {
        println("Disabled")
    }

    private val pluginModule = module {
        single { this@Main }
    }

    private fun setupKoin() {
        startKoin {
            modules(pluginModule)
        }
    }
}