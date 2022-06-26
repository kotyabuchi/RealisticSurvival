package com.github.kotyabuchi.RealisticSurvival.Menu.Home

import com.github.kotyabuchi.RealisticSurvival.CustomModelData
import com.github.kotyabuchi.RealisticSurvival.Menu.Menu
import com.github.kotyabuchi.RealisticSurvival.Menu.Home.Button.CreateHomeButton
import com.github.kotyabuchi.RealisticSurvival.Menu.Home.Button.HomeInfoButton
import com.github.kotyabuchi.RealisticSurvival.System.Player.Home
import com.github.kotyabuchi.RealisticSurvival.System.Player.getStatus
import com.github.kotyabuchi.RealisticSurvival.System.TombStone
import com.github.kotyabuchi.RealisticSurvival.Utility.toInt
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.persistence.PersistentDataType

class HomeMenu(val player: Player, hasBed: Boolean, private val hasLastDeath: Boolean): Menu(Component.text("${player.name}'s Homes"), 1 + hasBed.toInt() + hasLastDeath.toInt() + player.getStatus().homes.size,
    FrameType.TOP,
    FrameType.SIDE
) {

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
        val pdc = player.persistentDataContainer
        if (hasLastDeath) {
            pdc.get(TombStone.lastDeathPointKey, PersistentDataType.STRING)?.split(",")?.let { lastDeathStr ->
                val lastDeathWorld = Bukkit.getWorld(lastDeathStr[0])
                val lastDeathX = lastDeathStr[1].toDouble()
                val lastDeathY = lastDeathStr[2].toDouble()
                val lastDeathZ = lastDeathStr[3].toDouble()
                val lastDeathLoc = Location(lastDeathWorld, lastDeathX, lastDeathY, lastDeathZ)
                setMenuButton(HomeInfoButton(player, Home("Last Death", lastDeathLoc, TombStone.tombStoneItem.type), CustomModelData.TOMB_STONE))
            }
        }
        player.getStatus().homes.forEach {
            if (getLastBlankSlot(page) == null) page++
            setMenuButton(HomeInfoButton(player, it), page)
        }
        repeat(page + 1) {
            setMenuButton(CreateHomeButton(), it, menuSize - 5)
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
                home.homeId?.let { status.openMenu(HomeSettingMenu(home, it)) }
//                val homeId = home.homeId ?: return
//                DataBaseManager.removeHome(homeId)
//                status.homes.remove(home)
//                refresh()
//                status.openMenu(this, 0, true)
            }
        } else {
            super.doButtonClickEvent(slot, event, page)
        }
    }
}