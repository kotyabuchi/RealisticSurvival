package com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.Job

import com.github.kotyabuchi.RealisticSurvival.Menu.JobMenu
import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.ButtonItem
import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.MenuButton
import com.github.kotyabuchi.RealisticSurvival.System.Player.getStatus
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

object JobButton: MenuButton() {

    init {
        menuIcon = ButtonItem(Material.DIAMOND_PICKAXE, Component.text("JobInfo"))
    }

    override fun clickEvent(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        player.getStatus().openMenu(JobMenu(player))
    }
}