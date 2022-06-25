package com.github.kotyabuchi.RealisticSurvival.Menu

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Sound

class WorldsMenu: Menu(Component.text("Worlds"), Bukkit.getServer().worlds.size) {

    private val menuContentSize = 9 * 5
    private val createdPages = mutableSetOf<Int>()
    private val soundList = Sound.values().toList().chunked(menuContentSize)

    init {
        createMenu()
    }

    override fun createMenu() {
        createPageIfNeed(soundList.size - 1)
    }
}