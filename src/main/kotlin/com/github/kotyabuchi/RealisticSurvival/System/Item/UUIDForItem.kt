package com.github.kotyabuchi.RealisticSurvival.System.Item

import com.github.kotyabuchi.RealisticSurvival.Main
import org.bukkit.NamespacedKey
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockDropItemEvent
import org.bukkit.event.entity.EntityDropItemEvent
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.persistence.PersistentDataType
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

object UUIDForItem: Listener, KoinComponent {

    private val main: Main by inject()

    val UUIDKey = NamespacedKey(main, "UUID")

    @EventHandler
    fun onCraft(event: CraftItemEvent) {
        val item = event.currentItem ?: return
        if (item.maxStackSize > 1) return
        item.editMeta {
            it.persistentDataContainer.set(UUIDKey, PersistentDataType.STRING, UUID.randomUUID().toString())
        }
        event.currentItem = item
    }

    @EventHandler
    fun onPlayerDrop(event: PlayerDropItemEvent) {
        val item = event.itemDrop.itemStack
        if (item.maxStackSize > 1) return
        item.editMeta {
            it.persistentDataContainer.set(UUIDKey, PersistentDataType.STRING, UUID.randomUUID().toString())
        }
        event.itemDrop.itemStack = item
    }

    @EventHandler
    fun onEntityDrop(event: EntityDropItemEvent) {
        val item = event.itemDrop.itemStack
        if (item.maxStackSize > 1) return
        item.editMeta {
            it.persistentDataContainer.set(UUIDKey, PersistentDataType.STRING, UUID.randomUUID().toString())
        }
        event.itemDrop.itemStack = item
    }

    @EventHandler
    fun onBlockDrop(event: BlockDropItemEvent) {
        event.items.forEach { item->
            val itemStack = item.itemStack
            if (itemStack.maxStackSize > 1) return
            itemStack.editMeta {
                it.persistentDataContainer.set(UUIDKey, PersistentDataType.STRING, UUID.randomUUID().toString())
            }
            item.itemStack = itemStack
        }
    }
}