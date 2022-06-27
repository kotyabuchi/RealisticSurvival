package com.github.kotyabuchi.RealisticSurvival.Menu.Home

import com.github.kotyabuchi.RealisticSurvival.Menu.Home.Button.ChangeHomeIconButton
import com.github.kotyabuchi.RealisticSurvival.Menu.Home.Button.ChangePublicButton
import com.github.kotyabuchi.RealisticSurvival.Menu.Home.Button.RemoveHomeButton
import com.github.kotyabuchi.RealisticSurvival.Menu.Home.Button.RenameHomeButton
import com.github.kotyabuchi.RealisticSurvival.Menu.Menu
import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.Basic.BlankButton
import com.github.kotyabuchi.RealisticSurvival.System.Player.Home
import net.kyori.adventure.text.Component

class HomeSettingMenu(private val home: Home): Menu(Component.text("Home Setting"), 4, FrameType.TOP, FrameType.SIDE) {

    init {
        createMenu()
    }

    override fun createMenu() {
        setMenuButton(ChangeHomeIconButton(home))
        setMenuButton(RenameHomeButton(home))
        setMenuButton(ChangePublicButton(home, this))
        setMenuButton(BlankButton())
        setMenuButton(BlankButton())
        setMenuButton(RemoveHomeButton(home, this))
        setMenuButton(BlankButton())
    }
}