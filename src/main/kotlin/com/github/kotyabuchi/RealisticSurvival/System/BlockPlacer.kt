package com.github.kotyabuchi.RealisticSurvival.System

import io.papermc.paper.event.block.BlockPreDispenseEvent
import org.bukkit.Material
import org.bukkit.block.Dispenser
import org.bukkit.block.data.Directional
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockDispenseEvent
import org.bukkit.inventory.ItemStack

object BlockPlacer: Listener {

    @EventHandler
    fun onLaunch(event: BlockPreDispenseEvent) {
        val block = event.block
        val item = event.itemStack
        if (!item.type.isBlock) return
        val blockData = block.blockData as? Directional ?: return
        val face = blockData.facing
        val targetBlock = block.getRelative(face)
        event.isCancelled = true
        if (!targetBlock.type.isAir) return
        val dispenser = block.state as? Dispenser ?: return
        targetBlock.type = item.type
        block.world.playSound(block.location, item.type.createBlockData().soundGroup.placeSound, 1f, 1f)
        dispenser.inventory.getItem(event.slot)?.subtract(1)
    }
}