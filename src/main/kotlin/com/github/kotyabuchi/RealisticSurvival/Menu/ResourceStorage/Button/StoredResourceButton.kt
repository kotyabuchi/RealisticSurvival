package com.github.kotyabuchi.RealisticSurvival.Menu.ResourceStorage.Button

import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.ButtonData
import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.ButtonItem
import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.MenuButton
import com.github.kotyabuchi.RealisticSurvival.Utility.floorInt
import com.github.kotyabuchi.RealisticSurvival.Utility.normalize
import net.kyori.adventure.text.Component
import org.bukkit.Material

class StoredResourceButton(val material: Material, val totalAmount: Int): MenuButton() {

    init {
        val maxStackSize = material.maxStackSize
        val unit = (totalAmount / maxStackSize.toDouble()).floorInt()
        val lore = mutableListOf<Component>()
        lore.add(Component.text("Unit: ", ButtonData.buttonLoreStyle).append(Component.text("${unit}stacks + ${totalAmount % maxStackSize}items").normalize()))
        lore.add(Component.text("Items: ", ButtonData.buttonLoreStyle).append(Component.text("$totalAmount").normalize()))
        lore.add(Component.empty())
        lore.add(Component.text("Restore").normalize())
        lore.add(Component.text("Left Click: ", ButtonData.buttonLoreStyle).append(Component.text("1 item").normalize()))
        lore.add(Component.text("Right Click: ", ButtonData.buttonLoreStyle).append(Component.text("5 items").normalize()))
        lore.add(Component.text("Shift Click: ", ButtonData.buttonLoreStyle).append(Component.text("1 stack").normalize()))
        menuIcon = ButtonItem(material, lore = lore, disableDisplayName = true)
    }
}