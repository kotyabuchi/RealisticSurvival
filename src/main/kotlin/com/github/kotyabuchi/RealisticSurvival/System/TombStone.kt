package com.github.kotyabuchi.RealisticSurvival.System

import com.eclipsesource.json.Json
import com.eclipsesource.json.JsonObject
import com.github.kotyabuchi.RealisticSurvival.Main
import com.github.kotyabuchi.RealisticSurvival.Utility.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.title.Title
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.scheduler.BukkitRunnable
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File
import java.util.*

object TombStone: Listener, KoinComponent {

    private val main: Main by inject()

    private val tombStoneFile = File(main.dataFolder, "TombStones.json")
    private val tombStones: JsonObject
    private val tombStoneKey = NamespacedKey(main, "TombStone")
    val lastDeathPointKey = NamespacedKey(main, "LastDeathPoint")

    val tombStoneItem = ItemStack(Material.OAK_SIGN)

    init {
        tombStones = if (tombStoneFile.exists()) {
            try {
                Json.parse(readFile(tombStoneFile)).asObject()
            } catch (e: Exception) {
                e.printStackTrace()
                JsonObject()
            }
        } else {
            tombStoneFile.createNewFile()
            JsonObject()
        }

        tombStoneItem.editMeta {
            it.setCustomModelData(100)
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onDeath(event: PlayerDeathEvent) {
        val player = event.entity
        val inv = player.inventory
        val keepItems = event.itemsToKeep

        val json = JsonObject()
        val equipments = JsonObject()
        val storage = JsonObject()

        EquipmentSlot.values().forEach {
            if (it != EquipmentSlot.HAND) {
                val itemStack = inv.getItem(it)
                if (!itemStack.type.isAir && !keepItems.contains(itemStack)) equipments.set(it.name, ItemUtil.serializedString(itemStack))
            }
        }
        inv.storageContents.forEachIndexed { index, itemStack ->
            if (itemStack != null && !itemStack.type.isAir && !keepItems.contains(itemStack)) storage.set(index.toString(), ItemUtil.serializedString(itemStack))
        }

        if (equipments.isEmpty && storage.isEmpty) return

        val tombStoneLoc = player.location.block.location.toCenterLocation()
        tombStoneLoc.y = player.location.y

        json.set("Location", JsonObject().run {
            set("World", player.world.uid.toString())
            set("X", tombStoneLoc.x)
            set("Y", tombStoneLoc.y)
            set("Z", tombStoneLoc.z)
        })
        json.set("Equipment", equipments)
        json.set("Storage", storage)

        val playersTombStones = tombStones.get(player.uniqueId.toString())?.asObject() ?: JsonObject()

        EntityType.ARMOR_STAND.entityClass?.let {
            player.world.spawn(tombStoneLoc, it) { stand ->
                stand as ArmorStand
                stand.equipment.setHelmet(tombStoneItem.clone(), true)
                stand.isSilent = true
                stand.isVisible = false
                stand.setGravity(false)
                playersTombStones.set(stand.uniqueId.toString(), json)
                stand.persistentDataContainer.set(tombStoneKey, PersistentDataType.STRING, player.uniqueId.toString())
                stand.addPassenger(
                    player.world.spawn(tombStoneLoc, it) { nameStand ->
                        nameStand as ArmorStand
                        nameStand.customName(
                            Component.text("R.I.P [${player.name}]", NamedTextColor.WHITE, TextDecoration.BOLD).normalize()
                        )
                        nameStand.isCustomNameVisible = true
                        nameStand.isMarker = true
                        nameStand.isSilent = true
                        nameStand.isVisible = false
                        stand.setGravity(false)
                    }
                )
            }
        }
        event.drops.clear()
        tombStones.set(player.uniqueId.toString(), playersTombStones)
        player.persistentDataContainer.set(lastDeathPointKey, PersistentDataType.STRING, "${tombStoneLoc.world.name},${tombStoneLoc.x},${tombStoneLoc.y},${tombStoneLoc.z}")
    }

    @EventHandler
    fun onClick(event: PlayerInteractAtEntityEvent) {
        val player = event.player
        val tombStone = event.rightClicked as? ArmorStand ?: return
        val pdc = tombStone.persistentDataContainer
        val uuid = pdc.get(tombStoneKey, PersistentDataType.STRING) ?: return
        event.isCancelled = true
        if (uuid.isEmpty()) return
        if (player.uniqueId != UUID.fromString(uuid)) {
            player.showTitle(Title.title(Component.empty(), Component.text("他人の墓荒らしは良くないよ", NamedTextColor.RED, TextDecoration.BOLD).normalize(NamedTextColor.RED)))
            return
        }
        loadTombStoneItems(player, tombStone)
    }

    @EventHandler
    fun onDamage(event: EntityDamageEvent) {
        val entity = event.entity as? ArmorStand ?: return
        if (entity.persistentDataContainer.has(tombStoneKey, PersistentDataType.STRING)) event.isCancelled = true
    }

    @EventHandler
    fun onAttack(event: EntityDamageByEntityEvent) {
        val tombStone = event.entity as? ArmorStand ?: return
        if (!tombStone.persistentDataContainer.has(tombStoneKey, PersistentDataType.STRING)) return
        event.isCancelled = true
        event.damage = 0.0
        val player = event.damager as? Player ?: return
        val pdc = tombStone.persistentDataContainer
        val uuid = pdc.get(tombStoneKey, PersistentDataType.STRING) ?: return
        event.isCancelled = true
        if (uuid.isEmpty()) return
        if (player.uniqueId != UUID.fromString(uuid))  {
            player.showTitle(Title.title(Component.empty(), Component.text("他人の墓荒らしは良くないよ", NamedTextColor.RED, TextDecoration.BOLD).normalize(NamedTextColor.RED)))
            return
        }
        loadTombStoneItems(player, tombStone)
    }

    fun getPlayerTombStones(playerUUID: UUID): JsonObject? {
        return tombStones.get(playerUUID.toString())?.asObject()
    }

    fun getPlayerTombStones(player: Player): JsonObject? {
        return getPlayerTombStones(player.uniqueId)
    }

    fun getTombStoneItems(playerUUID: UUID, tombStoneUUID: UUID): TombStoneItem? {
        val tombStoneJson = getPlayerTombStones(playerUUID)?.get(tombStoneUUID.toString())?.asObject() ?: return null
        return TombStoneItem(tombStoneJson)
    }

    fun getTombStoneItems(player: Player, tombStoneUUID: UUID): TombStoneItem? {
        return getTombStoneItems(player.uniqueId, tombStoneUUID)
    }

    private fun loadTombStoneItems(player: Player, tombStone: ArmorStand) {
        val pdc = tombStone.persistentDataContainer
        val tombStoneUUID = tombStone.uniqueId
        val tombItems = getTombStoneItems(player, tombStoneUUID) ?: return

        pdc.remove(tombStoneKey)

        object : BukkitRunnable() {
            val loc = tombStone.location
            var count = 0
            override fun run() {
                if (player.isDead) {
                    pdc.set(tombStoneKey, PersistentDataType.STRING, player.uniqueId.toString())
                    cancel()
                } else {
                    if (count >= 2) {
                        val inventory = player.inventory

                        val inventoryBackup = mutableListOf<ItemStack>()

                        tombItems.getEquipmentItems().forEach {
                            val equipmentItem = inventory.getItem(it.key)
                            if (!equipmentItem.type.isAir) inventoryBackup.add(equipmentItem)
                            inventory.setItem(it.key, it.value)
                        }

                        tombItems.getStorageItems().forEach {
                            val slot = it.key
                            val storageItem = inventory.getItem(slot)
                            if (storageItem != null && !storageItem.type.isAir) inventoryBackup.add(storageItem)
                            inventory.setItem(slot, it.value)
                        }

                        inventoryBackup.forEach { backupItem ->
                            inventory.addItemOrDrop(player, backupItem)
                        }

                        removeTombStone(player, tombStone)
                        player.persistentDataContainer.remove(lastDeathPointKey)

                        cancel()
                    }
                    player.world.playSound(loc, Sound.BLOCK_GRAVEL_BREAK, 1.5f, .5f)
                }
                count++
            }
        }.runTaskTimer(main, 0, 10)
    }

    fun removeTombStone(player: Player, tombStone: ArmorStand) {
        val playersTombStones = getPlayerTombStones(player) ?: return
        val tombStoneUUID = tombStone.uniqueId.toString()
        playersTombStones.remove(tombStoneUUID)
        tombStones.set(player.uniqueId.toString(), playersTombStones)

        tombStone.passengers.forEach {
            it.remove()
        }
        tombStone.remove()
    }

    fun saveTombStoneFile() {
        saveFile(tombStoneFile, tombStones)
        println("&a[System]TombStoneを保存しました".colorS())
    }

    class TombStoneItem(itemJson: JsonObject) {
        private var location: Location? = null
        private val equipmentItems = mutableMapOf<EquipmentSlot, ItemStack>()
        private val storageItems = mutableMapOf<Int, ItemStack>()

        init {
            itemJson.get("Location")?.asObject()?.let {
                val world = main.server.getWorld(it.getString("World", ""))
                val x = it.get("X")?.asDouble()
                val y = it.get("Y")?.asDouble()
                val z = it.get("Z")?.asDouble()

                world?.let {
                    ifLet(x, y, z) { (x, y, z) ->
                        location = Location(it, x, y, z)
                    }
                }
            }

            val equipmentObject = itemJson.get("Equipment").asObject()
            equipmentObject.forEach {
                val serializedStr = equipmentObject.getString(it.name, null)
                if (serializedStr != null) equipmentItems[EquipmentSlot.valueOf(it.name)] = ItemUtil.deserializeItem(serializedStr)
            }

            val storageObject = itemJson.get("Storage").asObject()
            storageObject.forEach {
                val serializedStr = storageObject.getString(it.name, null)
                if (serializedStr != null) storageItems[it.name.toInt()] = ItemUtil.deserializeItem(serializedStr)
            }
        }

        fun getEquipmentItems(): Map<EquipmentSlot, ItemStack> = equipmentItems
        fun getStorageItems(): Map<Int, ItemStack> = storageItems
    }
}