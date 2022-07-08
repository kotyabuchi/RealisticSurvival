package com.github.kotyabuchi.RealisticSurvival.Utility

import com.github.kotyabuchi.RealisticSurvival.Job.JobType
import com.github.kotyabuchi.RealisticSurvival.Main
import com.github.kotyabuchi.RealisticSurvival.System.Player.*
import com.github.kotyabuchi.RealisticSurvival.System.TombStone
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.SQLException
import java.sql.Statement
import java.util.*

object DataBaseManager: KoinComponent {

    private val main: Main by inject()

    private const val dbFileName = "RealisticSurvival.db"
    private val dbFile = File(main.dataFolder, dbFileName)
    val dbHeader = "jdbc:sqlite:" + dbFile.absolutePath

    fun initDB() {
        var stmt: Statement
        var pstmt: PreparedStatement
        try {
            Class.forName("org.sqlite.JDBC")
            try {
                DriverManager.getConnection(dbHeader).use { conn ->  //try-with-resources
                    conn.autoCommit = false
                    stmt = conn.createStatement()
                    stmt.executeUpdate("CREATE TABLE IF NOT EXISTS player_job_status (uuid TEXT NOT NULL, job_id INTEGER NOT NULL, job_total_exp REAL NOT NULL, UNIQUE(uuid, job_id))")
                    stmt.executeUpdate("CREATE TABLE IF NOT EXISTS jobs (job_id INTEGER PRIMARY KEY AUTOINCREMENT, job_name TEXT UNIQUE NOT NULL)")
                    stmt.executeUpdate("CREATE TABLE IF NOT EXISTS player_mana (uuid TEXT NOT NULL PRIMARY KEY, max_mana REAL NOT NULL, mana REAL NOT NULL)")
                    stmt.executeUpdate("CREATE TABLE IF NOT EXISTS homes (home_id INTEGER PRIMARY KEY AUTOINCREMENT, uuid TEXT NOT NULL, home_name TEXT NOT NULL, world TEXT NOT NULL, x REAL NOT NULL, y REAL NOT NULL, z REAL NOT NULL, yaw REAL NOT NULL, icon TEXT, is_public INTEGER DEFAULT 0)")
                    stmt.executeUpdate("CREATE TABLE IF NOT EXISTS resource_storage_size (uuid TEXT NOT NULL PRIMARY KEY, slot_size INTEGER NOT NULL DEFAULT 2)")
                    stmt.executeUpdate("CREATE TABLE IF NOT EXISTS resource_storage (uuid TEXT NOT NULL, material TEXT NOT NULL, amount INTEGER NOT NULL)")
                    conn.commit()

                    try {
                        pstmt = conn.prepareStatement("INSERT INTO jobs(job_name) SELECT ? WHERE NOT EXISTS(SELECT 1 FROM jobs WHERE job_name = ?)")
                        JobType.values().forEach { job ->
                            pstmt.setString(1, job.name)
                            pstmt.setString(2, job.name)
                            pstmt.addBatch()
                        }
                        pstmt.executeBatch()
                        conn.commit()
                    } catch (e: SQLException) {
                        conn.rollback()
                        e.printStackTrace()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
    }

    fun loadPlayerStatus(vararg players: Player): List<PlayerStatus> {
        var pstmt: PreparedStatement

        val result = mutableListOf<PlayerStatus>()
        if (players.isNotEmpty()) {
            try {
                DriverManager.getConnection(dbHeader).use { conn ->  //try-with-resources
                    players.forEach { player ->
                        val playerStatus = PlayerStatus(player)
                        val uuid = player.uniqueId.toString()
                        pstmt = conn.prepareStatement("SELECT * FROM player_job_status INNER JOIN jobs ON (player_job_status.job_id = jobs.job_id) WHERE player_job_status.uuid = ?")
                        pstmt.setString(1, uuid)
                        val jobRs = pstmt.executeQuery()

                        while (jobRs.next()) {
                            valueOfOrNull<JobType>(jobRs.getString("job_name"))?.jobClass?.let { job ->
                                val jobStatus = JobStatus()
                                jobStatus.setTotalExp(jobRs.getDouble("job_total_exp"))
                                playerStatus.setJobStatus(job, jobStatus)
                            }
                        }

                        pstmt = conn.prepareStatement("SELECT * FROM player_mana WHERE uuid = ?")
                        pstmt.setString(1, uuid)
                        val manaRs = pstmt.executeQuery()

                        if (manaRs.next()) {
                            playerStatus.maxMana = manaRs.getDouble("max_mana")
                            playerStatus.mana = manaRs.getDouble("mana")
                        } else {
                            playerStatus.getAllJobStatus().forEach {
                                playerStatus.increaseMaxMana(it.getLevel() - 1.0)
                            }
                            playerStatus.mana = playerStatus.maxMana
                        }

                        pstmt = conn.prepareStatement("SELECT * FROM homes WHERE uuid = ?")
                        pstmt.setString(1, uuid)
                        val homeRs = pstmt.executeQuery()

                        while (homeRs.next()) {
                            val homeId = homeRs.getInt("home_id")
                            val homeName = homeRs.getString("home_name")
                            val world = main.server.getWorld(homeRs.getString("world"))
                            val x = homeRs.getDouble("x")
                            val y = homeRs.getDouble("y")
                            val z = homeRs.getDouble("z")
                            val yaw = homeRs.getFloat("yaw")
                            val icon = Material.valueOf(homeRs.getString("icon") ?: "ENDER_PEARL")
                            val creator = UUID.fromString(homeRs.getString("uuid"))
                            val isPublic = (homeRs.getInt("is_public") == 1)
                            world?.let {
                                playerStatus.homes.add(Home(homeId, homeName, world, x, y, z, yaw, icon, creator, isPublic))
                            }
                        }

                        val resourceStorage = ResourceStorage()
                        pstmt = conn.prepareStatement("SELECT * FROM resource_storage_size WHERE uuid = ?")
                        pstmt.setString(1, uuid)
                        val resourceStorageSizeRs = pstmt.executeQuery()

                        while (resourceStorageSizeRs.next()) {
                            resourceStorage.slotSize = resourceStorageSizeRs.getInt("slot_size")
                        }

                        pstmt = conn.prepareStatement("SELECT * FROM resource_storage WHERE uuid = ?")
                        pstmt.setString(1, uuid)
                        val resourceStorageRs = pstmt.executeQuery()

                        while (resourceStorageRs.next()) {
                            val material = Material.valueOf(resourceStorageRs.getString("material"))
                            val amount = resourceStorageRs.getInt("amount")
                            println("$material: ${resourceStorage.existsMaterial(ItemStack(material))}")
                            resourceStorage.storeResource(ItemStack(material, amount))
                        }
                        playerStatus.resourceStorage = resourceStorage

                        result.add(playerStatus)
                    }
                }
            } catch (e: SQLException) {
                e.printStackTrace()
            }
        }

        return result
    }

    @Synchronized
    fun savePlayerStatus(vararg statusList: PlayerStatus = PlayerManager.getAllPlayerStatus().toTypedArray()) {
        if (statusList.isNotEmpty()) {
            println("&a[System]PlayerStatusをデータベースに保存開始...".colorS())
            var success = true
            var stmt: Statement
            var pstmt: PreparedStatement
            try {
                DriverManager.getConnection(dbHeader).use { conn ->  //try-with-resources
                    conn.autoCommit = false
                    stmt = conn.createStatement()
                    val rs = stmt.executeQuery("SELECT * FROM jobs")
                    conn.commit()
                    val jobs = mutableMapOf<JobType, Int>()
                    while (rs.next()) {
                        valueOfOrNull<JobType>(rs.getString("job_name"))?.let { job ->
                            jobs[job] = rs.getInt("job_id")
                        }
                    }
                    try {
                        pstmt = conn.prepareStatement("REPLACE INTO player_job_status VALUES (?, ?, ?)")
                        statusList.forEach { status ->
                            JobType.values().forEach { job ->
                                jobs[job]?.let { jobId ->
                                    val jobStatus = status.getJobStatus(job.jobClass)
                                    pstmt.setString(1, status.player.uniqueId.toString())
                                    pstmt.setInt(2, jobId)
                                    pstmt.setDouble(3, jobStatus.getTotalExp())
                                    pstmt.addBatch()
                                }
                            }
                        }
                        pstmt.executeBatch()
                        pstmt = conn.prepareStatement("REPLACE INTO player_mana VALUES (?, ?, ?)")
                        statusList.forEach { status ->
                            pstmt.setString(1, status.player.uniqueId.toString())
                            pstmt.setDouble(2, status.maxMana)
                            pstmt.setDouble(3, status.mana)
                            pstmt.addBatch()
                        }
                        pstmt.executeBatch()
                        pstmt = conn.prepareStatement("DELETE FROM resource_storage WHERE uuid = ?")
                        statusList.forEach { status ->
                            pstmt.setString(1, status.player.uniqueId.toString())
                            pstmt.addBatch()
                        }
                        pstmt.executeBatch()
                        pstmt = conn.prepareStatement("INSERT INTO resource_storage(uuid, material, amount) VALUES (?, ?, ?)")
                        statusList.forEach { status ->
                            status.resourceStorage.getStoredResources().forEach { (material, amount) ->
                                pstmt.setString(1, status.player.uniqueId.toString())
                                pstmt.setString(2, material.name)
                                pstmt.setInt(3, amount)
                                pstmt.addBatch()
                            }
                        }
                        pstmt.executeBatch()
                        pstmt = conn.prepareStatement("REPLACE INTO resource_storage_size VALUES (?, ?)")
                        statusList.forEach { status ->
                            pstmt.setString(1, status.player.uniqueId.toString())
                            pstmt.setInt(2, status.resourceStorage.slotSize)
                            pstmt.addBatch()
                        }
                        pstmt.executeBatch()
                        conn.commit()
//                        println("&a[System]${status.player.name}'s status saved'".colorS())
                    } catch (e: SQLException) {
                        conn.rollback()
                        e.printStackTrace()
                        success = false
                    }
                }
            } catch (e: SQLException) {
                e.printStackTrace()
                success = false
            }

            if (success) {
                println("&a[System]PlayerStatusをデータベースに保存しました".colorS())
            } else {
                println("&4[System]PlayerStatusの保存に失敗しました".colorS())
            }
        }
    }

    fun startAutoSaveScheduler() {
        val interval = 20 * 60 * 5L
        object : BukkitRunnable() {
            override fun run() {
                savePlayerStatus()
                TombStone.saveTombStoneFile()
            }
        }.runTaskTimer(main, interval, interval)
    }
}