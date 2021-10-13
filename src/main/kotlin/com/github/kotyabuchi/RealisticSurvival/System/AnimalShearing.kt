package com.github.kotyabuchi.RealisticSurvival.System

import com.github.kotyabuchi.RealisticSurvival.Utility.damage
import net.kyori.adventure.sound.Sound
import org.bukkit.Material
import org.bukkit.entity.Chicken
import org.bukkit.entity.Cow
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.MushroomCow
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.inventory.ItemStack

object AnimalShearing: Listener {

    @EventHandler
    fun onShearing(event: PlayerInteractEntityEvent) {
        val player = event.player
        val entity = event.rightClicked as? LivingEntity ?: return
        val item = player.inventory.getItem(event.hand) ?: return

        if (item.type != Material.SHEARS) return
        if (entity.health <= 0) return

        val dropItem = when {
            entity is Cow && entity !is MushroomCow -> {
                entity.damage(4.0, player)
                Material.LEATHER
            }
            entity is Chicken -> {
                entity.damage(1.5, player)
                Material.FEATHER
            }
            else -> return
        }
        entity.noDamageTicks = 0
        val world = entity.world
        world.dropItem(entity.location, ItemStack(dropItem))
        world.playSound(Sound.sound(org.bukkit.Sound.ENTITY_SHEEP_SHEAR.key, Sound.Source.HOSTILE, 1f, 1f))
        item.damage(1)
    }
}