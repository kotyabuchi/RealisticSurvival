package com.github.kotyabuchi.RealisticSurvival.Skill

import com.github.kotyabuchi.RealisticSurvival.Event.BlockMineEvent
import com.github.kotyabuchi.RealisticSurvival.Main
import com.github.kotyabuchi.RealisticSurvival.Utility.*
import com.github.kotyabuchi.RealisticSurvival.Utility.Enum.WoodType
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.data.type.Leaves
import org.bukkit.entity.FallingBlock
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityChangeBlockEvent
import org.bukkit.event.entity.EntityDropItemEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import org.koin.core.component.inject
import java.util.*
import kotlin.random.Random

object TreeAssist: ToolLinkedSkill {
    override val main: Main by inject()
    override val skillName: String = "TreeAssist"
    override val cost: Int = 0
    override val needLevel: Int = 0
    override val description: String = "繋がった木を一括破壊する"

    override val skillItemBackup: MutableMap<UUID, ItemStack> = mutableMapOf()
    override val coolTime: Long = 0
    override val hasActiveTime: Boolean = true
    override val activeTimeMap: MutableMap<UUID, BukkitTask> = mutableMapOf()
    override val lastUseTime: MutableMap<UUID, Long> = mutableMapOf()

    override fun calcActiveTime(level: Int): Int = 20 * 6

    @EventHandler
    fun onSwitch(event: PlayerSwapHandItemsEvent) {
        val player = event.player
        val item = event.offHandItem ?: return
        if (!item.type.isAxe()) return
        event.isCancelled = true
        toggleSkill(player, 1)
    }

    @EventHandler
    fun onBreak(event: BlockBreakEvent) {
        if (event is BlockMineEvent) return
        if (event.isCancelled) return

        val player = event.player
        val block = event.block
        val itemStack = player.inventory.itemInMainHand
        val material = block.type

        if (!material.isWood()) return
        if (!itemStack.type.isAxe()) return
        if (!isEnabledSkill(player)) return
        restartActiveTime(player, 1)

        val woodList: MutableList<Block> = mutableListOf()
        val leaveList: MutableList<Block> = mutableListOf()

        val woodType = material.getWoodType()
        searchWood(block, woodType, block, woodList, leaveList, mutableListOf())
        leaveList.sortWith { o1, o2 -> o1.y - o2.y }

        woodList.forEach {
            it.miningWithEvent(main, player, itemStack, block)
        }
        itemStack.damage(woodList.size)

        if (leaveList.isNotEmpty()) {
            val lowestLeave = leaveList[0].y
            leaveList.forEach {
                object : BukkitRunnable() {
                    override fun run() {
                        block.world.spawnFallingBlock(it.location.add(0.5, 0.0, 0.5), it.blockData)
                        it.type = Material.AIR
                    }
                }.runTaskLater(main, it.y - lowestLeave.toLong())
            }
        }
    }

    @EventHandler
    fun onLand(event: EntityChangeBlockEvent) {
        val entity = event.entity as? FallingBlock ?: return
        val material = entity.blockData.material
        if (material.isLeave()) {
            dropFromLeave(entity)
            event.isCancelled = true
            entity.remove()
        }
    }

    @EventHandler
    fun onDropItemFromEntity(event: EntityDropItemEvent) {
        val entity = event.entity as? FallingBlock ?: return
        val material = entity.blockData.material
        if (material.isLeave()) {
            dropFromLeave(entity)
            event.isCancelled = true
            entity.remove()
        }
    }

    private fun dropFromLeave(entity: FallingBlock) {
        val material = entity.blockData.material
        if (Random.nextInt(100) < 5) {
            val item = entity.world.dropItem(entity.location, ItemStack(Material.valueOf(material.name.replace("LEAVES", "SAPLING"))))
            main.server.pluginManager.callEvent(EntityDropItemEvent(entity, item))
        }
        if (Random.nextInt(1000) < 15) {
            val item = entity.world.dropItem(entity.location, ItemStack(Material.STICK, Random.nextInt(2) + 1))
            main.server.pluginManager.callEvent(EntityDropItemEvent(entity, item))
        }
        if (material.name.contains("OAK") && Random.nextInt(1000) < 5) {
            val item = entity.world.dropItem(entity.location, ItemStack(Material.APPLE, 1))
            main.server.pluginManager.callEvent(EntityDropItemEvent(entity, item))
        }
    }

    fun searchWood(mainBlock: Block, woodType: WoodType, checkBlock: Block, woodList: MutableList<Block>, leaveList: MutableList<Block>, checkedList: MutableList<Block>) {
        if (checkedList.contains(checkBlock)) return
        checkedList.add(checkBlock)
        if (mainBlock.location.y > checkBlock.location.y) return
        val checkMaterial = checkBlock.type
        if (!checkMaterial.isWood()) {
            if (checkMaterial.isLeave()) searchLeave(
                mainBlock,
                checkBlock,
                checkMaterial.getWoodType(),
                leaveList,
                0
            )
            return
        }
        if (woodType != checkMaterial.getWoodType()) return
        woodList.add(checkBlock)
        if (checkedList.size > 496) return
        val upBlock = checkBlock.getRelative(BlockFace.UP)
        val downBlock = checkBlock.getRelative(BlockFace.DOWN)
        BlockUtil.aroundBlockFace.forEach {
            searchWood(mainBlock, woodType, upBlock.getRelative(it), woodList, leaveList, checkedList)
        }
        BlockUtil.aroundBlockFace.forEach {
            searchWood(mainBlock, woodType, checkBlock.getRelative(it), woodList, leaveList, checkedList)
        }
        BlockUtil.aroundBlockFace.forEach {
            searchWood(mainBlock, woodType, downBlock.getRelative(it), woodList, leaveList, checkedList)
        }
    }

    private fun searchLeave(beforeBlock: Block, checkBlock: Block, woodType: WoodType, leaveList: MutableList<Block>, _count: Int) {
        var count = _count
        if (leaveList.contains(checkBlock)) return
        val checkMaterial = checkBlock.type
        if (!checkMaterial.isLeave()) return
        val leaves = checkBlock.blockData as Leaves
        if (checkMaterial.getWoodType() != woodType) return
        if (leaves.isPersistent) return
        if (beforeBlock.type.isLeave() && (beforeBlock.blockData as Leaves).distance >= leaves.distance) return
        leaveList.add(checkBlock)
        count++
        if (count > 496) return
        val upBlock = checkBlock.getRelative(BlockFace.UP)
        val downBlock = checkBlock.getRelative(BlockFace.DOWN)
        BlockUtil.aroundBlockFace.forEach {
            searchLeave(checkBlock, upBlock.getRelative(it), woodType, leaveList, count)
        }
        BlockUtil.aroundBlockFace.forEach {
            searchLeave(checkBlock, checkBlock.getRelative(it), woodType, leaveList, count)
        }
        BlockUtil.aroundBlockFace.forEach {
            searchLeave(checkBlock, downBlock.getRelative(it), woodType, leaveList, count)
        }
    }
}