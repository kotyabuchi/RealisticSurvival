package com.github.kotyabuchi.RealisticSurvival.System.Player

import com.github.kotyabuchi.RealisticSurvival.Utility.*
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.SQLException

object HomePoint: Listener {

    private val homeNameTypingPlayers = mutableSetOf<Player>()

    @EventHandler
    fun onType(event: AsyncChatEvent) {
        val player = event.player
        if (!homeNameTypingPlayers.contains(player)) return
        homeNameTypingPlayers.remove(player)
        val homeName = PlainTextComponentSerializer.plainText().serialize(event.originalMessage())
        addHome(player, homeName, player.location)
        player.sendSuccessMessage("ホームポイント[${homeName}]を追加しました。")
        event.isCancelled = true
    }

    @EventHandler
    fun onMove(event: PlayerMoveEvent) {
        val player = event.player
        if (!event.hasExplicitlyChangedPosition()) return
        if (!homeNameTypingPlayers.contains(player)) return
        homeNameTypingPlayers.remove(player)

        player.sendErrorMessage("ホームポイントの追加をキャンセルしました。")
        player.playErrorSound()
    }

    fun addHomeNameTypingPlayer(player: Player) {
        homeNameTypingPlayers.add(player)
        player.sendMessage(Component.text("ホームポイントの名前をチャットで入力してください。"))
    }

    private fun addHome(player: Player, homeName: String, location: Location): Int? {
        var pstmt: PreparedStatement

        val block = location.block.location.add(.5, .0, .5)
        val world = location.world ?: return null
        val worldName = world.name
        val x = block.x
        val y = location.y
        val z = block.z
        val yaw = location.yaw
        try {
            DriverManager.getConnection(DataBaseManager.dbHeader).use { conn ->
                pstmt = conn.prepareStatement("INSERT INTO homes(uuid, home_name, world, x, y, z, yaw, icon) VALUES (?,?,?,?,?,?,?,?)")
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
                    player.getStatus().homes.add(Home(homeId, homeName, world, x, y, z, yaw, Material.ENDER_PEARL))
                    return homeId
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return null
    }
}