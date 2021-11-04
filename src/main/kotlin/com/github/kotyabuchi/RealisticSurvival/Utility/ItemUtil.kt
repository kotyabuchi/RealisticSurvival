package com.github.kotyabuchi.RealisticSurvival.Utility

import com.github.kotyabuchi.RealisticSurvival.Item.Enum.ArmorType
import com.github.kotyabuchi.RealisticSurvival.Item.Enum.EquipmentType
import com.github.kotyabuchi.RealisticSurvival.Item.Enum.ToolType
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import java.util.*
import kotlin.random.Random

object ItemUtil {
    fun serializedString(itemStack: ItemStack): String {
        return Base64.getEncoder().encodeToString(itemStack.serializeAsBytes())
    }

    fun deserializeItem(serializeString: String): ItemStack {
        return ItemStack.deserializeBytes(Base64.getDecoder().decode(serializeString))
    }
}

fun Material.hasDurability(): Boolean {
    return this.isTools() || this.isWeapons() || this.isArmors() || this.isShield()
}

fun Material.isSword(): Boolean {
    return ToolType.SWORD.includes(this)
}

fun Material.isPickAxe(): Boolean {
    return ToolType.PICKAXE.includes(this)
}

fun Material.isAxe(): Boolean {
    return ToolType.AXE.includes(this)
}

fun Material.isShovel(): Boolean {
    return ToolType.SHOVEL.includes(this)
}

fun Material.isHoe(): Boolean {
    return ToolType.HOE.includes(this)
}

fun Material.isShield(): Boolean {
    return ToolType.SHIELD.includes(this)
}

fun Material.isTools(): Boolean {
    return this == Material.SHIELD ||
            this.isPickAxe() ||
            this.isShovel() ||
            this.isHoe() ||
            this.isAxe() ||
            this == Material.FISHING_ROD ||
            this == Material.FLINT_AND_STEEL
}

fun Material.isWeapons(): Boolean {
    return this == Material.BOW ||
            this == Material.CROSSBOW ||
            this.isSword() ||
            this.isAxe()
}

fun Material.isHelmet(): Boolean {
    return ArmorType.HELMET.includes(this)
}

fun Material.isChestplate(): Boolean {
    return ArmorType.CHESTPLATE.includes(this)
}

fun Material.isLeggings(): Boolean {
    return ArmorType.LEGGINGS.includes(this)
}

fun Material.isBoots(): Boolean {
    return ArmorType.BOOTS.includes(this)
}

fun Material.isArmors(): Boolean {
    return this.isHelmet() ||
            this.isChestplate() ||
            this.isLeggings() ||
            this.isBoots()
}

fun Material.getEquipmentType(): EquipmentType? {
    ToolType.values().forEach {
        if (it.includes(this)) return it
    }
    ArmorType.values().forEach {
        if (it.includes(this)) return it
    }
    return null
}

fun ItemStack.damage(_amount: Int) {
    val meta = itemMeta
    if (meta is Damageable) {
        var amount = _amount
        val damageChance = 100 / (getEnchantmentLevel(Enchantment.DURABILITY) + 1)
        if (containsEnchantment(Enchantment.DURABILITY)) {
            repeat(_amount) {
                if (Random.nextInt(100) <= damageChance) amount--
            }
        }
//        if (amount > 0) main.server.pluginManager.callEvent(PlayerItemDamageEvent(player, this, amount))
        if (amount > 0) {
            meta.damage += amount
            this.itemMeta = meta
        }
    }
}