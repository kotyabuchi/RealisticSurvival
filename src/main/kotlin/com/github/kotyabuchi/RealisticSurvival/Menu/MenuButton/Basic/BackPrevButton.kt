package com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.Basic

import com.github.kotyabuchi.RealisticSurvival.Menu.Menu
import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.ButtonItem
import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.MenuButton
import com.github.kotyabuchi.RealisticSurvival.System.Player.getStatus
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

class BackPrevButton(private val prevMenu: Menu): MenuButton() {

    init {
        menuIcon = ButtonItem(Material.ARROW, Component.text("Back to ").append(prevMenu.title))
    }

    override fun clickEvent(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        player.getStatus().openMenu(prevMenu, prev = true)
    }
}