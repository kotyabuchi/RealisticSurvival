package com.github.kotyabuchi.RealisticSurvival.Event

import com.github.kotyabuchi.RealisticSurvival.Main
import com.github.kotyabuchi.RealisticSurvival.Utility.miningWithEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object CustomEventCaller: Listener, KoinComponent {
    private val main: Main by inject()

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
}