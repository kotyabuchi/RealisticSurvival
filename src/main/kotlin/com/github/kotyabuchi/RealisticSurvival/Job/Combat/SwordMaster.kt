package com.github.kotyabuchi.RealisticSurvival.Job.Combat

import com.github.kotyabuchi.RealisticSurvival.Item.Enum.ToolType

object SwordMaster: WeaponCombatJob("SWORD_MASTER") {
    override val weaponType: ToolType = ToolType.SWORD
    override val needWeapon: Boolean = true
}