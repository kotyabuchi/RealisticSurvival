package com.github.kotyabuchi.RealisticSurvival.Menu.Home.Button

import com.github.kotyabuchi.RealisticSurvival.Menu.Home.HomeMenu
import com.github.kotyabuchi.RealisticSurvival.Menu.Home.PublicHomeMenu
import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.ButtonData
import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.ButtonItem
import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.MenuButton
import com.github.kotyabuchi.RealisticSurvival.System.Player.getStatus
import com.github.kotyabuchi.RealisticSurvival.System.TombStone
import com.github.kotyabuchi.RealisticSurvival.Utility.normalize
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

object HomeButton: MenuButton() {

    init {
        val lore = mutableListOf<Component>()
        lore.add(Component.text("Left Click: ", ButtonData.buttonLoreStyle).append(Component.text("Show private homes").normalize()))
        lore.add(Component.text("Right Click: ", ButtonData.buttonLoreStyle).append(Component.text("Show public homes").normalize()))
        menuIcon = ButtonItem(Material.ENDER_PEARL, Component.text("Homes"), lore = lore)
    }

    override fun leftClickEvent(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        val hasBed = player.bedSpawnLocation != null
        val hasTombStone = TombStone.hasTombStone(player)
        player.getStatus().openMenu(HomeMenu(player, hasBed, hasTombStone))
    }

    override fun rightClickEvent(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        player.getStatus().openMenu(PublicHomeMenu(player))
    }
}