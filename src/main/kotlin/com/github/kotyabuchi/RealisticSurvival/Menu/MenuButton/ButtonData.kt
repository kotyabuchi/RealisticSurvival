package com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton

import com.github.kotyabuchi.RealisticSurvival.Main
import org.bukkit.NamespacedKey
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object ButtonData: KoinComponent {

    private val main: Main by inject()

    val buttonItemKey = NamespacedKey(main, "BUTTON_ITEM")
}