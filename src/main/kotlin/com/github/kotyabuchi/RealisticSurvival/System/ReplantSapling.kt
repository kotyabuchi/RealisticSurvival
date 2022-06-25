package com.github.kotyabuchi.RealisticSurvival.System

import com.github.kotyabuchi.RealisticSurvival.Utility.isDirt
import org.bukkit.block.BlockFace
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.ItemDespawnEvent
import org.bukkit.event.entity.ItemMergeEvent

object ReplantSapling: Listener {


    @EventHandler
    fun onReplant(event: ItemDespawnEvent) {
        val item = event.entity
        val itemStack = item.itemStack.clone()
        val itemName = itemStack.type.name
        if (itemName.startsWith("POTTED_")) return
        if (!itemName.endsWith("_SAPLING")) return
        val loc = event.location
        val block = loc.block
        if (!block.type.isAir) return
        if (!block.getRelative(BlockFace.DOWN).type.isDirt()) return
        block.type = itemStack.type
    }

    @EventHandler
    fun onMergeItem(event: ItemMergeEvent) {
        val item = event.entity
        val itemName = item.itemStack.type.name
        if (itemName.startsWith("POTTED_")) return
        if (!itemName.endsWith("_SAPLING")) return
        event.isCancelled = true
    }
}