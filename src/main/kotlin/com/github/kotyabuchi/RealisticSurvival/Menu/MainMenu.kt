package com.github.kotyabuchi.RealisticSurvival.Menu

import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.Home.HomeButton
import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.Job.JobButton
import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.SoundMenuButton
import net.kyori.adventure.text.Component

class MainMenu(private val isOp: Boolean): Menu(Component.text("Menu"), 3, FrameType.TOP, FrameType.SIDE) {

    init {
        createMenu()
    }

    override fun createMenu() {
        setMenuButton(JobButton())
        setMenuButton(HomeButton())
        if (isOp) {
            setMenuButton(SoundMenuButton())
        }
    }
}