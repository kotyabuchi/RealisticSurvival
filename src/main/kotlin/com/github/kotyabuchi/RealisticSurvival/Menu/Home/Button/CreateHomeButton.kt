package com.github.kotyabuchi.RealisticSurvival.Menu.Home.Button

import com.github.kotyabuchi.RealisticSurvival.CustomModelData
import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.ButtonItem
import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.MenuButton
import com.github.kotyabuchi.RealisticSurvival.System.Player.HomePoint
import com.github.kotyabuchi.RealisticSurvival.System.Player.getStatus
import com.github.kotyabuchi.RealisticSurvival.Utility.normalize
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

class CreateHomeButton: MenuButton() {

    init {
        menuIcon = ButtonItem(Material.ENDER_EYE, Component.text("Create Home").normalize(NamedTextColor.GREEN), modelData = CustomModelData.PLUS)
    }

    override fun leftClickEvent(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        player.getStatus().closeMenu()
        HomePoint.openCreateHomeUI(player)
    }
}

