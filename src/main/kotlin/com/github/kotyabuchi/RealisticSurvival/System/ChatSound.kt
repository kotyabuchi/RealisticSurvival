package com.github.kotyabuchi.RealisticSurvival.System

import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.sound.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

object ChatSound: Listener {

    @EventHandler
    fun onChat(event: AsyncChatEvent) {
        event.viewers().forEach {
            it.playSound(Sound.sound(org.bukkit.Sound.ENTITY_CHICKEN_EGG, Sound.Source.MASTER, .5f, 1.5f))
        }
    }
}