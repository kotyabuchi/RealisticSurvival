package com.github.kotyabuchi.RealisticSurvival.System

import com.github.kotyabuchi.RealisticSurvival.CustomPersistentDataType.PersistentDataTypeBoolean
import com.github.kotyabuchi.RealisticSurvival.Event.PlayerInteractBlockEvent
import com.github.kotyabuchi.RealisticSurvival.Main
import com.github.kotyabuchi.RealisticSurvival.Utility.addItemOrDrop
import com.github.kotyabuchi.RealisticSurvival.Utility.normalize
import com.github.kotyabuchi.RealisticSurvival.Utility.upperCamelCase
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.apache.commons.lang3.SerializationUtils
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.block.Dispenser
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataAdapterContext
import org.bukkit.persistence.PersistentDataType
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.ByteArrayInputStream
import java.io.ObjectInputStream

object OmniDispenser: Listener, KoinComponent {

    private val main: Main by inject()

    private val attachmentItemStack = ItemStack(Material.LIME_DYE)

    private val attachmentKey = NamespacedKey(main, "Attachment")
    private val attachmentTypeKey = NamespacedKey(main, "AttachmentType")
    private val attachmentTierKey = NamespacedKey(main, "AttachmentTier")

    private fun getAttachmentItem(attachmentType: AttachmentType, attachmentTier: AttachmentTier): ItemStack {
        val result = attachmentItemStack.clone()
        result.editMeta {
            it.displayName(Component.text("Attachment: " + attachmentType.name.upperCamelCase()).normalize())
            val lore = listOf(
                Component.text("Tier: ").append(Component.text(attachmentTier.name.upperCamelCase()).normalize(attachmentTier.color)),
                Component.text("Range: " + attachmentTier.range)
            )
            it.lore(lore)

            it.setCustomModelData(attachmentType.modelData + attachmentTier.modelData)
            val pdc = it.persistentDataContainer
            pdc.set(attachmentTypeKey, PersistentDataTypeAttachmentType, attachmentType)
            pdc.set(attachmentTierKey, PersistentDataTypeAttachmentTier, attachmentTier)
            pdc.set(attachmentKey, PersistentDataTypeBoolean, true)
        }
        return result
    }

    @EventHandler
    fun onGetItem(event: PlayerInteractEvent) {
        val player = event.player
        if (player.isSneaking) return

        player.inventory.addItemOrDrop(player, getAttachmentItem(AttachmentType.values().random(), AttachmentTier.values().random()))
    }

