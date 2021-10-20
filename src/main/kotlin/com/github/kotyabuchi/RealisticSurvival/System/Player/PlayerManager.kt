package com.github.kotyabuchi.RealisticSurvival.System.Player

import com.github.kotyabuchi.RealisticSurvival.Job.JobMaster
import com.github.kotyabuchi.RealisticSurvival.Main
import com.github.kotyabuchi.RealisticSurvival.Utility.DataBaseManager
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.scheduler.BukkitRunnable
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*
import kotlin.random.Random

fun Player.getStatus(): PlayerStatus {
    val uuid = this.uniqueId
    if (!PlayerManager.playerStatusMap.containsKey(uuid)) PlayerManager.playerStatusMap[uuid] = PlayerStatus(this)
    return PlayerManager.playerStatusMap[uuid]!!
}

fun Player.getJobLevel(job: JobMaster): Int {
    return this.getStatus().getJobStatus(job).getLevel()
}

object PlayerManager: Listener, KoinComponent {

    private val main: Main by inject()

    val playerStatusMap = mutableMapOf<UUID, PlayerStatus>()

    init {
        val status = DataBaseManager.loadPlayerStatus(*main.server.onlinePlayers.toTypedArray())

        status.forEach {
            playerStatusMap[it.player.uniqueId] = it
            it.showManaIndicator()
        }
    }

    fun hideAllManaIndicator() {
        playerStatusMap.values.forEach {
            it.hideManaIndicator()
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
        val playerStatus = DataBaseManager.loadPlayerStatus(player).first()
        playerStatusMap[player.uniqueId] = playerStatus
        playerStatus.showManaIndicator()
    }

    @EventHandler
    fun onQuitServer(event: PlayerQuitEvent) {
        val player = event.player
        val uuid = player.uniqueId
        playerStatusMap[uuid]?.let {
            it.save()
            it.hideManaIndicator()
        }
        playerStatusMap.remove(uuid)
    }
}