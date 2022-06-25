package com.github.kotyabuchi.RealisticSurvival.Event

import com.github.kotyabuchi.RealisticSurvival.Job.Gathering.GatheringJob
import org.bukkit.block.Block
import org.bukkit.entity.Player

class GatheringEvent(player: Player, gatheringJob: GatheringJob, val block: Block): JobActionEvent(player, gatheringJob) {
}