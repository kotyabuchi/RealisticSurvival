package com.github.kotyabuchi.RealisticSurvival.Skill.Gathering

import com.github.kotyabuchi.RealisticSurvival.Event.BlockMineEvent
import com.github.kotyabuchi.RealisticSurvival.Main
import com.github.kotyabuchi.RealisticSurvival.Skill.ToolLinkedSkill
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
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import org.koin.core.component.inject
import java.util.*
import kotlin.math.ceil
import kotlin.math.max
import kotlin.random.Random

object TreeAssist: ToolLinkedSkill {
    override val main: Main by inject()
    override val skillName: String = "TREE_ASSIST"
    override val displayName: String = "Tree Assist"
    override val needLevel: Int = 0
    override val description: String = "原木を破壊した際に繋がった原木もまとめて伐採する"

    override val skillItemBackup: MutableMap<UUID, ItemStack> = mutableMapOf()
    override val coolTime: Long = 0
    override val hasActiveTime: Boolean = true
    override val activeTimeMap: MutableMap<UUID, BukkitTask> = mutableMapOf()
    override val lastUseTime: MutableMap<UUID, Long> = mutableMapOf()

    override fun calcActiveTime(level: Int): Int = 20 * 6

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
        event.isCancelled = true

        restartActiveTime(player, 1)

        val woodList: MutableList<Block> = mutableListOf()
        val leaveList: MutableList<Block> = mutableListOf()

        val woodType = material.getWoodType()
        val foundRoot = searchRoot(block, woodType, mutableListOf()) ?: return
        searchWood(foundRoot, woodType, foundRoot, woodList, leaveList, mutableListOf())
        leaveList.sortWith { o1, o2 -> o1.y - o2.y }

        woodList.forEach {
            it.miningWithEvent(main, player, itemStack, block, false)
        }
        player.foodLevel = max(0, player.foodLevel - ceil(woodList.size / 20.0).toInt())
        itemStack.damage(player, woodList.size)

        if (leaveList.isNotEmpty()) {
            val lowestLeave = leaveList[0].y
            leaveList.forEach {
                object : BukkitRunnable() {
                    override fun run() {
                        val fallingBlock = block.world.spawnFallingBlock(it.location.add(0.5, 0.0, 0.5), it.blockData)
                        fallingBlock.dropItem = false
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
            if (event.to == entity.blockData.material) {
                val block = event.block
                block.type = event.to
                block.drops.forEach {
                    val item = entity.world.dropItem(entity.location, it)
                    main.server.pluginManager.callEvent(EntityDropItemEvent(entity, item))
                }
                block.type = Material.AIR
//            dropFromLeave(entity)
            }
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

    private fun searchRoot(checkBlock: Block, woodType: WoodType, checkedList: MutableList<Block>): Block? {
        if (checkedList.contains(checkBlock)) return null
        checkedList.add(checkBlock)
        val checkMaterial = checkBlock.type
        if (!checkMaterial.isWood()) return null
        if (woodType != checkMaterial.getWoodType()) return null
        if (checkedList.size > 496) return null
        val downBlock = checkBlock.getRelative(BlockFace.DOWN)
        if (downBlock.type.isDirt()) return checkBlock

        var result: Block? = null
        for (face in BlockUtil.aroundBlockFace) {
            result = searchRoot(downBlock.getRelative(face), woodType, checkedList)
            if (result != null) break
        }
        if (result == null) {
            for (face in BlockUtil.aroundBlockFace) {
                result = searchRoot(checkBlock.getRelative(face), woodType, checkedList)
                if (result != null) break
            }
        }
        return result
    }

    private fun searchWood(mainBlock: Block, woodType: WoodType, checkBlock: Block, woodList: MutableList<Block>, leaveList: MutableList<Block>, checkedList: MutableList<Block>) {
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