package com.github.kotyabuchi.RealisticSurvival.Monster

import com.github.kotyabuchi.RealisticSurvival.Monster.Action.FightingAction
import com.github.kotyabuchi.RealisticSurvival.Monster.CustomMonster.CursedEye
import com.github.kotyabuchi.RealisticSurvival.Monster.CustomMonster.PetOwner
import org.bukkit.NamespacedKey
import org.bukkit.entity.EntityType

object AdvEnderman: AdvancedMonster(EntityType.ENDERMAN), PetOwner {

    override val maxPetAmount: Int = 3
    override val petKey: NamespacedKey = NamespacedKey(main, "PETS")

    init {
        addFightingAction(FightingAction(main, 20L, .2) { mob ->
            if (canSummonPet(mob)) {
                val cursedEye = CursedEye.spawn(mob) ?: return@FightingAction
                addPet(mob, cursedEye)
            }
        })
    }
}