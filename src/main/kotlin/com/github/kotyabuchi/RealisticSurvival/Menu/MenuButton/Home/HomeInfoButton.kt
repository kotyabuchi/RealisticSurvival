package com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.Home

import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.ButtonItem
import com.github.kotyabuchi.RealisticSurvival.Menu.MenuButton.MenuButton
import com.github.kotyabuchi.RealisticSurvival.System.Player.Home
import com.github.kotyabuchi.RealisticSurvival.System.Player.getStatus
import com.github.kotyabuchi.RealisticSurvival.Utility.Emoji
import com.github.kotyabuchi.RealisticSurvival.Utility.consume
import com.github.kotyabuchi.RealisticSurvival.Utility.normalize
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import kotlin.math.round
import kotlin.random.Random

class HomeInfoButton(val player: Player, val home: Home): MenuButton() {
    private val location = Location(home.world, home.x, home.y, home.z, home.yaw, 0f)
    private val distanceCost = round(player.location.toVector().distance(location.toVector()) / 3).toInt()
    private var cost = distanceCost
    private var pearCost = distanceCost / 2

    init {
        if (home.world != player.world) {
            cost += 100
            pearCost += 100
        }
        val lore = mutableListOf<Component>()
        lore.add(Component.text("World: ${location.world?.name}").normalize())
        lore.add(Component.text("X: ${location.x}").normalize())
        lore.add(Component.text("Y: ${location.y}").normalize())
        lore.add(Component.text("Z: ${location.z}").normalize())
        lore.add(Component.text("Yaw: ${location.yaw}").normalize())
        lore.add(Component.text("Cost: ").normalize().append(Component.text("${Emoji.DIAMOND}$cost($pearCost)").normalize(NamedTextColor.AQUA)))
        lore.add(Component.empty())
        lore.add(Component.text("Left Click: Teleport to location").normalize(NamedTextColor.GOLD))
        lore.add(Component.text("Right Click: Remove home").normalize(NamedTextColor.RED))
        menuIcon = ButtonItem(home.icon, Component.text(home.name), lore = lore)
    }

    override fun clickEvent(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        val playerStatus = player.getStatus()
        val world = player.world
        val inv = player.inventory
        val teleportCost = if (inv.contains(ItemStack(Material.ENDER_PEARL))) pearCost else cost

        if (playerStatus.decreaseMana(teleportCost)) {
            inv.consume(ItemStack(Material.ENDER_PEARL))
            playerStatus.refreshManaIndicator()
            playerStatus.closeMenu()
            for (i in 0 until 20) {
                val x = Random.nextInt(15) / 10.0 - .75
                val y = Random.nextInt(20) / 10.0 - 1
                val z = Random.nextInt(15) / 10.0 - .75
                world.spawnParticle(Particle.SPELL, player.location.clone().add(.0, 1.0, .0).add(x, y, z), 20)
                world.spawnParticle(Particle.PORTAL, player.location.clone().add(.0, 1.0, .0).add(x, y, z), 20)
            }
            world.playSound(player.location, Sound.ENTITY_ENDERMAN_TELEPORT, .4f, .6f)
            if (player.teleport(location)) {
                for (i in 0 until 20) {
                    val x = Random.nextInt(15) / 10.0 - .75
                    val y = Random.nextInt(20) / 10.0 - 1
                    val z = Random.nextInt(15) / 10.0 - .75
                    world.spawnParticle(Particle.SPELL, location.clone().add(.0, 1.0, .0).add(x, y, z), 20)
                    world.spawnParticle(Particle.PORTAL, location.clone().add(.0, 1.0, .0).add(x, y, z), 20)
                }
                world.playSound(location, Sound.ENTITY_ENDERMAN_TELEPORT, .4f, .6f)
            } else {
                playerStatus.increaseMana(cost)
                player.sendMessage(Component.text("テレポートに失敗しました").normalize(NamedTextColor.RED))
            }
        } else {
            player.sendMessage(Component.text("マナが足りません").normalize(NamedTextColor.RED))
        }
    }
}