package com.github.kotyabuchi.RealisticSurvival.System

import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.ItemDespawnEvent

object ReplantSapling: Listener {

    private val dirt = setOf(Material.GRASS_BLOCK, Material.DIRT, Material.COARSE_DIRT, Material.PODZOL, Material.ROOTED_DIRT)

    @EventHandler
    fun onReplant(event: ItemDespawnEvent) {
        val item = event.entity
        val itemStack = item.itemStack
        val itemName = itemStack.type.name
        if (itemName.startsWith("POTTED_")) return
        if (!itemName.endsWith("_SAPLING")) return
        val block = event.location.block
        if (!block.type.isAir) return
        if (!dirt.contains(block.getRelative(BlockFace.DOWN).type)) return
        block.type = itemStack.type
    }
}