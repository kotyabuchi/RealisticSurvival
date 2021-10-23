package com.github.kotyabuchi.RealisticSurvival.Utility

import com.github.kotyabuchi.RealisticSurvival.Job.JobType
import com.github.kotyabuchi.RealisticSurvival.Main
import com.github.kotyabuchi.RealisticSurvival.System.Player.*
import net.kyori.adventure.text.Component
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.SQLException
import java.sql.Statement

object DataBaseManager: KoinComponent {

    private val main: Main by inject()

    private const val dbFileName = "RealisticSurvival.db"
    private val dbFile = File(main.dataFolder, dbFileName)
    private val dbHeader = "jdbc:sqlite:" + dbFile.absolutePath

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
                    stmt.executeUpdate("CREATE TABLE IF NOT EXISTS homes (home_id INTEGER PRIMARY KEY AUTOINCREMENT, uuid TEXT NOT NULL, home_name TEXT NOT NULL, world TEXT NOT NULL, x REAL NOT NULL, y REAL NOT NULL, z REAL NOT NULL, yaw REAL NOT NULL, icon TEXT)")
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
                        pstmt = conn.prepareStatement("SELECT * FROM player_job_status INNER JOIN jobs ON (player_job_status.job_id = jobs.job_id) WHERE player_job_status.uuid = ?")
                        pstmt.setString(1, player.uniqueId.toString())
                        val jobRs = pstmt.executeQuery()

                        while (jobRs.next()) {
                            val jobStatus = JobStatus()
                            jobStatus.setTotalExp(jobRs.getDouble("job_total_exp"))
                            playerStatus.setJobStatus(JobType.valueOf(jobRs.getString("job_name")).jobClass, jobStatus)
                        }

                        pstmt = conn.prepareStatement("SELECT * FROM player_mana WHERE uuid = ?")
                        pstmt.setString(1, player.uniqueId.toString())
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
                        pstmt.setString(1, player.uniqueId.toString())
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
                            world?.let {
                                playerStatus.homes.add(Home(homeId, homeName, world, x, y, z, yaw, icon))
                            }
                        }
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
                        jobs[JobType.valueOf(rs.getString("job_name"))] = rs.getInt("job_id")
                    }
                    try {
                        pstmt = conn.prepareStatement("REPLACE INTO player_job_status VALUES (?, ?, ?)")
                        statusList.forEach { status ->
                            pstmt = conn.prepareStatement("REPLACE INTO player_job_status VALUES (?, ?, ?)")
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
                        statusList.forEach { status ->
                            pstmt = conn.prepareStatement("REPLACE INTO player_mana VALUES (?, ?, ?)")
                            pstmt.setString(1, status.player.uniqueId.toString())
                            pstmt.setDouble(2, status.maxMana)
                            pstmt.setDouble(3, status.mana)
                            pstmt.addBatch()
                            println("&a[System]${status.player.name}'s status saved'".colorS())
                        }
                        pstmt.executeBatch()
                        conn.commit()
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

    @Synchronized
    fun removeHome(homeId: Int) {
        var pstmt: PreparedStatement
        try {
            DriverManager.getConnection(dbHeader).use { conn ->  //try-with-resources
                pstmt = conn.prepareStatement("DELETE FROM homes WHERE home_id = ?")
                pstmt.setInt(1, homeId)
                pstmt.execute()
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    fun addHome(player: Player, homeName: String, location: Location): Int? {
        var pstmt: PreparedStatement

        val block = location.block.location.add(.5, .0, .5)
        val world = location.world ?: return null
        val worldName = world.name
        val x = block.x
        val y = location.y
        val z = block.z
        val yaw = location.yaw
        try {
            DriverManager.getConnection(dbHeader).use { conn ->  //try-with-resources
                pstmt = conn.prepareStatement("INSERT INTO homes(uuid, home_name, world, x, y, z, yaw, icon) VALUES (?,?,?,?,?,?,?,?)")
                pstmt.setString(1, player.uniqueId.toString())
                pstmt.setString(2, homeName)
                pstmt.setString(3, worldName)
                pstmt.setDouble(4, x)
                pstmt.setDouble(5, y)
                pstmt.setDouble(6, z)
                pstmt.setFloat(7, yaw)
                pstmt.setString(8, "ENDER_PEARL")
                pstmt.executeUpdate()

                val rs = conn.createStatement().executeQuery("SELECT LAST_INSERT_ROWID()")
                if (rs.next()) {
                    val homeId = rs.getInt("LAST_INSERT_ROWID()")
                    player.getStatus().homes.add(Home(homeId, homeName, world, x, y, z, yaw, Material.ENDER_PEARL))
                    return homeId
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return null
    }

    fun changeHomeIcon(player: Player, home: Home, icon: Material) {
        var pstmt: PreparedStatement

        home.homeId?.let { homeId ->
            try {
                DriverManager.getConnection(dbHeader).use { conn ->  //try-with-resources
                    pstmt = conn.prepareStatement("REPLACE INTO homes VALUES (?,?,?,?,?,?,?,?,?)")
                    pstmt.setInt(1, homeId)
                    pstmt.setString(2, player.uniqueId.toString())
                    pstmt.setString(3, home.name)
                    pstmt.setString(4, home.world.name)
                    pstmt.setDouble(5, home.x)
                    pstmt.setDouble(6, home.y)
                    pstmt.setDouble(7, home.z)
                    pstmt.setFloat(8, home.yaw)
                    pstmt.setString(9, icon.name)
                    pstmt.executeUpdate()
                }
            } catch (e: SQLException) {
                e.printStackTrace()
            }
        }
    }

    fun startAutoSaveScheduler() {
        val interval = 20 * 60 * 5L
        object : BukkitRunnable() {
            override fun run() {
                savePlayerStatus()
            }
        }.runTaskTimer(main, interval, interval)
    }
}