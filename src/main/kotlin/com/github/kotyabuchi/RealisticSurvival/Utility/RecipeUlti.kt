package com.github.kotyabuchi.RealisticSurvival.Utility

import com.github.kotyabuchi.RealisticSurvival.Main
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.Recipe
import org.bukkit.inventory.RecipeChoice
import org.bukkit.inventory.ShapedRecipe
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object RecipeUlti: KoinComponent {

    private val main: Main by inject()

    fun registerRecipe(recipe: Recipe) {
        main.server.addRecipe(recipe)
    }

    fun test(recipeKey: NamespacedKey, resultItem: ItemStack) {
        val shapedRecipe = ShapedRecipe(recipeKey, resultItem)
        shapedRecipe.choiceMap['A'] = RecipeChoice.ExactChoice()
    }

    class CustomRecipe<T: Recipe> {

    }
}