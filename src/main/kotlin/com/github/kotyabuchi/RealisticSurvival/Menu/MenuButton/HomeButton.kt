package com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton

import com.github.kotyabuchi.RealisticSurvival.Menu.HomeMenu
import com.github.kotyabuchi.RealisticSurvival.System.Player.getStatus
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

class HomeButton: MenuButton() {

    init {
        menuIcon = ButtonItem(Material.ENDER_PEARL, Component.text("Homes"))
    }

    override fun clickEvent(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        player.getStatus().openMenu(HomeMenu(player))
    }
}