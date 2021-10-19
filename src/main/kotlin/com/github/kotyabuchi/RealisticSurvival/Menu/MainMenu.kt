package com.github.kotyabuchi.RealisticSurvival.Menu

import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.JobButton
import net.kyori.adventure.text.Component

class MainMenu: Menu(Component.text("Menu"), 1) {

    init {
        setFrame()
        setMenuButton(JobButton())
    }
}