    @EventHandler
    fun onSneakClick(event: PlayerInteractBlockEvent) {
        val player = event.player
        if (!player.isSneaking) return

        println(event.hand.name)

        val blockState = event.clickedBlock.state as? Dispenser ?: return
        val item = player.inventory.getItem(event.hand)
        val itemPdc = item.itemMeta?.persistentDataContainer ?: return
        if (!itemPdc.has(attachmentKey, PersistentDataTypeBoolean)) return

        val itemAttachmentType = itemPdc.get(attachmentTypeKey, PersistentDataTypeAttachmentType) ?: return
        val itemAttachmentTier = itemPdc.get(attachmentTierKey, PersistentDataTypeAttachmentTier) ?: return

        val blockPdc = blockState.persistentDataContainer
        if (blockPdc.has(attachmentKey, PersistentDataTypeBoolean)) {
            val blockAttachmentType = blockPdc.get(attachmentTypeKey, PersistentDataTypeAttachmentType)
            val blockAttachmentTier = blockPdc.get(attachmentTierKey, PersistentDataTypeAttachmentTier)
            if (blockAttachmentType != null && blockAttachmentTier != null) {
                val containedAttachmentItem = getAttachmentItem(blockAttachmentType, blockAttachmentTier)
                player.inventory.addItemOrDrop(player, containedAttachmentItem)
            }
        }
        blockPdc.set(attachmentKey, PersistentDataTypeBoolean, true)
        blockPdc.set(attachmentTypeKey, PersistentDataTypeAttachmentType, itemAttachmentType)
        blockPdc.set(attachmentTierKey, PersistentDataTypeAttachmentTier, itemAttachmentTier)
        blockState.update()
        item.amount--
        blockState.world.playSound(blockState.location, Sound.BLOCK_ANVIL_USE, .7f, .7f)
        event.isCancelled = true
        println("used")
    }

//    @EventHandler
//    fun onLaunch(event: BlockPreDispenseEvent) {
//        val block = event.block
//        val dispenser = block.state as? Dispenser ?: return
//        val blockPdc = dispenser.persistentDataContainer
//
//        if (!blockPdc.has(attachmentKey, PersistentDataTypeBoolean)) return
//        val attachmentType = blockPdc.get(attachmentTypeKey, PersistentDataTypeAttachmentType) ?: return
//        val attachmentTier = blockPdc.get(attachmentTierKey, PersistentDataTypeAttachmentTier) ?: return
//
//        event.isCancelled = true
//
//        val blockData = block.blockData as? Directional ?: return
//        val face = blockData.facing
//        val targetBlock = block.getRelative(face)
//        val usedItem = dispenser.inventory.getItem(event.slot) ?: return
//        val range = attachmentTier.range
//
//        val centerBlock: Block
//        val targetCenterLoc: Location
//        val rangeX: Int
//        val rangeZ: Int
//        when (face) {
//            BlockFace.DOWN, BlockFace.UP -> {
//                centerBlock = targetBlock
//                targetCenterLoc = centerBlock.location.toCenterLocation()
//                rangeX = (range - 1) * -1
//                rangeZ = (range - 1) * -1
//            }
//            else -> {
//                repeat(range - 1) {
//
//                }
//            }
//        }
//        val blockCenterLoc = block.location.toCenterLocation()
//
//        when (attachmentType) {
//            AttachmentType.SHEARS -> {
//                if (usedItem.type != Material.SHEARS) return
//                for (sheep in targetCenterLoc.getNearbyEntitiesByType(Sheep::class.java, range)) {
//                    if (sheep.isSheared) continue
//                    val color = sheep.color ?: continue
//                    val brokenShears = usedItem.damageWithBreakSound(1, blockCenterLoc)
//                    sheep.isSheared = true
//                    sheep.world.dropItem(sheep.location, ItemStack(Material.valueOf("${color.name}_WOOL"), Random.nextInt(2) + 1))
//                    sheep.world.playSound(sheep.location, Sound.ENTITY_SHEEP_SHEAR, 1f, 1f)
//                    if (brokenShears) break
//                }
//            }
//            AttachmentType.FEED -> {
//                for (animal in targetCenterLoc.getNearbyEntitiesByType(Animals::class.java, range)) {
//
//                }
//            }
//        }
//    }

    enum class AttachmentType(val modelData: Int) {
        SHEARS(10),
        FEED(20),
        BLOCK_PLACE(30),
        BLOCK_BREAK(40),
        HARVEST(50),
        CUTTING(60)
    }

    enum class AttachmentTier(val modelData: Int, val range: Int, val color: NamedTextColor) {
        WOOD(1, 1, NamedTextColor.WHITE),
        STONE(2, 3, NamedTextColor.DARK_GRAY),
        IRON(3, 5, NamedTextColor.GRAY),
        GOLD(4, 7, NamedTextColor.GOLD),
        DIAMOND(5, 9, NamedTextColor.AQUA),
        NETHERITE(6, 11, NamedTextColor.BLACK)
    }

    object PersistentDataTypeAttachmentType: PersistentDataType<ByteArray, AttachmentType> {
        override fun getPrimitiveType(): Class<ByteArray> {
            return ByteArray::class.java
        }

        override fun getComplexType(): Class<AttachmentType> {
            return AttachmentType::class.java
        }

        override fun toPrimitive(complex: AttachmentType, context: PersistentDataAdapterContext): ByteArray {
            return SerializationUtils.serialize(complex)
        }

        override fun fromPrimitive(primitive: ByteArray, context: PersistentDataAdapterContext): AttachmentType {
            try {
                val inputStream = ByteArrayInputStream(primitive)
                val objectInputStream = ObjectInputStream(inputStream)
                return objectInputStream.readObject() as AttachmentType
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return AttachmentType.SHEARS
        }
    }

    object PersistentDataTypeAttachmentTier: PersistentDataType<ByteArray, AttachmentTier> {
        override fun getPrimitiveType(): Class<ByteArray> {
            return ByteArray::class.java
        }

        override fun getComplexType(): Class<AttachmentTier> {
            return AttachmentTier::class.java
        }

        override fun toPrimitive(complex: AttachmentTier, context: PersistentDataAdapterContext): ByteArray {
            return SerializationUtils.serialize(complex)
        }

        override fun fromPrimitive(primitive: ByteArray, context: PersistentDataAdapterContext): AttachmentTier {
            try {
                val inputStream = ByteArrayInputStream(primitive)
                val objectInputStream = ObjectInputStream(inputStream)
                return objectInputStream.readObject() as AttachmentTier
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return AttachmentTier.WOOD
        }
    }
}