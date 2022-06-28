package com.github.kotyabuchi.RealisticSurvival.Event

import com.github.kotyabuchi.RealisticSurvival.Item.ItemExtension
import org.bukkit.event.inventory.PrepareItemCraftEvent
import org.bukkit.inventory.CraftingInventory
import org.bukkit.inventory.InventoryView

class PrepareItemExtensionCraftEvent(val result: ItemExtension, what: CraftingInventory, view: InventoryView, isRepair: Boolean): PrepareItemCraftEvent(what, view, isRepair) {
}