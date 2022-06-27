package com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton

import com.github.kotyabuchi.RealisticSurvival.Utility.canNotItem
import com.github.kotyabuchi.RealisticSurvival.Utility.normalize
import com.github.kotyabuchi.RealisticSurvival.Utility.valueOfOrNull
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

class SoundSampleButton(private val sound: Sound): MenuButton() {

    init {
        val soundName = sound.name
        val splitName = soundName.split("_")
        var material: Material = when (splitName[0]) {
            "BLOCK" -> valueOfOrNull<Material>("${splitName[1]}_${splitName[2]}") ?: valueOfOrNull<Material>(splitName[1])
            "ENTITY" -> valueOfOrNull<Material>("${splitName[1]}_${splitName[2]}_SPAWN_EGG") ?: valueOfOrNull<Material>("${splitName[1]}_SPAWN_EGG") ?: valueOfOrNull<Material>("${splitName[1]}_${splitName[2]}") ?: valueOfOrNull<Material>(splitName[1])
            else -> Material.NOTE_BLOCK
        } ?: Material.NOTE_BLOCK
        if (material.canNotItem()) material = Material.NOTE_BLOCK
        val lore = mutableListOf<Component>()
        lore.add(Component.text("LeftClick: Normal").normalize())
        lore.add(Component.text("RightClick: 0.5").normalize())
        lore.add(Component.text("MiddleClick: 1.5").normalize())
        menuIcon = ButtonItem(material, Component.text(sound.name).normalize(), lore = lore)
        clickSound = null
    }

    override fun leftClickEvent(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        when (event.click) {
            ClickType.LEFT -> {
                player.playSound(player.eyeLocation, sound, 1f, 1f)
            }
            ClickType.RIGHT -> {
                player.playSound(player.eyeLocation, sound, 1f, .5f)
            }
            ClickType.MIDDLE -> {
                player.playSound(player.eyeLocation, sound, 1f, 1.5f)
            }
            else -> return
        }
    }
}