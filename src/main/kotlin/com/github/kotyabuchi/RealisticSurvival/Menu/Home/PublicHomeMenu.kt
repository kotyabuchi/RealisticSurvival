package com.github.kotyabuchi.RealisticSurvival.Menu.Home

import com.github.kotyabuchi.RealisticSurvival.Menu.Home.Button.HomeInfoButton
import com.github.kotyabuchi.RealisticSurvival.Menu.Menu
import com.github.kotyabuchi.RealisticSurvival.System.Player.HomePoint
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

class PublicHomeMenu(private val player: Player): Menu(Component.text("Public Homes"), HomePoint.getPublicHomes().size, FrameType.TOP, FrameType.SIDE) {

    init {
        createMenu()
    }

    override fun createMenu() {
        var page = 0
        HomePoint.getPublicHomes().forEach {
            if (getLastBlankSlot(page) == null) page++
            setMenuButton(HomeInfoButton(player, it), page)
        }
    }
}