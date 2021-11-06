package com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton

import com.github.kotyabuchi.RealisticSurvival.CustomModelData
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

class ButtonItem(material: Material, displayName: Component = Component.empty(), normalizeDecoration: Boolean = true, lore: List<Component> = listOf(), modelData: CustomModelData? = null): ItemStack(material) {

    init {
        editMeta { meta ->
            meta.displayName(if (normalizeDecoration) displayName.decoration(TextDecoration.ITALIC, false) else displayName)
            meta.lore(lore)
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
            meta.persistentDataContainer.set(ButtonData.buttonItemKey, PersistentDataType.BYTE, 1)
            modelData?.let {
                meta.setCustomModelData(modelData.id)
            }
        }
    }
}