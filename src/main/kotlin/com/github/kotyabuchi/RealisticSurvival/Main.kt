package com.github.kotyabuchi.RealisticSurvival

import com.github.kotyabuchi.RealisticSurvival.Event.CustomEventCaller
import com.github.kotyabuchi.RealisticSurvival.Skill.MineAssist
import com.github.kotyabuchi.RealisticSurvival.Skill.TreeAssist
import com.github.kotyabuchi.RealisticSurvival.System.LevelTheFarmlandAnPath
import com.github.kotyabuchi.RealisticSurvival.System.SafeFarmland
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.context.startKoin
import org.koin.dsl.module

class Main: JavaPlugin() {

    private fun registerEvents() {
        val pm = server.pluginManager
        pm.registerEvents(CustomEventCaller, this)
        // Skill
        pm.registerEvents(TreeAssist, this)
        pm.registerEvents(MineAssist, this)
        // System
        pm.registerEvents(LevelTheFarmlandAnPath, this)
        pm.registerEvents(SafeFarmland, this)
    }

    override fun onEnable() {
        setupKoin()
        registerEvents()
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