package com.github.kotyabuchi.RealisticSurvival

import com.github.kotyabuchi.RealisticSurvival.Event.CustomEventCaller
import com.github.kotyabuchi.RealisticSurvival.Job.JobType
import com.github.kotyabuchi.RealisticSurvival.Menu.MenuController
import com.github.kotyabuchi.RealisticSurvival.System.*
import com.github.kotyabuchi.RealisticSurvival.System.Combat.DamagePopup
import com.github.kotyabuchi.RealisticSurvival.System.Player.PlayerManageCommand
import com.github.kotyabuchi.RealisticSurvival.System.Player.PlayerManager
import com.github.kotyabuchi.RealisticSurvival.Utility.DataBaseManager
import org.bukkit.NamespacedKey
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
        // System
            // Combat
        pm.registerEvents(DamagePopup, this)
            // Player
        pm.registerEvents(PlayerManager, this)

        pm.registerEvents(AnimalShearing, this)
        pm.registerEvents(LevelTheFarmlandAnPath, this)
        pm.registerEvents(ReplantSapling, this)
        pm.registerEvents(SafeCropAndReplant, this)
        pm.registerEvents(SafeFarmland, this)
        pm.registerEvents(SortChest, this)
        pm.registerEvents(StarterItem, this)
        pm.registerEvents(StoneGenerator, this)
    }

    private fun registerCommands() {
        getCommand("sort")?.setExecutor(SortChest)
        getCommand("playermanager")?.setExecutor(PlayerManageCommand)
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
        PlayerManager.hideAllManaIndicator()
        refreshBossbar()
        println("Disabled")
    }

    private fun refreshBossbar() {
        val cache = mutableSetOf<NamespacedKey>()
        server.bossBars.forEach {
            it.removeAll()
            it.isVisible = false
            cache.add(it.key)
        }
        cache.forEach {
            server.removeBossBar(it)
        }
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