package com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.Basic

import com.github.kotyabuchi.RealisticSurvival.Event.ChangeMenuPageEvent
import com.github.kotyabuchi.RealisticSurvival.Event.CustomEventCaller
import com.github.kotyabuchi.RealisticSurvival.Main
import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.ButtonItem
import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.MenuButton
import com.github.kotyabuchi.RealisticSurvival.System.Player.getStatus
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class BackPageButton(private val page: Int, private val totalPage: Int): MenuButton() {

    init {
        menuIcon = ButtonItem(Material.ARROW, Component.text("Back page $page / $totalPage"))
    }

    override fun clickEvent(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        val status = player.getStatus()
        status.getOpeningMenu()?.let { menu ->
            val changeMenuPageEvent = ChangeMenuPageEvent(menu, page, totalPage, player)
            CustomEventCaller.callEvent(changeMenuPageEvent)
            status.backPage()
        }
    }
}