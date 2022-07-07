package com.github.kotyabuchi.RealisticSurvival.Menu.ResourceStorage.Button

import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.ButtonItem
import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.MenuButton
import com.github.kotyabuchi.RealisticSurvival.Menu.ResourceStorage.ResourceStorageMenu
import com.github.kotyabuchi.RealisticSurvival.System.Player.getStatus
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

object ResourceStorageButton: MenuButton() {

    init {
        menuIcon = ButtonItem(Material.CHEST, Component.text("Resource Storage"))
    }

    override fun leftClickEvent(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        player.getStatus().openMenu(ResourceStorageMenu(player.getStatus().resourceStorage))
    }
}