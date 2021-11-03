package com.github.kotyabuchi.RealisticSurvival.Menu

import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.Home.HomeButton
import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.Job.JobButton
import net.kyori.adventure.text.Component

class MainMenu: Menu(Component.text("Menu"), 2, FrameType.TOP, FrameType.SIDE) {

    init {
        createMenu()
    }

    override fun createMenu() {
        setMenuButton(JobButton())
        setMenuButton(HomeButton())
    }
}