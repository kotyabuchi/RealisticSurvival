package com.github.kotyabuchi.RealisticSurvival.Job

import com.github.kotyabuchi.RealisticSurvival.Event.BlockMineEvent
import com.github.kotyabuchi.RealisticSurvival.Main
import com.github.kotyabuchi.RealisticSurvival.System.Player.getStatus
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.Block
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockDropItemEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.math.floor
import kotlin.random.Random

open class GatheringJob(jobName: String): JobMaster(jobName), KoinComponent {

    private val main: Main by inject()

    private val expMap = mutableMapOf<Material, Int>()
    private val brokenBlockSet = mutableSetOf<Block>()
    open val canGetExpWithHand = true
    private val placedBlock = mutableMapOf<Block, BukkitTask>()

    fun addExpMap(exp: Int, vararg materials: Material) {
        materials.forEach {
            expMap[it] = exp
        }
    }

    fun isTargetBlock(block: Block): Boolean {
        return expMap.containsKey(block.type)
    }

    fun addBrokenBlockSet(block: Block) {
        brokenBlockSet.add(block)
    }

    fun containsBrokenBlockSet(block: Block): Boolean {
        return brokenBlockSet.contains(block)
    }

    open fun afterDropAction(event: BlockDropItemEvent) {}

    @EventHandler(priority = EventPriority.HIGH)
    fun onDropItemFromBlock(event: BlockDropItemEvent) {
        val block = event.block
        val blockState = event.blockState
        val player = event.player
        val playerStatus = player.getStatus()

        if (containsBrokenBlockSet(block)) {
            brokenBlockSet.remove(block)
            if (placedBlock.containsKey(block)) {
                placedBlock[block]?.cancel()
                placedBlock.remove(block)
            } else {
                var exp = 0.0
                val doubleDropChance = playerStatus.getJobStatus(this).getLevel() / 3
                var multiDropAmount = 1 + floor(doubleDropChance / 100.0).toInt()
                if (Random.nextInt(100) < doubleDropChance % 100) {
                    multiDropAmount++
                    player.playSound(player.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.2f, 1.0f)
                }
                val itemExp = expMap[blockState.type] ?: 1
                event.items.forEach {
                    val item = it.itemStack
                    item.amount *= multiDropAmount
                    exp += (itemExp * item.amount)
                }

                playerStatus.addSkillExp(main, this, exp, multiDropAmount)
                afterDropAction(event)
            }
        }
    }

    @EventHandler
    fun onPlace(event: BlockPlaceEvent) {
        if (event.isCancelled) return
        val block = event.block
        placedBlock[block]?.cancel()
        placedBlock[block] = object : BukkitRunnable() {
            override fun run() {
                placedBlock.remove(block)
            }
        }.runTaskLater(main, 20 * 60L)
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onBlockMine(event: BlockMineEvent) {
        if (event.isCancelled) return
        val player = event.player
        val block = event.block
        val item = player.inventory.itemInMainHand
        val toolType= item.type
        if (!isJobTool(toolType) && !canGetExpWithHand) return
        if (!isTargetBlock(block)) return
        addBrokenBlockSet(block)
    }
}