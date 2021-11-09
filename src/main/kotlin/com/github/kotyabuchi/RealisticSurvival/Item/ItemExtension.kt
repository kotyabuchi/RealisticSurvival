package com.github.kotyabuchi.RealisticSurvival.Item

import com.github.kotyabuchi.RealisticSurvival.Main
import com.github.kotyabuchi.RealisticSurvival.Utility.hasDurability
import com.github.kotyabuchi.RealisticSurvival.Utility.normalize
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.bukkit.persistence.PersistentDataType
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.logging.Level
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
            if (itemStack.type.hasDurability() && meta is Damageable) {
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

    fun damage(amount: Int, player: Player): ItemExtension {
        if (hasDurability) {
            damage(amount)
            notifyDurability(player)
        } else {
            val stackTrace = Throwable().stackTraceToString()
            Bukkit.getLogger().log(Level.INFO, """
                
                ItemExtension Damage call not damageable
                ItemStack: $itemStack
                $stackTrace
            """.trimIndent())
        }
        return this
    }

    fun mending(amount: Int): ItemExtension {
        durability(min(maxDurability, durability + amount))
        return this
    }

    fun mending(amount: Int, player: Player): ItemExtension {
        if (hasDurability) {
            mending(amount)
            notifyDurability(player)
        } else {
        val stackTrace = Throwable().stackTraceToString()
        Bukkit.getLogger().log(Level.INFO, """
                
                ItemExtension Mending call not damageable
                ItemStack: $itemStack
                $stackTrace
            """.trimIndent())
    }
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

    fun notifyDurability(player: Player) {
        val percent = durability / maxDurability.toDouble()
        val color = when {
            percent > .7 -> NamedTextColor.GREEN
            percent > .5 -> NamedTextColor.GOLD
            percent > .2 -> NamedTextColor.RED
            else -> NamedTextColor.DARK_RED
        }
        player.sendActionBar(Component.text("$durability / $maxDurability").normalize(color))
    }
}