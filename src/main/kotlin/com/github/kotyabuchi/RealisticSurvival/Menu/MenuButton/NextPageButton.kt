package com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton

import com.github.kotyabuchi.RealisticSurvival.System.Player.getStatus
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

class NextPageButton(page: Int, allPage: Int): MenuButton() {

    init {
        menuIcon = ButtonItem(Material.ARROW, Component.text("Next page $page / $allPage"))
    }

    override fun clickEvent(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        player.getStatus().nextPage()
    }
}