package com.github.kotyabuchi.RealisticSurvival.Menu.Home

import com.github.kotyabuchi.RealisticSurvival.CustomModelData
import com.github.kotyabuchi.RealisticSurvival.Menu.Home.Button.CreateHomeButton
import com.github.kotyabuchi.RealisticSurvival.Menu.Home.Button.HomeInfoButton
import com.github.kotyabuchi.RealisticSurvival.Menu.Menu
import com.github.kotyabuchi.RealisticSurvival.System.Player.Home
import com.github.kotyabuchi.RealisticSurvival.System.Player.getStatus
import com.github.kotyabuchi.RealisticSurvival.System.TombStone
import com.github.kotyabuchi.RealisticSurvival.Utility.toInt
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import java.util.*

class HomeMenu(val player: Player, hasBed: Boolean, private val hasTombStone: Boolean): Menu(Component.text("${player.name}'s Homes"), 1 + hasBed.toInt() + hasTombStone.toInt() + player.getStatus().homes.size,
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
        if (hasTombStone) {
            val tombStones = TombStone.getPlayerTombStones(player)
            tombStones?.let {
                try {
                    val recentTombStone = tombStones.get(tombStones.names().last()).asObject()
                    val jsonLocation = recentTombStone.get("Location")?.asObject()
                    jsonLocation?.let {
                        val world = Bukkit.getWorld(UUID.fromString(jsonLocation.getString("World", "")))
                        val x = jsonLocation.getDouble("X", 0.0)
                        val y = jsonLocation.getDouble("Y", 0.0)
                        val z = jsonLocation.getDouble("Z", 0.0)
                        val tombStoneLocation = Location(world, x, y, z)
                        setMenuButton(HomeInfoButton(player, Home("Recent TombStone", tombStoneLocation, TombStone.tombStoneItem.type), CustomModelData.TOMB_STONE))
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
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
}