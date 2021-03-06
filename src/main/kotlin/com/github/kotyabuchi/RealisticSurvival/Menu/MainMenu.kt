package com.github.kotyabuchi.RealisticSurvival.Menu

import com.github.kotyabuchi.RealisticSurvival.Menu.Home.Button.HomeButton
import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.Job.JobButton
import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.SoundMenuButton
import com.github.kotyabuchi.RealisticSurvival.Menu.ResourceStorage.Button.ResourceStorageButton
import net.kyori.adventure.text.Component

class MainMenu(private val isOp: Boolean): Menu(Component.text("Menu"), 3, FrameType.TOP, FrameType.SIDE) {

    init {
        createMenu()
    }

    override fun createMenu() {
        setMenuButton(JobButton)
        setMenuButton(HomeButton)
        setMenuButton(ResourceStorageButton)
        if (isOp) {
            setMenuButton(SoundMenuButton)
        }
    }
}