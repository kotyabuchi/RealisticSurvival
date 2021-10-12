package com.github.kotyabuchi.RealisticSurvival.System

import com.github.kotyabuchi.RealisticSurvival.Event.PlayerInteractBlockEvent
import com.github.kotyabuchi.RealisticSurvival.Main
import com.github.kotyabuchi.RealisticSurvival.Utility.isShovel
import net.kyori.adventure.sound.Sound
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.scheduler.BukkitRunnable
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object LevelTheFarmlandAnPath: Listener, KoinComponent {

    private val main: Main by inject()

    @EventHandler
    fun onClick(event: PlayerInteractBlockEvent) {
        val player = event.player
        val item = event.item ?: return
        val block = event.clickedBlock
        val type = block.type

        if (!player.isSneaking) return
        if (!item.type.isShovel()) return
        if (type != Material.FARMLAND && type != Material.DIRT_PATH) return
        object : BukkitRunnable() {
            override fun run() {
                if (event.hand == EquipmentSlot.HAND) {
                    player.swingMainHand()
                } else {
                    player.swingOffHand()
                }
                block.type = Material.DIRT
                block.world.playSound(Sound.sound(org.bukkit.Sound.ITEM_HOE_TILL.key, Sound.Source.BLOCK, 1f, 1f))
            }
        }.runTaskLater(main, 1)
    }
}