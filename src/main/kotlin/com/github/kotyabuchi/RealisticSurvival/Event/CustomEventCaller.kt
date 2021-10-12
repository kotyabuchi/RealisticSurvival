package com.github.kotyabuchi.RealisticSurvival.Event

import com.github.kotyabuchi.RealisticSurvival.Main
import com.github.kotyabuchi.RealisticSurvival.Utility.miningWithEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.PluginManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object CustomEventCaller: Listener, KoinComponent {
    private val main: Main by inject()
    private val pm: PluginManager = main.server.pluginManager

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onBlockBreak(event: BlockBreakEvent) {
        if (event is BlockMineEvent) return
        if (event.isCancelled) return
        val player = event.player
        val block = event.block
        val itemStack = player.inventory.itemInMainHand

        event.isCancelled = true

        block.miningWithEvent(main, player, itemStack)
    }

    @EventHandler
    fun onClickBlock(event: PlayerInteractEvent) {
        if (event is PlayerInteractBlockEvent) return
        if (!event.hasBlock()) return
        val block = event.clickedBlock ?: return
        val player = event.player
        val hand = event.hand ?: return
        val itemStack = player.inventory.getItem(hand)
        pm.callEvent(PlayerInteractBlockEvent(event.player, event.action, itemStack, block, event.blockFace, hand))
    }
}