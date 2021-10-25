package com.github.kotyabuchi.RealisticSurvival.Event

import com.github.kotyabuchi.RealisticSurvival.Job.JobMaster
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent

abstract class JobActionEvent(player: Player, val jobMaster: JobMaster): PlayerEvent(player) {

    companion object {
        private val handlerList = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList {
            return handlerList
        }
    }

    override fun getHandlers(): HandlerList = handlerList
}