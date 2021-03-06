package com.github.kotyabuchi.RealisticSurvival.System

import org.bukkit.Material
import org.bukkit.entity.Item
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBurnEvent
import org.bukkit.event.block.BlockIgniteEvent
import org.bukkit.event.block.BlockSpreadEvent
import org.bukkit.event.entity.EntityCombustEvent

object WorldGuard: Listener {

    @EventHandler
    fun onIgniteItem(event: EntityCombustEvent) {
        val entity = event.entity
        if (entity !is Item) return
        event.isCancelled = true
    }

    @EventHandler
    fun onBurnBlock(event: BlockBurnEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun onSpread(event: BlockSpreadEvent) {
        val source = event.source.type
        if (source == Material.FIRE ||
                source == Material.SOUL_FIRE) {
            event.isCancelled = true
        }
    }
}