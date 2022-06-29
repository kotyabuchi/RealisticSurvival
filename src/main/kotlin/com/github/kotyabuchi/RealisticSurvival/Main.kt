package com.github.kotyabuchi.RealisticSurvival

import com.github.kotyabuchi.RealisticSurvival.Event.CustomEventCaller
import com.github.kotyabuchi.RealisticSurvival.Job.JobType
import com.github.kotyabuchi.RealisticSurvival.Menu.MenuController
import com.github.kotyabuchi.RealisticSurvival.Menu.SoundSampleMenu
import com.github.kotyabuchi.RealisticSurvival.Monster.*
import com.github.kotyabuchi.RealisticSurvival.System.*
import com.github.kotyabuchi.RealisticSurvival.System.Combat.DamagePopup
import com.github.kotyabuchi.RealisticSurvival.System.Item.*
import com.github.kotyabuchi.RealisticSurvival.System.Player.HomePoint
import com.github.kotyabuchi.RealisticSurvival.System.Player.PlayerManageCommand
import com.github.kotyabuchi.RealisticSurvival.System.Player.PlayerManager
import com.github.kotyabuchi.RealisticSurvival.Utility.DataBaseManager
import org.bukkit.NamespacedKey
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.context.startKoin
import org.koin.dsl.module

class Main: JavaPlugin() {

    fun registerEvent(vararg events: Listener) {
        val pm = server.pluginManager
        events.forEach {
            pm.registerEvents(it, this)
        }
    }

    private fun registerEvents() {
        val pm = server.pluginManager
        registerEvent(
            CustomEventCaller,
            Debug
        )
        // Job
        JobType.values().forEach {
            pm.registerEvents(it.jobClass, this)
        }
        registerEvent(
            // Menu
            MenuController,
            SoundSampleMenu,
            // Monster
            AdvCreeper,
            AdvEnderman,
            AdvSkeleton,
            AdvSpider,
            AdvWitch,
            AdvZombie,
            // System
                // Combat
            DamagePopup,
//            HealthBar,
                // Item
            AnvilPressCraft,
            CraftExtension,
            GlobalMending,
            ItemExtensionManager,
            UUIDForItem,
                // Player
            HomePoint,
            PlayerManager,
                // Other
            AnimalShearing,
            BlockPlacer,
            ChatSound,
            Elevator,
            LevelTheFarmlandAnPath,
            ReplantSapling,
            SafeCropAndReplant,
            SafeFarmland,
            SortChest,
            StarterItem,
            StoneGenerator,
            TombStone,
            UtilityRecipe,
            WorldGuard,
        )
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
        UtilityRecipe.registerRecipe()
        println("Enabled")
    }

    override fun onDisable() {
        DataBaseManager.savePlayerStatus()
        TombStone.saveTombStoneFile()
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