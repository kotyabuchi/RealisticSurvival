package com.github.kotyabuchi.RealisticSurvival.Skill.Gathering

import com.destroystokyo.paper.ParticleBuilder
import com.github.kotyabuchi.RealisticSurvival.Main
import com.github.kotyabuchi.RealisticSurvival.Skill.Skill
import com.github.kotyabuchi.RealisticSurvival.System.SafeCropAndReplant
import com.github.kotyabuchi.RealisticSurvival.Utility.ParticleUtil
import com.github.kotyabuchi.RealisticSurvival.Utility.miningWithEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.block.Block
import org.bukkit.block.data.Ageable
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector
import org.koin.core.component.inject
import java.util.*
import kotlin.random.Random

object BlessOfDemeter: Skill {
    override val main: Main by inject()
    override val skillName: String = "TOTEM_OF_DEMETER"
    override val displayName: String = "Totem of Demeter"
    override val cost: Int = 35
    override val needLevel: Int = 20
    override val description: String = "周囲の植物の成長を促進するトーテムをその場に設置する"

    override val coolTime: Long = 1000 * 60 * 5
    override val lastUseTime: MutableMap<UUID, Long> = mutableMapOf()

    private val circleLocation = ParticleUtil.circleLocations(4.2)

    override fun enableAction(player: Player, level: Int) {
        player.world.playSound(player.eyeLocation, Sound.BLOCK_BEACON_POWER_SELECT, 1f, 1.3f)
        player.sendActionBar(Component.text(skillName, NamedTextColor.GREEN))
        val item = player.inventory.itemInMainHand
        val baseBlock = player.location.block
        val location = baseBlock.location.toCenterLocation().clone()
        val targetBlocks = mutableListOf<Block>()
        val distance = 4

        for (x in distance * -1 .. distance) {
            for (y in -1 .. 1) {
                for (z in distance * -1 .. distance) {
                    val checkBlock = baseBlock.getRelative(x, y, z)
                    if (checkBlock.blockData is Ageable) {
                        val checkLoc = checkBlock.location.toCenterLocation()
                        if (location.distance(checkLoc) <= 4.1) targetBlocks.add(checkBlock)
                    }
                }
            }
        }

        val totemItem = player.world.dropItem(location, ItemStack(Material.WATER_BUCKET)) {
            it.setGravity(false)
            it.setCanPlayerPickup(false)
            it.setCanMobPickup(false)
            it.velocity = Vector(.0, .0, .0)
        }
        // totem effect
        val particleLocations = mutableListOf<Location>()
        circleLocation.forEach {
            particleLocations.add(location.clone().add(it.first, .0, it.second))
        }
        val totemAreaParticle = ParticleBuilder(Particle.COMPOSTER)
            .location(particleLocations.first())
            .offset(.0, .5, .0)
            .extra(.0)
            .receivers(30)
            .count(5)
        object : BukkitRunnable() {
            var count = 0
            override fun run() {
                if (count > 4 * 60) {
                    cancel()
                } else {
                    particleLocations.forEach {
                        totemAreaParticle.location(it).spawn()
                    }
                }
                count++
            }
        }.runTaskTimer(main, 0, 5)

        // glow plant
        val particleBuilder = ParticleBuilder(Particle.TOTEM)
            .location(location)
            .offset(.0, .2, .0)
            .extra(.15)
            .receivers(30)
            .count(20)
        object : BukkitRunnable() {
            var count = 0
            override fun run() {
                if (count > 10 * 60) {
                    cancel()
                    totemItem.remove()
                } else {
                    val checkBlocks = targetBlocks.toMutableList()
                    if (checkBlocks.isNotEmpty()) {
                        for (i in 0 .. targetBlocks.size) {
                            val checkBlock = checkBlocks[Random.nextInt(checkBlocks.size)]
                            val blockData = checkBlock.blockData
                            if (blockData !is Ageable) continue
                            if (blockData.age == blockData.maximumAge) {
                                SafeCropAndReplant.addHarvestBlock(checkBlock)
                                checkBlock.miningWithEvent(main, player, item)
                            } else {
                                blockData.age++
                                checkBlock.blockData = blockData
                            }
                            particleBuilder.location(checkBlock.location.toCenterLocation()).spawn()
                            break
                        }
                    }
                }
                count++
            }
        }.runTaskTimer(main, 0, 2)
    }
}