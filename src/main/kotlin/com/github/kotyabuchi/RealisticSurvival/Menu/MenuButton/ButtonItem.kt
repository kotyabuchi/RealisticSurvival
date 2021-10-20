package com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

class ButtonItem(material: Material, displayName: Component = Component.empty(), normalizeDecoration: Boolean = true, lore: List<Component> = listOf()): ItemStack(material) {

    init {
        editMeta {
            it.displayName(if (normalizeDecoration) displayName.decoration(TextDecoration.ITALIC, false) else displayName)
            it.lore(lore)
            it.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
            it.persistentDataContainer.set(ButtonData.buttonItemKey, PersistentDataType.BYTE, 1)
        }
    }
}