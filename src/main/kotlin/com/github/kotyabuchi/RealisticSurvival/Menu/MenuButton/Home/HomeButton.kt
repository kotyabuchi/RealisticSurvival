package com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.Home

import com.github.kotyabuchi.RealisticSurvival.Menu.HomeMenu
import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.ButtonItem
import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.MenuButton
import com.github.kotyabuchi.RealisticSurvival.System.Player.getStatus
import com.github.kotyabuchi.RealisticSurvival.System.TombStone
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.persistence.PersistentDataType

object HomeButton: MenuButton() {

    init {
        menuIcon = ButtonItem(Material.ENDER_PEARL, Component.text("Homes"))
    }

    override fun clickEvent(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        val hasBed = player.bedSpawnLocation != null
        val hasLastDeath = player.persistentDataContainer.has(TombStone.lastDeathPointKey, PersistentDataType.STRING)
        player.getStatus().openMenu(HomeMenu(player, hasBed, hasLastDeath))
    }
}