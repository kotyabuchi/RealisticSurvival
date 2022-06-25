package com.github.kotyabuchi.RealisticSurvival.Utility

import org.bukkit.Material
import org.bukkit.entity.*

fun LivingEntity.isUndead(): Boolean {
    return this is Zombie ||
            this is Skeleton
}

fun Breedable.getFoods(): List<Material> {
    return when (this) {
        is Horse, is Donkey -> listOf(Material.GOLDEN_APPLE, Material.ENCHANTED_GOLDEN_APPLE, Material.GOLDEN_CARROT)
        is Pig -> listOf(Material.CARROT, Material.POTATO, Material.BEETROOT)
        is Chicken -> listOf(Material.WHEAT_SEEDS, Material.PUMPKIN_SEEDS, Material.BEETROOT_SEEDS)
        is Wolf -> listOf(
            Material.BEEF, Material.COOKED_BEEF, Material.CHICKEN, Material.COOKED_CHICKEN, Material.PORKCHOP, Material.COOKED_PORKCHOP,
            Material.MUTTON, Material.COOKED_MUTTON, Material.RABBIT, Material.COOKED_RABBIT, Material.ROTTEN_FLESH)
        is Cat, is Ocelot -> listOf(Material.COD, Material.SALMON)
        is Axolotl -> listOf(Material.TROPICAL_FISH_BUCKET)
        is Llama -> listOf(Material.HAY_BLOCK)
        is Rabbit -> listOf(Material.CARROT)
        is Turtle -> listOf(Material.SEAGRASS)
        is Panda -> listOf(Material.BAMBOO)
        is Fox -> listOf(Material.SWEET_BERRIES, Material.GLOW_BERRIES)
        is Bee -> listOf(
            Material.DANDELION, Material.POPPY, Material.BLUE_ORCHID, Material.ALLIUM, Material.AZURE_BLUET,
            Material.RED_TULIP, Material.PINK_TULIP, Material.WHITE_TULIP, Material.ORANGE_TULIP,
            Material.OXEYE_DAISY, Material.CORNFLOWER, Material.LILY_OF_THE_VALLEY, Material.WITHER_ROSE,
            Material.SUNFLOWER, Material.LILAC, Material.ROSE_BUSH, Material.PEONY)
        is Strider -> listOf(Material.WARPED_FUNGUS)
        is Hoglin -> listOf(Material.CRIMSON_FUNGUS)
        else -> listOf(Material.WHEAT)
    }

}