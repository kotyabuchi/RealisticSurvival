package com.github.kotyabuchi.RealisticSurvival.System.Item

import com.github.kotyabuchi.RealisticSurvival.Event.CustomEventCaller
import com.github.kotyabuchi.RealisticSurvival.Item.ItemExtension
import com.github.kotyabuchi.RealisticSurvival.Utility.findFirst
import com.github.kotyabuchi.RealisticSurvival.Utility.hasDurability
import com.github.kotyabuchi.RealisticSurvival.Utility.isArmors
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.event.inventory.PrepareItemCraftEvent
import org.bukkit.event.player.PlayerItemBreakEvent
import org.bukkit.event.player.PlayerItemDamageEvent
import org.bukkit.event.player.PlayerItemMendEvent
import org.bukkit.inventory.AnvilInventory
import kotlin.math.round

object ItemExtensionManager: Listener {

    @EventHandler
    fun onDamage(event: PlayerItemDamageEvent) {
        if (event.isCancelled) return
        event.isCancelled = true
        val itemStack = event.item
        val itemExtension = ItemExtension(itemStack)
        if (itemStack.type.isArmors()) {
            itemExtension.damage(event.damage)
        } else {
            itemExtension.damage(event.damage, event.player)
        }
        itemExtension.applyDurability().applySetting()

        if (itemExtension.durability > 0) return
        val player = event.player
        val itemBreakEvent = PlayerItemBreakEvent(player, itemStack)
        CustomEventCaller.callEvent(itemBreakEvent)
        player.world.playSound(player.location, Sound.ENTITY_ITEM_BREAK, 1f, 1f)
        val inv = player.inventory
        inv.findFirst(itemStack)?.slot?.let { slot ->
            inv.setItem(slot, null)
        }
    }

    @EventHandler
    fun onCraft(event: PrepareItemCraftEvent) {
        val inv = event.inventory
        val result = if (event.isRepair) {
            inv.result
        } else {
            val recipe = event.recipe ?: return
            recipe.result
        } ?: return
        if (!result.type.hasDurability()) return
        inv.result = ItemExtension(result).applySetting().itemStack
    }

    @EventHandler
    fun onCombined(event: PrepareAnvilEvent) {
        val result = event.result ?: return
        if (!result.type.hasDurability()) return
        val inv = event.inventory as? AnvilInventory ?: return
        val firstItem = inv.firstItem ?: return
        val secondItem = inv.secondItem ?: return
        val mendAmount = when {
            firstItem.type == secondItem.type -> {
                ItemExtension(secondItem).durability
            }
            secondItem.canRepair(firstItem) -> {
                val vanillaDurability = firstItem.type.maxDurability
                val mendPerItem = vanillaDurability * .25
                round(secondItem.amount * mendPerItem).toInt()
            }
            else -> return
        }
        ItemExtension(result).mending(mendAmount).applyDurability().applySetting()
    }

    @EventHandler
    fun onMendItem(event: PlayerItemMendEvent) {
        ItemExtension(event.item).mending(event.repairAmount, event.player).applyDurability().applySetting()
        event.isCancelled = true
    }
}