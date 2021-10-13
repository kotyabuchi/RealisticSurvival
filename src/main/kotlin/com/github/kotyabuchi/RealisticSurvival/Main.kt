package com.github.kotyabuchi.RealisticSurvival

import com.github.kotyabuchi.RealisticSurvival.Event.CustomEventCaller
import com.github.kotyabuchi.RealisticSurvival.Skill.MineAssist
import com.github.kotyabuchi.RealisticSurvival.Skill.TreeAssist
import com.github.kotyabuchi.RealisticSurvival.System.Combat.DamagePopup
import com.github.kotyabuchi.RealisticSurvival.System.Combat.Fracture
import com.github.kotyabuchi.RealisticSurvival.System.LevelTheFarmlandAnPath
import com.github.kotyabuchi.RealisticSurvival.System.ReplantSapling
import com.github.kotyabuchi.RealisticSurvival.System.SafeCropAndReplant
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
            // Combat
        pm.registerEvents(DamagePopup, this)
        pm.registerEvents(Fracture, this)

        pm.registerEvents(LevelTheFarmlandAnPath, this)
        pm.registerEvents(ReplantSapling, this)
        pm.registerEvents(SafeCropAndReplant, this)
        pm.registerEvents(SafeFarmland, this)
    }

    override fun onEnable() {
        setupKoin()
        registerEvents()
        println("Enabled")
    }

    override fun onDisable() {
        DamagePopup.clearPopup()
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