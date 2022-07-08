package com.github.kotyabuchi.RealisticSurvival.Utility

import com.github.kotyabuchi.RealisticSurvival.Main
import com.github.kotyabuchi.RealisticSurvival.System.Player.getStatus
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType

fun Player.sendActionMessage(message: String) {
    val blank = "                                   "
    this.sendMessage(blank.substring(0 until (blank.length - (message.length / 2))) + message)
}

fun Player.sendSuccessMessage(message: String) {
    this.sendMessage(Component.text(message).normalize(NamedTextColor.GREEN))
}

fun Player.sendErrorMessage(message: String) {
    this.sendMessage(Component.text(message).normalize(NamedTextColor.RED))
}

fun Player.sendSuccessActionBar(message: String) {
    this.sendActionBar(Component.text(message).normalize(NamedTextColor.GREEN))
}

fun Player.sendErrorActionBar(message: String) {
    this.sendActionBar(Component.text(message).normalize(NamedTextColor.RED))
}

fun Player.playSuccessSound() {
    this.playSound(this, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, .5f, 1.5f)
}

fun Player.playErrorSound() {
    this.playSound(this, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, .5f, .5f)
}

fun Player.hasTag(tag: NamespacedKey, dataType: PersistentDataType<out Any, out Any> = PersistentDataType.BYTE): Boolean {
    val pdc = this.persistentDataContainer
    return pdc.has(tag, dataType)
}

fun Player.hasTag(main: Main, tagName: String, dataType: PersistentDataType<out Any, out Any> = PersistentDataType.BYTE): Boolean {
    val pdc = this.persistentDataContainer
    return pdc.has(NamespacedKey(main, tagName), dataType)
}

fun Player.toggleTag(main: Main, tagName: String): Boolean {
    val pdc = this.persistentDataContainer
    return if (pdc.has(NamespacedKey(main, tagName), PersistentDataType.BYTE)) {
        pdc.remove(NamespacedKey(main, tagName))
        false
    } else {
        pdc.set(NamespacedKey(main, tagName), PersistentDataType.BYTE, 1)
        true
    }
}

fun Player.consumeFillBlockFromResourceStorage(block: Block): Material? {
    val resourceStorage = getStatus().resourceStorage
    var fillBlock: Material? = null
    val groundFillBlockMaterials = listOf(Material.STONE, Material.COBBLESTONE)
    val underGroundFillBlockMaterials = listOf(Material.DEEPSLATE, Material.COBBLED_DEEPSLATE)
    if (block.y > 0) {
        for (material in groundFillBlockMaterials) {
            if (resourceStorage.getStoredResourceAmount(material) > 1) {
                resourceStorage.restoreResource(material, 1)
                fillBlock = material
                break
            }
        }
        if (fillBlock == null) {
            for (material in underGroundFillBlockMaterials) {
                if (resourceStorage.getStoredResourceAmount(material) > 1) {
                    resourceStorage.restoreResource(material, 1)
                    fillBlock = material
                    break
                }
            }
        }
    } else {
        for (material in underGroundFillBlockMaterials) {
            if (resourceStorage.getStoredResourceAmount(material) > 1) {
                resourceStorage.restoreResource(material, 1)
                fillBlock = material
                break
            }
        }
        if (fillBlock == null) {
            for (material in groundFillBlockMaterials) {
                if (resourceStorage.getStoredResourceAmount(material) > 1) {
                    resourceStorage.restoreResource(material, 1)
                    fillBlock = material
                    break
                }
            }
        }
    }
    return fillBlock
}