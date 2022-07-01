package com.github.kotyabuchi.RealisticSurvival.System.Item

import com.github.kotyabuchi.RealisticSurvival.Event.CustomEventCaller
import com.github.kotyabuchi.RealisticSurvival.Event.PrepareItemExtensionCraftEvent
import com.github.kotyabuchi.RealisticSurvival.Item.ItemExtension
import com.github.kotyabuchi.RealisticSurvival.Utility.hasDurability
import com.github.kotyabuchi.RealisticSurvival.Utility.isArmors
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.enchantment.EnchantItemEvent
import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.event.inventory.PrepareItemCraftEvent
import org.bukkit.event.inventory.PrepareSmithingEvent
import org.bukkit.event.player.PlayerItemBreakEvent
import org.bukkit.event.player.PlayerItemDamageEvent
import org.bukkit.event.player.PlayerItemMendEvent
import org.bukkit.inventory.AnvilInventory
import org.bukkit.inventory.ItemStack
import kotlin.math.round

object ItemExtensionManager: Listener {

    @EventHandler
    fun onDamage(event: PlayerItemDamageEvent) {
        if (event.isCancelled) return
        event.isCancelled = true
        val itemStack = event.item
        val itemExtension = ItemExtension(itemStack)
        val beforeDurability = itemExtension.durability
        val damage = event.damage

        if (itemStack.type.isArmors()) {
            itemExtension.damage(damage)
        } else {
            itemExtension.damage(damage, event.player)
        }
        itemExtension.applyDurability().applySetting()

        val player = event.player
        if (itemExtension.durability == 1 && itemStack.type == Material.ELYTRA) {
            player.world.playSound(player.location, Sound.ENTITY_ITEM_BREAK, 1f, 1f)
            return
        }
        if (itemExtension.durability > 0) return
        if (beforeDurability > 1 && damage > 1) {
            itemExtension.mending(1, player).applyDurability().applySetting()
            return
        }

        val itemBreakEvent = PlayerItemBreakEvent(player, itemStack)
        CustomEventCaller.callEvent(itemBreakEvent)
        player.world.playSound(player.location, Sound.ENTITY_ITEM_BREAK, 1f, 1f)
        itemStack.subtract(Int.MAX_VALUE)
    }

    @EventHandler
    fun onCraft(event: PrepareItemCraftEvent) {
        if (event is PrepareItemExtensionCraftEvent) return
        val inv = event.inventory
        val result = if (event.isRepair) {
            inv.result
        } else {
            val recipe = event.recipe ?: return
            recipe.result
        } ?: return
        if (!result.type.hasDurability()) return
        val extensionItemResult = ItemExtension(result).applySetting()
        val prepareItemExtensionCraftEvent = PrepareItemExtensionCraftEvent(extensionItemResult, inv, event.view, event.isRepair)
        CustomEventCaller.callEvent(prepareItemExtensionCraftEvent)
        inv.result = prepareItemExtensionCraftEvent.result.itemStack
    }

    @EventHandler
    fun onCombined(event: PrepareAnvilEvent) {
        val result = event.result ?: return
        if (!result.type.hasDurability()) return
        val inv = event.inventory as? AnvilInventory ?: return
        val firstItem = inv.firstItem ?: return
        val secondItem = inv.secondItem ?: return
        if (!firstItem.containsEnchantment(Enchantment.DURABILITY) && result.containsEnchantment(Enchantment.DURABILITY)) {
            applyUnbreaking(result)
        }
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

    @EventHandler(priority = EventPriority.HIGH)
    fun onMendItem(event: PlayerItemMendEvent) {
        ItemExtension(event.item).mending(event.repairAmount, event.player).applyDurability().applySetting()
        event.isCancelled = true
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onEnchant(event: EnchantItemEvent) {
        val level = event.enchantsToAdd[Enchantment.DURABILITY] ?: return
        applyUnbreaking(event.item, level)
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onSmithing(event: PrepareSmithingEvent) {
        val result = event.result ?: return
        ItemExtension(result).durabilityReset().applyDurability().applySetting()
        event.result = applyUnbreaking(result)
    }

    private fun applyUnbreaking(item: ItemStack, _level: Int? = null): ItemStack {
        val material = item.type
        if (!material.hasDurability()) return item
        val level = _level ?: item.getEnchantmentLevel(Enchantment.DURABILITY)
        if (level > 0) {
            val multiple = if (material.isArmors()) {
                1.25 + (.09 * level)
            } else {
                1.0 + level
            }
            val itemExtension = ItemExtension(item)
            val maxDurability = itemExtension.maxDurability
            val durabilityRatio = itemExtension.durability / maxDurability.toDouble()
            itemExtension.maxDurability(round(maxDurability * multiple).toInt())
            itemExtension.durability(round(itemExtension.maxDurability * durabilityRatio).toInt()).applyDurability().applySetting()
        }
        return item
    }
}