package com.github.kotyabuchi.RealisticSurvival.Job.Gathering

import com.github.kotyabuchi.RealisticSurvival.Skill.Gathering.MineAssist
import com.github.kotyabuchi.RealisticSurvival.Skill.Gathering.MultiBreakMiner
import com.github.kotyabuchi.RealisticSurvival.Skill.PassiveSkill.Gathering.GemCollector
import com.github.kotyabuchi.RealisticSurvival.Skill.PassiveSkill.Gathering.HadesBlessing
import com.github.kotyabuchi.RealisticSurvival.Skill.PassiveSkill.Gathering.StoneReplacer
import com.github.kotyabuchi.RealisticSurvival.Skill.PassiveSkill.Gathering.TunnelAssist
import com.github.kotyabuchi.RealisticSurvival.Skill.SkillCommand
import org.bukkit.Material

object Miner: GatheringJob("MINER") {
    override val canGetExpWithHand: Boolean = false
    private val stoneSet = mutableSetOf(
        Material.STONE, Material.GRANITE, Material.DIORITE, Material.ANDESITE, Material.NETHERRACK,
        Material.SANDSTONE, Material.SMOOTH_SANDSTONE, Material.RED_SANDSTONE,
        Material.WHITE_TERRACOTTA, Material.ORANGE_TERRACOTTA, Material.MAGENTA_TERRACOTTA, Material.LIGHT_BLUE_TERRACOTTA, Material.YELLOW_TERRACOTTA, Material.LIME_TERRACOTTA,
        Material.PINK_TERRACOTTA, Material.GRAY_TERRACOTTA, Material.LIGHT_GRAY_TERRACOTTA, Material.CYAN_TERRACOTTA, Material.PURPLE_TERRACOTTA, Material.BLUE_TERRACOTTA,
        Material.BROWN_TERRACOTTA, Material.GREEN_TERRACOTTA, Material.RED_TERRACOTTA, Material.BLACK_TERRACOTTA, Material.TERRACOTTA,
        Material.CRIMSON_NYLIUM, Material.WARPED_NYLIUM, Material.MOSSY_COBBLESTONE, Material.BASALT, Material.BLACKSTONE,
        Material.DEEPSLATE, Material.CALCITE, Material.TUFF)

    init {

        Material.values().forEach {
            if (it.name.endsWith("_PICKAXE")) addTool(it)
        }
        stoneSet.forEach {
            addExpMap(1, it)
        }
        addExpMap(2, Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE)
        addExpMap(3, Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE, Material.COPPER_ORE, Material.DEEPSLATE_COPPER_ORE, Material.END_STONE, Material.GLOWSTONE)
        addExpMap(4, Material.REDSTONE_ORE, Material.DEEPSLATE_REDSTONE_ORE, Material.NETHER_QUARTZ_ORE)
        addExpMap(5, Material.GOLD_ORE, Material.DEEPSLATE_GOLD_ORE, Material.OBSIDIAN, Material.NETHER_GOLD_ORE)
        addExpMap(7, Material.LAPIS_ORE, Material.DEEPSLATE_LAPIS_ORE)
        addExpMap(8, Material.DIAMOND_ORE, Material.DEEPSLATE_DIAMOND_ORE)
        addExpMap(10, Material.EMERALD_ORE, Material.DEEPSLATE_EMERALD_ORE)

        registerSkill(SkillCommand.LRL, MineAssist)
        registerSkill(SkillCommand.RLR, MultiBreakMiner)
        registerPassiveSkill(GemCollector(this))
        registerPassiveSkill(StoneReplacer(this))
        registerPassiveSkill(TunnelAssist(this))
        registerPassiveSkill(HadesBlessing(this))
    }
}