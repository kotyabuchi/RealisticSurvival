package com.github.kotyabuchi.RealisticSurvival.Menu.Home.Button

import com.github.kotyabuchi.RealisticSurvival.Menu.Home.HomeMenu
import com.github.kotyabuchi.RealisticSurvival.Menu.Menu
import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.ButtonItem
import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.MenuButton
import com.github.kotyabuchi.RealisticSurvival.System.Player.Home
import com.github.kotyabuchi.RealisticSurvival.System.Player.HomePoint
import com.github.kotyabuchi.RealisticSurvival.System.Player.getStatus
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

class RemoveHomeButton(private val home: Home, private val menu: Menu): MenuButton() {

    init {
        menuIcon = ButtonItem(Material.BARRIER, Component.text("Remove"))
    }

    override fun leftClickEvent(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        HomePoint.removeHome(player, home)
        val homeMenu = menu.getPrevMenu() as? HomeMenu ?: return
        homeMenu.refresh()
        player.getStatus().openMenu(homeMenu, 0, true)
    }
}