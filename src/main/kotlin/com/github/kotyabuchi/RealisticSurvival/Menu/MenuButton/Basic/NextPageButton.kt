package com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.Basic

import com.github.kotyabuchi.RealisticSurvival.CustomModelData
import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.ButtonItem
import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.MenuButton
import com.github.kotyabuchi.RealisticSurvival.System.Player.getStatus
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

class NextPageButton(private val page: Int, private val totalPage: Int): MenuButton() {

    init {
        menuIcon = ButtonItem(Material.ARROW, Component.text("Next page $page / $totalPage"), modelData = CustomModelData.ARROW_RIGHT)
    }

    override fun clickEvent(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        val status = player.getStatus()
        status.getOpeningMenu()?.let { menu ->
            menu.changePageEvent(page - 1, totalPage, true, player)
            status.nextPage()
        }
    }
}