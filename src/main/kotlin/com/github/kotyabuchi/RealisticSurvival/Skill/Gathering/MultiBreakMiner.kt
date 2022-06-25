package com.github.kotyabuchi.RealisticSurvival.Skill.Gathering

import com.github.kotyabuchi.RealisticSurvival.Item.Enum.ToolType

object MultiBreakMiner: MultiBreak() {
    override val skillName: String = "MULTI_BREAK_MINER"
    override val targetToolType: ToolType = ToolType.PICKAXE
}