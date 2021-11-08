package com.github.kotyabuchi.RealisticSurvival.System.Item

import com.github.kotyabuchi.RealisticSurvival.Item.ItemExtension
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.PrepareItemCraftEvent
import org.bukkit.event.player.PlayerItemDamageEvent
import org.bukkit.event.player.PlayerItemMendEvent
import org.bukkit.inventory.meta.Damageable

object ItemExtensionManager: Listener {

    @EventHandler
    fun onDamage(event: PlayerItemDamageEvent) {
        if (event.isCancelled) return
        event.isCancelled = true
        ItemExtension(event.item).damage(event.damage, event.player).applyDurability().applySetting()
    }

    @EventHandler
    fun onCraft(event: PrepareItemCraftEvent) {
        val recipe = event.recipe ?: return
        val result = recipe.result
        if (result.itemMeta !is Damageable) return
        val inv = event.inventory
        inv.result = ItemExtension(result).applySetting().itemStack
    }

    @EventHandler
    fun onMendItem(event: PlayerItemMendEvent) {
        ItemExtension(event.item).mending(event.repairAmount, event.player).applyDurability().applySetting()
        event.isCancelled = true
    }
}