package com.github.kotyabuchi.RealisticSurvival.System.Item

import com.github.kotyabuchi.RealisticSurvival.Utility.getWoodType
import org.bukkit.Material
import org.bukkit.Tag
import org.bukkit.entity.FallingBlock
import org.bukkit.entity.Item
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityChangeBlockEvent
import org.bukkit.inventory.ItemStack
import kotlin.math.min

object AnvilPressCraft: Listener {

    private val recipes: MutableSet<AnvilPressRecipe> = mutableSetOf(
        AnvilPressRecipe(ItemStack(Material.COBBLESTONE)).addIngredients(ItemStack(Material.STONE)),
        AnvilPressRecipe(ItemStack(Material.GRAVEL)).addIngredients(ItemStack(Material.COBBLESTONE)),
        AnvilPressRecipe(ItemStack(Material.DIRT)).addIngredients(ItemStack(Material.GRAVEL)),
        AnvilPressRecipe(ItemStack(Material.SAND)).addIngredients(ItemStack(Material.DIRT)),
        AnvilPressRecipe(ItemStack(Material.COBBLED_DEEPSLATE)).addIngredients(ItemStack(Material.DEEPSLATE)),
        AnvilPressRecipe(ItemStack(Material.AMETHYST_SHARD, 4)).addIngredients(ItemStack(Material.AMETHYST_BLOCK)),
        AnvilPressRecipe(ItemStack(Material.GLOWSTONE_DUST, 4)).addIngredients(ItemStack(Material.GLOWSTONE)),
        AnvilPressRecipe(ItemStack(Material.QUARTZ, 4)).addIngredients(ItemStack(Material.QUARTZ_BLOCK)),
        AnvilPressRecipe(ItemStack(Material.BLAZE_POWDER, 3)).addIngredients(ItemStack(Material.BLAZE_ROD))
    )

    init {
        Tag.LOGS.values.forEach {
            recipes.add(AnvilPressRecipe(ItemStack(Material.valueOf(it.getWoodType().name + "_PLANKS"), 6)).addIngredients(ItemStack(it)))
        }
        Tag.WOOL.values.forEach {
            recipes.add(AnvilPressRecipe(ItemStack(Material.STRING, 4)).addIngredients(ItemStack(it)))
        }
    }

    @EventHandler
    fun onPress(event: EntityChangeBlockEvent) {
        if (event.isCancelled) return
        val block = event.block
        val fallingBlock = event.entity as? FallingBlock ?: return
        if (!fallingBlock.blockData.material.name.endsWith("ANVIL")) return
        val itemStacks = block.location.add(.5, .0, .5).getNearbyEntitiesByType(Item::class.java, .5).map { it.itemStack }
        recipes.forEach { recipe ->
            while (recipe.matchIngredient(itemStacks)) {
                recipe.getIngredients().forEach { ingredient ->
                    var needAmount = ingredient.amount
                    for (itemStack in itemStacks) {
                        if (ingredient.isSimilar(itemStack)) {
                            val consumeAmount = min(itemStack.amount, needAmount)
                            itemStack.amount -= consumeAmount
                            needAmount -= consumeAmount
                            if (needAmount <= 0) break
                        }
                    }
                }
                recipe.results.forEach {
                    block.world.dropItem(fallingBlock.location, it)
                }
            }
        }
    }

    class AnvilPressRecipe(vararg val results: ItemStack) {
        private val ingredients: MutableSet<ItemStack> = mutableSetOf()

        fun addIngredients(ingredient: ItemStack): AnvilPressRecipe {
            ingredients.add(ingredient)
            return this
        }

        fun getIngredients(): Set<ItemStack> = ingredients

        fun matchIngredient(_itemStacks: List<ItemStack>): Boolean {
            val itemStacks = _itemStacks.toMutableList()
            var match = true
            for (ingredient in ingredients) {
                var found = false
                for (itemStack in itemStacks) {
                    if (ingredient.isSimilar(itemStack) && itemStack.amount >= ingredient.amount) {
                        itemStacks.remove(itemStack)
                        found = true
                        break
                    }
                }
                if (!found) {
                    match = false
                    break
                }
            }
            return match
        }
    }
}