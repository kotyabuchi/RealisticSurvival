package com.github.kotyabuchi.RealisticSurvival.System.Item

import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent
import com.github.kotyabuchi.RealisticSurvival.Event.CustomEventCaller
import com.github.kotyabuchi.RealisticSurvival.Item.ItemExtension
import com.github.kotyabuchi.RealisticSurvival.Utility.hasDurability
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerItemMendEvent
import kotlin.math.min

object GlobalMending: Listener {

    @EventHandler
    fun onPickupOrb(event: PlayerPickupExperienceEvent) {
        val player = event.player
        val orb = event.experienceOrb

        for (itemStack in player.inventory.contents) {
            if (itemStack == null) continue
            if (!itemStack.type.hasDurability()) continue
            if (!itemStack.itemMeta.hasEnchant(Enchantment.MENDING)) return
            val itemExtension = ItemExtension(itemStack)
            val repairAmount = min(itemExtension.maxDurability - itemExtension.durability, orb.experience)
            val mendEvent = PlayerItemMendEvent(player, itemStack, orb, repairAmount)
            CustomEventCaller.callEvent(mendEvent)
            if (!mendEvent.isCancelled) {
                orb.experience -= repairAmount
                if (orb.experience <= 0) {
                    event.isCancelled = true
                    break
                }
            }
        }
    }
}