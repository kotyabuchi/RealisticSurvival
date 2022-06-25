package com.github.kotyabuchi.RealisticSurvival.Job.Combat

import com.github.kotyabuchi.RealisticSurvival.Item.Enum.ToolType

object BattleAxe: WeaponCombatJob("BATTLE_AXE") {
    override val weaponType: ToolType = ToolType.BATTLEAXE
    override val needWeapon: Boolean = true
}