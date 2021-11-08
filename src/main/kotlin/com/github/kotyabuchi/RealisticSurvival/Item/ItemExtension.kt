package com.github.kotyabuchi.RealisticSurvival.Item

import com.github.kotyabuchi.RealisticSurvival.Main
import com.github.kotyabuchi.RealisticSurvival.Utility.normalize
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.bukkit.persistence.PersistentDataType
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.math.max
import kotlin.math.min
import kotlin.math.round

class ItemExtension(_itemStack: ItemStack): KoinComponent {

    private val main: Main by inject()

    val itemStack: ItemStack = _itemStack
    var hasDurability: Boolean = false
        private set
    var maxDurability: Int = 0
        private set
    var durability: Int = 0
        private set

    private val maxDurabilityKey = NamespacedKey(main, "MaxDurability")
    private val durabilityKey = NamespacedKey(main, "Durability")

    init {
        itemStack.itemMeta?.let { meta ->
            val pdc = meta.persistentDataContainer
            if (meta is Damageable && itemStack.type.maxDurability > 0) {
                hasDurability = true
                maxDurability = pdc.getOrDefault(maxDurabilityKey, PersistentDataType.INTEGER, itemStack.type.maxDurability.toInt())
                durability = pdc.getOrDefault(durabilityKey, PersistentDataType.INTEGER, min(maxDurability - meta.damage, maxDurability))
            }
        }
    }

    fun maxDurability(amount: Int): ItemExtension {
        maxDurability = amount
        return this
    }

    fun durability(amount: Int): ItemExtension {
        durability = min(maxDurability, amount)
        return this
    }

    fun damage(amount: Int): ItemExtension {
        durability(max(0, durability - amount))
        return this
    }

    fun regen(amount: Int): ItemExtension {
        durability(min(maxDurability, durability + amount))
        return this
    }

    fun applyDurability(): ItemExtension {
        if (hasDurability) {
            itemStack.editMeta { meta ->
                val percent = durability.toDouble() / maxDurability * 100
                meta as Damageable
                val materialDurability = itemStack.type.maxDurability
                val ratio = materialDurability / 100.0
                meta.damage = materialDurability - min(materialDurability.toInt(), round(ratio * percent).toInt())
                val pdc = meta.persistentDataContainer
                pdc.set(maxDurabilityKey, PersistentDataType.INTEGER, maxDurability)
                pdc.set(durabilityKey, PersistentDataType.INTEGER, durability)
            }
        }
        return this
    }

    fun applySetting(): ItemExtension {
        val lore = mutableListOf<Component>()
        if (hasDurability) {
            lore.add(Component.empty())
            lore.add(Component.text("Durability: ").normalize(NamedTextColor.GRAY).append(Component.text("$durability / $maxDurability").normalize(NamedTextColor.GREEN)))
        }
        itemStack.editMeta {
            it.lore(lore)
        }
        return this
    }
}