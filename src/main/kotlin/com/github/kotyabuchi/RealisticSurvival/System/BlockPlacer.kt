package com.github.kotyabuchi.RealisticSurvival.System

import com.github.kotyabuchi.RealisticSurvival.Utility.consume
import org.bukkit.block.Dispenser
import org.bukkit.block.data.Directional
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockDispenseEvent

object BlockPlacer: Listener {

    @EventHandler
    fun onLaunch(event: BlockDispenseEvent) {
        val block = event.block
        val item = event.item
        if (!item.type.isBlock) return
        event.isCancelled = true
        val blockData = block.blockData as? Directional ?: return
        val face = blockData.facing
        val targetBlock = block.getRelative(face)
        if (!targetBlock.type.isAir) return
        targetBlock.type = item.type
        (block.state as? Dispenser)?.inventory?.consume(item, 1)
    }
}