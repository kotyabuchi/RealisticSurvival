package com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton

import net.kyori.adventure.text.Component
import org.bukkit.Material

class BlankButton(material: Material = Material.BLACK_STAINED_GLASS_PANE): MenuButton() {

    init {
        menuIcon = ButtonItem(material, Component.text(""))
        clickSound = null
    }
}