package com.github.kotyabuchi.RealisticSurvival.System

import com.github.kotyabuchi.RealisticSurvival.Main
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockFromToEvent
import org.bukkit.scheduler.BukkitRunnable
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object StoneGenerator: Listener, KoinComponent {

    private val main: Main by inject()

    @EventHandler
    fun onChange(event: BlockFromToEvent) {
        val fromBlock = event.block
        val toBlock = event.toBlock

        if (fromBlock.type != Material.LAVA) return

        object : BukkitRunnable() {
            override fun run() {
                if (toBlock.type != Material.COBBLESTONE) return
                if (toBlock.location.y <= 15) {
                    toBlock.type = Material.DEEPSLATE
                    return
                }
                toBlock.type = when (toBlock.getRelative(BlockFace.DOWN).type) {
                    Material.DEEPSLATE -> {
                        Material.DEEPSLATE
                    }
                    Material.GRANITE -> {
                         Material.GRANITE
                    }
                    Material.DIORITE -> {
                        Material.DIORITE
                    }
                    Material.ANDESITE -> {
                        Material.ANDESITE
                    }
                    Material.CALCITE -> {
                        Material.CALCITE
                    }
                    Material.TUFF -> {
                        Material.TUFF
                    }
                    else -> return
                }
            }
        }.runTaskLater(main, 0)
    }
}