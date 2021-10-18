package com.github.kotyabuchi.RealisticSurvival.System.Player

import com.github.kotyabuchi.RealisticSurvival.Job.JobMaster
import com.github.kotyabuchi.RealisticSurvival.Main
import com.github.kotyabuchi.RealisticSurvival.Utility.DataBaseManager
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

private val playerStatusMap = mutableMapOf<UUID, PlayerStatus>()

fun Player.getStatus(): PlayerStatus {
    val uuid = this.uniqueId
    if (!playerStatusMap.containsKey(uuid)) playerStatusMap[uuid] = PlayerStatus(this)
    return playerStatusMap[uuid]!!
}

fun Player.getJobLevel(job: JobMaster): Int {
    return this.getStatus().getJobStatus(job).getLevel()
}

object PlayerManager: Listener, KoinComponent {

    private val main: Main by inject()

    init {
        val status = DataBaseManager.loadPlayerStatus(*main.server.onlinePlayers.toTypedArray())

        status.forEach {
            playerStatusMap[it.player.uniqueId] = it
        }
    }

    fun getAllPlayerStatus(): Collection<PlayerStatus> {
        val removeList = mutableListOf<UUID>()
        val result = mutableListOf<PlayerStatus>()
        val server = main.server
        playerStatusMap.forEach { (t, u) ->
            if (server.getPlayer(t)?.isOnline == true) {
                result.add(u)
            } else {
                removeList.add(t)
            }
        }
        removeList.forEach {
            playerStatusMap.remove(it)
        }
        return result
    }

    @EventHandler
    fun onJoinServer(event: PlayerJoinEvent) {
        val player = event.player
        playerStatusMap[player.uniqueId] = DataBaseManager.loadPlayerStatus(player).first()
    }

    @EventHandler
    fun onQuitServer(event: PlayerQuitEvent) {
        val player = event.player
        val uuid = player.uniqueId
        playerStatusMap[uuid]?.save()
        playerStatusMap.remove(uuid)
    }
}