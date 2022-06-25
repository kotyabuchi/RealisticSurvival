package com.github.kotyabuchi.RealisticSurvival.Monster

import org.bukkit.Color
import org.bukkit.entity.EntityType

object AdvZombie: HelmetMob(
    mapOf(EntityType.ZOMBIE to Color.fromRGB(78, 123, 54),
        EntityType.ZOMBIE_VILLAGER to Color.fromRGB(78, 123, 54),
        EntityType.DROWNED to Color.fromRGB(96, 152, 146)
    ),
    EntityType.ZOMBIE, EntityType.ZOMBIE_VILLAGER, EntityType.DROWNED)