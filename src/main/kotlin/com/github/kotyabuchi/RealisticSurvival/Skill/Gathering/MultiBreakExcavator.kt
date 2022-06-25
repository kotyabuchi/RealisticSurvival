package com.github.kotyabuchi.RealisticSurvival.Skill.Gathering

import com.github.kotyabuchi.RealisticSurvival.Item.Enum.ToolType

object MultiBreakExcavator: MultiBreak() {
    override val skillName: String = "MULTI_BREAK_EXCAVATOR"
    override val targetToolType: ToolType = ToolType.SHOVEL
}