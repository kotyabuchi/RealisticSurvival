package com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.Home

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

class AddHomeButton: MenuButton() {

    init {
        menuIcon = ButtonItem(Material.ENDER_EYE, Component.text("Add Home").normalize(NamedTextColor.GREEN), modelData = CustomModelData.PLUS)
    }

    override fun clickEvent(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        player.getStatus().closeMenu()
        player.closeInventory()
        HomePoint.addHomeNameTypingPlayer(player)
//        var addedHome = false
//        val homeItem = ItemStack(Material.NAME_TAG)
//        AnvilGUI.Builder()
//            .itemLeft(homeItem)
//            .text(" ")
//            .title("Add Home")
//            .plugin(main)
//            .onClose {
//                if (addedHome) {
//                    player.sendMessage(Component.text("ホームポイントを追加しました", NamedTextColor.GREEN))
//                } else {
//                    player.sendMessage(Component.text("ホームポイントの追加をキャンセルしました", NamedTextColor.RED))
//                }
//            }
//            .onComplete { _, text ->
//                val homeName = text.trim()
//                if (homeName != "") {
//                    HomePoint.addHome(player, homeName, player.location)
//                    addedHome = true
//                }
//                AnvilGUI.Response.close()
//            }
//            .open(player)
    }
}

