package com.github.kotyabuchi.RealisticSurvival.Event

import com.github.kotyabuchi.RealisticSurvival.Menu.Menu
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class ChangeMenuPageEvent(val menu: Menu, val nextPage: Int, val totalPage: Int, val isNext: Boolean, val player: Player): Event() {

    companion object {
        private val handlerList = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList {
            return handlerList
        }
    }

    override fun getHandlers(): HandlerList = handlerList
}