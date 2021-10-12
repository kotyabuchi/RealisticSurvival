package com.github.kotyabuchi.RealisticSurvival.Event

import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

class PlayerInteractBlockEvent(player: Player, action: Action, itemStack: ItemStack?, private val block: Block, blockFace: BlockFace, private val equipmentSlot: EquipmentSlot): PlayerInteractEvent(player, action, itemStack, block, blockFace, equipmentSlot) {
    override fun getClickedBlock(): Block {
        return block
    }

    override fun getHand(): EquipmentSlot {
        return equipmentSlot
    }
}