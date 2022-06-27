package com.github.kotyabuchi.RealisticSurvival.Menu.Home.Button

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

class ChangePublicButton(private val home: Home, private val menu: Menu): MenuButton() {

    init {
        menuIcon = ButtonItem(Material.FILLED_MAP, Component.text("Change to " + (if (home.isPublic) "private" else "public")))
    }

    override fun leftClickEvent(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        HomePoint.changeVisibility(player, home, !home.isPublic)
        menu.refresh()
        player.getStatus().openMenu(menu, 0, true)
    }
}