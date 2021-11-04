package com.github.kotyabuchi.RealisticSurvival.System

import com.destroystokyo.paper.event.player.PlayerJumpEvent
import com.github.kotyabuchi.RealisticSurvival.System.Player.getStatus
import com.github.kotyabuchi.RealisticSurvival.Utility.Emoji
import com.github.kotyabuchi.RealisticSurvival.Utility.floor1Digits
import com.github.kotyabuchi.RealisticSurvival.Utility.normalize
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerToggleSneakEvent
import kotlin.math.abs

object Elevator: Listener {

    private val limit = 50

    @EventHandler
    fun onJump(event: PlayerJumpEvent) {
        val player = event.player
        val block = player.location.block

        if (block.type == Material.HEAVY_WEIGHTED_PRESSURE_PLATE) {
            val downBlockType = block.getRelative(BlockFace.DOWN).type
            if (downBlockType != Material.GLASS && downBlockType != Material.IRON_BLOCK) return
            var checkBlock = block.getRelative(BlockFace.UP)
            var count = 0
            while (!checkBlock.isSolid && count < limit) {
                checkBlock = checkBlock.getRelative(BlockFace.UP)
                count++
            }
            val checkBlockType = checkBlock.type
            val upBlock = checkBlock.getRelative(BlockFace.UP)
            if (checkBlockType != Material.GLASS || upBlock.type != Material.HEAVY_WEIGHTED_PRESSURE_PLATE) return
            teleport(player, upBlock)
        }
    }

    @EventHandler
    fun onSneak(event: PlayerToggleSneakEvent) {
        if (!event.isSneaking) return
        val player = event.player
        val block = player.location.block

        if (block.type == Material.HEAVY_WEIGHTED_PRESSURE_PLATE) {
            val downBlock = block.getRelative(BlockFace.DOWN)
            val downBlockType = downBlock.type
            if (downBlockType != Material.GLASS) return
            var checkBlock = downBlock.getRelative(BlockFace.DOWN)
            var count = 0
            while (!checkBlock.isSolid && count < limit) {
                checkBlock = checkBlock.getRelative(BlockFace.DOWN)
                count++
            }
            checkBlock = checkBlock.getRelative(BlockFace.DOWN)
            val checkBlockType = checkBlock.type
            val upBlock = checkBlock.getRelative(BlockFace.UP)
            if ((checkBlockType != Material.GLASS && checkBlockType != Material.IRON_BLOCK) || upBlock.type != Material.HEAVY_WEIGHTED_PRESSURE_PLATE) return
            teleport(player, upBlock)
        }
    }

    private fun teleport(player: Player, block: Block) {
        val playerLoc = player.location
        val blockLoc = block.location
        val needMana = abs(playerLoc.y - blockLoc.y) / 5.0
        if (player.getStatus().decreaseMana(needMana)) {
            player.world.playSound(playerLoc, Sound.BLOCK_PISTON_EXTEND, 1f, 1.5f)
            playerLoc.y = blockLoc.y
            player.teleport(playerLoc)
            player.world.playSound(blockLoc, Sound.BLOCK_PISTON_EXTEND, 1f, 1.5f)
        } else {
            player.sendMessage(Component.text("Not enough mana ").normalize(NamedTextColor.RED)
                .append(Component.text("${Emoji.DIAMOND}${needMana.floor1Digits()}").normalize(NamedTextColor.AQUA)))
        }
    }
}