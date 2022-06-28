package com.github.kotyabuchi.RealisticSurvival.System.Player

import com.github.kotyabuchi.RealisticSurvival.Main
import com.github.kotyabuchi.RealisticSurvival.Utility.DataBaseManager
import com.github.kotyabuchi.RealisticSurvival.Utility.sendErrorMessage
import com.github.kotyabuchi.RealisticSurvival.Utility.sendSuccessMessage
import com.github.kotyabuchi.RealisticSurvival.Utility.toInt
import net.wesjd.anvilgui.AnvilGUI
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.sql.DriverManager
import java.sql.SQLException
import java.util.*

object HomePoint: Listener, KoinComponent {

    private val main: Main by inject()
    private val publicHomes = mutableSetOf<Home>()

    init {
        loadPublicHome()
    }

    private fun loadPublicHome() {
        try {
            DriverManager.getConnection(DataBaseManager.dbHeader).use { conn ->
                val pstmt = conn.prepareStatement("SELECT * FROM homes WHERE is_public = 1")
                val homeRs = pstmt.executeQuery()

                while (homeRs.next()) {
                    val homeId = homeRs.getInt("home_id")
                    val homeName = homeRs.getString("home_name")
                    val world = main.server.getWorld(homeRs.getString("world"))
                    val x = homeRs.getDouble("x")
                    val y = homeRs.getDouble("y")
                    val z = homeRs.getDouble("z")
                    val yaw = homeRs.getFloat("yaw")
                    val icon = Material.valueOf(homeRs.getString("icon") ?: "ENDER_PEARL")
                    val creator = UUID.fromString(homeRs.getString("uuid"))
                    val isPublic = (homeRs.getInt("is_public") == 1)
                    world?.let {
                        publicHomes.add(Home(homeId, homeName, world, x, y, z, yaw, icon, creator, isPublic))
                    }
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    fun getPublicHomes(): Set<Home> {
        return publicHomes
    }

    fun openCreateHomeUI(player: Player) {
        var createdHome = false
        val homeItem = ItemStack(Material.NAME_TAG)
        AnvilGUI.Builder()
            .itemLeft(homeItem)
            .text(" ")
            .title("Create Home")
            .plugin(main)
            .onClose {
                if (!createdHome) player.sendErrorMessage("ホームポイントの作成をキャンセルしました。")
            }
            .onComplete { _, text ->
                val homeName = text.trim()
                if (homeName != "") {
                    createHome(player, homeName, player.location)
                    createdHome = true
                }
                AnvilGUI.Response.close()
            }
            .open(player)
    }

    private fun createHome(player: Player, homeName: String, location: Location) {
        val block = location.block.location.add(.5, .0, .5)
        val world = location.world ?: return
        val worldName = world.name
        val x = block.x
        val y = location.y
        val z = block.z
        val yaw = location.yaw
        try {
            DriverManager.getConnection(DataBaseManager.dbHeader).use { conn ->
                val pstmt = conn.prepareStatement("INSERT INTO homes(uuid, home_name, world, x, y, z, yaw, icon) VALUES (?,?,?,?,?,?,?,?)")
                pstmt.setString(1, player.uniqueId.toString())
                pstmt.setString(2, homeName)
                pstmt.setString(3, worldName)
                pstmt.setDouble(4, x)
                pstmt.setDouble(5, y)
                pstmt.setDouble(6, z)
                pstmt.setFloat(7, yaw)
                pstmt.setString(8, "ENDER_PEARL")
                pstmt.executeUpdate()

                val rs = conn.createStatement().executeQuery("SELECT LAST_INSERT_ROWID()")
                if (rs.next()) {
                    val homeId = rs.getInt("LAST_INSERT_ROWID()")
                    player.getStatus().homes.add(Home(homeId, homeName, world, x, y, z, yaw, Material.ENDER_PEARL, player.uniqueId))
                }
                player.sendSuccessMessage("ホームポイント[${homeName}]を作成しました。")
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    fun openRenameHomeUI(player: Player, home: Home) {
        var renamed = false
        val homeItem = ItemStack(Material.NAME_TAG)
        AnvilGUI.Builder()
            .itemLeft(homeItem)
            .text(" ")
            .title("Rename Home")
            .plugin(main)
            .onClose {
                if (!renamed) player.sendErrorMessage("ホームポイントのリネームをキャンセルしました。")
            }
            .onComplete { _, text ->
                val homeName = text.trim()
                if (homeName != "") {
                    renameHome(player, home, homeName)
                    renamed = true
                }
                AnvilGUI.Response.close()
            }
            .open(player)
    }

    private fun renameHome(player: Player, home: Home, newName: String) {
        home.homeId?.let { id ->
            try {
                DriverManager.getConnection(DataBaseManager.dbHeader).use { conn ->
                    val pstmt = conn.prepareStatement("UPDATE homes SET home_name = ? WHERE home_id = ?")
                    pstmt.setString(1, newName)
                    pstmt.setInt(2, id)
                    pstmt.executeUpdate()

                    home.name = newName
                    player.sendSuccessMessage("名前を変更しました。")
                }
            } catch (e: SQLException) {
                e.printStackTrace()
            }
        }
    }

    fun changeVisibility(player: Player, home: Home, changeValue: Boolean) {
        home.homeId?.let { id ->
            if (!player.isOp && home.creator != player.uniqueId) {
                player.sendErrorMessage("作成者のみ公開設定を変更できます。")
                return
            }
            try {
                DriverManager.getConnection(DataBaseManager.dbHeader).use { conn ->
                    val pstmt = conn.prepareStatement("UPDATE homes SET is_public = ? WHERE home_id = ?")
                    pstmt.setInt(1, changeValue.toInt())
                    pstmt.setInt(2, id)
                    pstmt.executeUpdate()

                    home.isPublic = changeValue
                    if (changeValue) publicHomes.add(home) else publicHomes.remove(home)
                    player.sendSuccessMessage("[${home.name}]を" + (if (changeValue) "公開" else "非公開") + "に変更しました。")
                }
            } catch (e: SQLException) {
                e.printStackTrace()
            }
        }
    }

    fun changeIcon(home: Home, newIcon: Material) {
        home.homeId?.let { id ->
            try {
                DriverManager.getConnection(DataBaseManager.dbHeader).use { conn ->
                    val pstmt = conn.prepareStatement("UPDATE homes SET icon = ? WHERE home_id = ?")
                    pstmt.setString(1, newIcon.name)
                    pstmt.setInt(2, id)
                    pstmt.executeUpdate()

                    home.changeIcon(newIcon)
                }
            } catch (e: SQLException) {
                e.printStackTrace()
            }
        }
    }

    fun removeHome(player: Player, home: Home) {
        home.homeId?.let { id ->
            try {
                DriverManager.getConnection(DataBaseManager.dbHeader).use { conn ->  //try-with-resources
                    val pstmt = conn.prepareStatement("DELETE FROM homes WHERE home_id = ?")
                    pstmt.setInt(1, id)
                    pstmt.execute()

                    player.getStatus().homes.remove(home)
                    player.sendSuccessMessage("[${home.name}]を削除しました。")
                }
            } catch (e: SQLException) {
                e.printStackTrace()
            }
        }
    }
}