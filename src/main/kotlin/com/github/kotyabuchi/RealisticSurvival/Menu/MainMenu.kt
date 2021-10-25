package com.github.kotyabuchi.RealisticSurvival.Menu

import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.Home.HomeButton
import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.Job.JobButton
import net.kyori.adventure.text.Component

class MainMenu: Menu(Component.text("Menu"), 1) {

    init {
        createMenu()
    }

    override fun createMenu() {
        setFrame()
        setMenuButton(JobButton())
        setMenuButton(HomeButton())
    }
}