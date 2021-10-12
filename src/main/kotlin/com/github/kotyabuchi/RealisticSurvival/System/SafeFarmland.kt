package com.github.kotyabuchi.RealisticSurvival.System

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityChangeBlockEvent

object SafeFarmland: Listener {

    @EventHandler
    fun onStep(event: EntityChangeBlockEvent) {
        if (event.block.type == Material.FARMLAND) {
            event.isCancelled = true
        }
    }
}