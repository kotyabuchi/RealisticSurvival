package com.github.kotyabuchi.RealisticSurvival.System.Player

import com.github.kotyabuchi.RealisticSurvival.Job.JobMaster
import com.github.kotyabuchi.RealisticSurvival.Main
import com.github.kotyabuchi.RealisticSurvival.Menu.Menu
import com.github.kotyabuchi.RealisticSurvival.Utility.*
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.title.Title
import org.bukkit.Sound
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import java.time.Duration
import kotlin.math.max
import kotlin.math.min

data class PlayerStatus(val player: Player) {

    var maxMana: Int = 10
        set(value) {
            field = value
            if (mana > value) mana = value
        }
    var mana: Int = 10
        set(value) {
            field = min(maxMana, value)
        }
    private val manaIndicator: BossBar = BossBar.bossBar(Component.text("Mana $mana / $maxMana"), max(0f, min(1f, mana / maxMana.toFloat())), BossBar.Color.BLUE, BossBar.Overlay.PROGRESS)

    private var openingMenu: Menu? = null
    private var openingMenuPage: Int = 0
    var openMenuWithCloseMenu = false

    val homes = mutableListOf<Home>()

    private val jobStatusMap = mutableMapOf<JobMaster, JobStatus>()
    private val expBarMap = mutableMapOf<JobMaster, BukkitTask>()

    fun closeMenu() {
        openingMenu?.doCloseMenuAction(player)
        openingMenuPage = 0
        openingMenu = null
    }

    fun setOpeningMenu(menu: Menu) {
        openingMenu = menu
    }

    fun getOpeningMenu(): Menu? = openingMenu

    fun getOpeningPage(): Int = openingMenuPage

    fun backPage() {
        openingMenu?.let {
            openMenu(it, openingMenuPage - 1)
        }
    }

    fun nextPage() {
        openingMenu?.let {
            openMenu(it, openingMenuPage + 1)
        }
    }

    fun openMenu(menu: Menu, page: Int = 0, prev: Boolean = false) {
        if (openingMenu != null && openingMenu != menu) {
            if (!prev) menu.setPrevMenu(openingMenu!!)
            openMenuWithCloseMenu = true
        }
        player.openInventory(menu.getInventory(page))
        openingMenu = menu
        openingMenuPage = page
    }

    fun increaseMaxMana(amount: Int): PlayerStatus {
        maxMana += amount
        return this
    }

    fun increaseMana(amount: Int): PlayerStatus {
        mana += amount
        return this
    }

    fun decreaseMana(amount: Int): Boolean {
        return if (mana >= amount) {
            mana -= amount
            true
        } else {
            false
        }
    }

    fun notifyLevelUp(job: JobMaster) {
        val jobStatus = getJobStatus(job)
        player.showTitle(
            Title.title(
                Component.text("Level Up!"),
                Component.text("${job.jobName.upperCamelCase()} Lv. ${jobStatus.getLevel()}"),
                Title.Times.of(Duration.ofMillis(500), Duration.ofSeconds(2), Duration.ofMillis(500))
            )
        )
        player.playSound(player.location, Sound.ENTITY_PLAYER_LEVELUP, 0.2f, 1.0f)
    }

    fun addJobExp(main: Main, job: JobMaster, point: Double, increaseCombo: Int = 1) {
        val jobStatus = getJobStatus(job)
        if (jobStatus.addExp(point, increaseCombo) == JobStatus.AddExpResult.LEVEL_UP) {
            increaseMaxMana(1)
            mana = maxMana
            refreshManaIndicator()
            notifyLevelUp(job)
        }
        val addedExp = jobStatus.getRecentAddedExp()
        val combo = jobStatus.getCombo()
        val jobName = job.jobName.upperCamelCase()

        if (expBarMap.containsKey(job)) {
            expBarMap[job]!!.cancel()
            expBarMap.remove(job)
        }
        val exp = jobStatus.getExp()
        val nextLevelExp = jobStatus.getNextLevelExp()
        var title = "$jobName Lv.${jobStatus.getLevel()} ${exp.floor2Digits()}/$nextLevelExp"
        title += if (addedExp > 0) " &a+${addedExp.floor2Digits()} " else " &c+${addedExp.floor2Digits()} "
        title += " &6${combo}Combo(x${(1 + combo * 0.002).floor3Digits()})"
        val bossBar = main.server.getBossBar(job.getExpBossBarKey(player)) ?: main.server.createBossBar(job.getExpBossBarKey(player), title, BarColor.GREEN, BarStyle.SEGMENTED_10)
        bossBar.apply {
            isVisible = true
            addPlayer(player)
            setTitle(title.colorS())
            progress = exp / nextLevelExp
        }
        expBarMap[job] = object : BukkitRunnable() {
            override fun run() {
                jobStatus.resetCombo()
                bossBar.removeAll()
                bossBar.isVisible = false
            }
        }.runTaskLater(main, 20 * 6L)
        setJobStatus(job, jobStatus)
    }

    fun setJobStatus(job: JobMaster, JobStatus: JobStatus) {
        jobStatusMap[job] = JobStatus
    }

    fun getJobStatus(job: JobMaster): JobStatus {
        return jobStatusMap[job] ?: JobStatus()
    }

    fun getAllJobStatus(): List<JobStatus> {
        return jobStatusMap.values.toList()
    }

    fun showManaIndicator() {
        refreshManaIndicator()
        player.showBossBar(manaIndicator)
    }

    fun hideManaIndicator() {
        player.hideBossBar(manaIndicator)
    }

    fun refreshManaIndicator() {
        val progress = max(0f, min(1f, mana / maxMana.toFloat()))
        manaIndicator.name(Component.text("Mana ($mana/$maxMana)"))
        manaIndicator.progress(progress)
    }

    fun save() {
        DataBaseManager.savePlayerStatus(this)
    }
}
