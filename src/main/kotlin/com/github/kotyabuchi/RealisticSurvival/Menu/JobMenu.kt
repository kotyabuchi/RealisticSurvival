package com.github.kotyabuchi.RealisticSurvival.Menu

import com.github.kotyabuchi.RealisticSurvival.Job.JobType
import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.Basic.BlankButton
import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.Job.JobInfoButton
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import kotlin.math.ceil

class JobMenu(val player: Player): Menu(Component.text("Job"), ceil(JobType.values().size / 7.0).toInt()) {

    init {
        createMenu()
    }

    override fun createMenu() {
        setFrame()
        JobType.values().forEach {
            setMenuButton(JobInfoButton(it, player))
        }
        while (getLastBlankSlot() != null) {
            setMenuButton(BlankButton())
        }
    }
}