package com.github.kotyabuchi.RealisticSurvival.Menu

import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.Home.AddHomeButton
import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.Home.HomeInfoButton
import com.github.kotyabuchi.RealisticSurvival.System.Player.Home
import com.github.kotyabuchi.RealisticSurvival.System.Player.getStatus
import com.github.kotyabuchi.RealisticSurvival.Utility.DataBaseManager
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

class HomeMenu(val player: Player): Menu(Component.text("${player.name}'s Homes"), player.getStatus().homes.size, FrameType.TOP, FrameType.SIDE) {

    init {
        createMenu()
    }

    override fun createMenu() {
        var page = 0
        player.world.spawnLocation.let { loc ->
            loc.yaw = 0f
            setMenuButton(HomeInfoButton(player, Home("World Spawn",loc, Material.GRASS_BLOCK)), 0)
        }
        player.bedSpawnLocation?.let { loc ->
            loc.yaw = 0f
            setMenuButton(HomeInfoButton(player, Home("Bed", loc, Material.RED_BED)), 0)
        }
        player.getStatus().homes.forEach {
            if (getLastBlankSlot(page) == null) page++
            setMenuButton(HomeInfoButton(player, it), page)
        }
        repeat(page + 1) {
            setMenuButton(AddHomeButton(), it, menuSize - 5)
        }
    }

    override fun doButtonClickEvent(slot: Int, event: InventoryClickEvent, page: Int) {
        createPageIfNeed(page)
        val button = getButton(slot, page)
        if (button is HomeInfoButton) {
            val player = event.whoClicked as? Player ?: return
            playClickedButtonSound(button, player)
            val status = player.getStatus()
            val home = button.home
            if (event.isLeftClick) {
                getButton(slot, page)?.clickEvent(event)
            } else if (event.isRightClick) {
                val homeId = home.homeId ?: return
                DataBaseManager.removeHome(homeId)
                status.homes.remove(home)
                refresh()
                status.openMenu(this, 0, true)
            }
        } else {
            super.doButtonClickEvent(slot, event, page)
        }
    }
}