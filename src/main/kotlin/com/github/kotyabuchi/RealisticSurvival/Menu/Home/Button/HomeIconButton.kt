package com.github.kotyabuchi.RealisticSurvival.Menu.Home.Button

import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.ButtonItem
import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.MenuButton
import org.bukkit.Material

class HomeIconButton(val iconMaterial: Material): MenuButton() {

    init {
        menuIcon = ButtonItem(iconMaterial, disableDisplayName = true)
    }
}