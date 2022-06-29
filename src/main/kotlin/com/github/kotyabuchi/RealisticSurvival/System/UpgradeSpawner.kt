package com.github.kotyabuchi.RealisticSurvival.System

import com.github.kotyabuchi.RealisticSurvival.Event.PlayerInteractBlockEvent
import com.github.kotyabuchi.RealisticSurvival.Utility.floor1Digits
import com.github.kotyabuchi.RealisticSurvival.Utility.sendSuccessMessage
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.CreatureSpawner
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

object UpgradeSpawner: Listener {

    @EventHandler
    fun onUpgrade(event: PlayerInteractBlockEvent) {
        val player = event.player
        val block = event.clickedBlock
        val spawner = block.state as? CreatureSpawner ?: return
        val item = player.inventory.getItem(event.hand)

        when (item.type) {
            Material.DIAMOND -> {
                if (spawner.minSpawnDelay <= 20) return
                item.amount--
                spawner.maxSpawnDelay -= 40
                spawner.minSpawnDelay -= 10
                player.sendSuccessMessage("最長スポーン時間 -2秒(${spawner.maxSpawnDelay / 20}秒)")
                player.sendSuccessMessage("最短スポーン時間 -0.5秒(${(spawner.minSpawnDelay / 20.0).floor1Digits()}秒)")
            }
//            Material.GOLD_INGOT -> {
//                if (spawner.spawnCount >= 10) return
//                if (item.amount < 10) return
//                spawner.spawnCount++
//                item.amount -= 10
//            }
            Material.AMETHYST_SHARD -> {
                if (spawner.maxNearbyEntities >= 50) return
                if (item.amount < 10) return
                spawner.maxNearbyEntities += 2
                item.amount -= 10
                player.sendSuccessMessage("最大モンスター数 +2体(${spawner.maxNearbyEntities}体)")
            }
            else -> return
        }
        spawner.update()
        block.world.playSound(block.location, Sound.BLOCK_ANVIL_USE,.8f, .7f)
    }
}