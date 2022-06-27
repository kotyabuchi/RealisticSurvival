package com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton

import org.bukkit.Sound
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

open class MenuButton {
    lateinit var menuIcon: ItemStack
    var clickSound: Sound? = Sound.UI_BUTTON_CLICK

    open fun leftClickEvent(event: InventoryClickEvent) {}
    open fun rightClickEvent(event: InventoryClickEvent) {}
}