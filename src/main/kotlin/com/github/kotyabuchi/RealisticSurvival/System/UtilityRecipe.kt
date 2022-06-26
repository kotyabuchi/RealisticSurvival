package com.github.kotyabuchi.RealisticSurvival.System

import com.github.kotyabuchi.RealisticSurvival.Main
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Tag
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.RecipeChoice
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.inventory.ShapelessRecipe
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object UtilityRecipe: KoinComponent, Listener {

    private val main: Main by inject()

    private val shapedRecipes: List<ShapedRecipe> = listOf(
        // Logs to Chest
        ShapedRecipe(NamespacedKey(main, "CHEST"), ItemStack(Material.CHEST, 4)).run {
            shape("AAA", "A A", "AAA")
            setIngredient('A', RecipeChoice.MaterialChoice(Tag.LOGS))
            this
        },
        // Iron Horse Armor
        ShapedRecipe(NamespacedKey(main, "IRON_HORSE_ARMOR"), ItemStack(Material.IRON_HORSE_ARMOR)).run {
            shape("AAA", "ABA")
            setIngredient('A', Material.IRON_INGOT)
            setIngredient('B', Material.TRIPWIRE_HOOK)
            this
        },
        // Golden Horse Armor
        ShapedRecipe(NamespacedKey(main, "GOLDEN_HORSE_ARMOR"), ItemStack(Material.GOLDEN_HORSE_ARMOR)).run {
            shape("AAA", "ABA")
            setIngredient('A', Material.GOLD_INGOT)
            setIngredient('B', Material.TRIPWIRE_HOOK)
            this
        },
        // Diamond Horse Armor
        ShapedRecipe(NamespacedKey(main, "DIAMOND_HORSE_ARMOR"), ItemStack(Material.DIAMOND_HORSE_ARMOR)).run {
            shape("AAA", "ABA")
            setIngredient('A', Material.DIAMOND)
            setIngredient('B', Material.TRIPWIRE_HOOK)
            this
        },
        // End Rod
        ShapedRecipe(NamespacedKey(main, "END_ROD"), ItemStack(Material.END_ROD, 2)).run {
            shape("A","B","C")
            setIngredient('A', Material.IRON_INGOT)
            setIngredient('B', Material.GLOWSTONE_DUST)
            setIngredient('C', Material.PURPUR_BLOCK)
            this
        },
        // Name Tag
        ShapedRecipe(NamespacedKey(main, "NAME_TAG"), ItemStack(Material.NAME_TAG)).run {
            shape("AAB")
            setIngredient('A', Material.PAPER)
            setIngredient('B', Material.LEAD)
            this
        },
        // Repeater
        ShapedRecipe(NamespacedKey(main, "REPEATER"), ItemStack(Material.REPEATER)).run {
            shape("A A", "BAB", "CCC")
            setIngredient('A', Material.REDSTONE)
            setIngredient('B', Material.STICK)
            setIngredient('C', Material.STONE)
            this
        },
        // Dispenser
        ShapedRecipe(NamespacedKey(main, "DISPENSER"), ItemStack(Material.DISPENSER)).run {
            shape(" AB", "ACB", " AB")
            setIngredient('A', Material.STICK)
            setIngredient('B', Material.STRING)
            setIngredient('C', Material.DROPPER)
            this
        }
    )

    private val shapelessRecipes: List<ShapelessRecipe> = listOf(
        // Wheat to Seed
        ShapelessRecipe(NamespacedKey(main, "WHEAT_SEEDS"), ItemStack(Material.WHEAT_SEEDS, 2)).run {
            addIngredient(Material.WHEAT)
            this
        }
    )

    fun registerRecipe() {
        val server = main.server
        shapedRecipes.forEach {
            server.addRecipe(it)
        }
        shapelessRecipes.forEach {
            server.addRecipe(it)
        }
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player
        shapedRecipes.forEach {
            if (!player.hasDiscoveredRecipe(it.key)) player.discoverRecipe(it.key)
        }
        shapelessRecipes.forEach {
            if (!player.hasDiscoveredRecipe(it.key)) player.discoverRecipe(it.key)
        }
    }
}