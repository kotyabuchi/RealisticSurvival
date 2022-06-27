package com.github.kotyabuchi.RealisticSurvival.Menu.Home.Button

import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.ButtonItem
import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.MenuButton
import com.github.kotyabuchi.RealisticSurvival.System.Player.Home
import com.github.kotyabuchi.RealisticSurvival.System.Player.HomePoint
import com.github.kotyabuchi.RealisticSurvival.System.Player.getStatus
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

class RenameHomeButton(private val home: Home): MenuButton() {

    init {
        menuIcon = ButtonItem(Material.NAME_TAG, Component.text("Rename"))
    }

    override fun leftClickEvent(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        player.getStatus().closeMenu()
        HomePoint.openRenameHomeUI(player, home)
    }
}