package com.github.kotyabuchi.RealisticSurvival

import com.github.kotyabuchi.RealisticSurvival.Event.CustomEventCaller
import com.github.kotyabuchi.RealisticSurvival.Job.JobType
import com.github.kotyabuchi.RealisticSurvival.Menu.MenuController
import com.github.kotyabuchi.RealisticSurvival.Skill.Gathering.MineAssist
import com.github.kotyabuchi.RealisticSurvival.Skill.Gathering.TreeAssist
import com.github.kotyabuchi.RealisticSurvival.System.*
import com.github.kotyabuchi.RealisticSurvival.System.Combat.DamagePopup
import com.github.kotyabuchi.RealisticSurvival.System.Combat.Fracture
import com.github.kotyabuchi.RealisticSurvival.System.Player.PlayerManager
import com.github.kotyabuchi.RealisticSurvival.Utility.DataBaseManager
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.context.startKoin
import org.koin.dsl.module

class Main: JavaPlugin() {

    private fun registerEvents() {
        val pm = server.pluginManager
        pm.registerEvents(CustomEventCaller, this)
        // Job
        JobType.values().forEach {
            pm.registerEvents(it.jobClass, this)
        }
        // Menu
        pm.registerEvents(MenuController, this)
        // Skill
        pm.registerEvents(TreeAssist, this)
        pm.registerEvents(MineAssist, this)
        // System
            // Combat
        pm.registerEvents(DamagePopup, this)
        pm.registerEvents(Fracture, this)
            // Player
        pm.registerEvents(PlayerManager, this)

        pm.registerEvents(AnimalShearing, this)
        pm.registerEvents(LevelTheFarmlandAnPath, this)
        pm.registerEvents(ReplantSapling, this)
        pm.registerEvents(SafeCropAndReplant, this)
        pm.registerEvents(SafeFarmland, this)
        pm.registerEvents(SortChest, this)
        pm.registerEvents(StarterItem, this)
    }

    private fun registerCommands() {
        getCommand("sort")?.setExecutor(SortChest)
    }

    override fun onEnable() {
        setupKoin()
        if (!dataFolder.exists()) dataFolder.mkdirs()
        DataBaseManager.initDB()
        DataBaseManager.startAutoSaveScheduler()
        registerEvents()
        registerCommands()
        println("Enabled")
    }

    override fun onDisable() {
        DamagePopup.clearPopup()
        DataBaseManager.savePlayerStatus()
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