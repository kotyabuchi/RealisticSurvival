package com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.Home

import com.github.kotyabuchi.RealisticSurvival.Main
import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.ButtonItem
import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.MenuButton
import com.github.kotyabuchi.RealisticSurvival.Utility.DataBaseManager
import com.github.kotyabuchi.RealisticSurvival.Utility.normalize
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.wesjd.anvilgui.AnvilGUI
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AddHomeButton: MenuButton(), KoinComponent {

    private val main: Main by inject()

    init {
        menuIcon = ButtonItem(Material.ENDER_EYE, Component.text("Add Home").normalize(NamedTextColor.GREEN))
    }

    override fun clickEvent(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        var addedHome = false
        val homeItem = ItemStack(Material.NAME_TAG)
        AnvilGUI.Builder()
            .itemLeft(homeItem)
            .text(" ")
            .title("Add Home")
            .plugin(main)
            .onClose {
                if (addedHome) {
                    player.sendMessage(Component.text("ホームポイントを追加しました", NamedTextColor.GREEN))
                } else {
                    player.sendMessage(Component.text("ホームポイントの追加をキャンセルしました", NamedTextColor.RED))
                }
            }
            .onComplete { _, text ->
                val homeName = text.trim()
                if (homeName != "") {
                    DataBaseManager.addHome(player, homeName, player.location)
                    addedHome = true
                }
                AnvilGUI.Response.close()
            }
            .open(player)
    }
}