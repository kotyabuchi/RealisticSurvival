package com.github.kotyabuchi.RealisticSurvival.Menu

import com.github.kotyabuchi.RealisticSurvival.Job.JobType
import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.Basic.BlankButton
import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.Job.JobInfoButton
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

class JobMenu(val player: Player): Menu(Component.text("Job"), JobType.values().size, FrameType.TOP, FrameType.SIDE) {

    init {
        createMenu()
    }

    override fun createMenu() {
        JobType.values().forEach {
            setMenuButton(JobInfoButton(it, player))
        }
    }
}