package com.github.kotyabuchi.RealisticSurvival.Utility

import com.github.kotyabuchi.RealisticSurvival.Job.JobType
import com.github.kotyabuchi.RealisticSurvival.Main
import com.github.kotyabuchi.RealisticSurvival.System.Player.JobStatus
import com.github.kotyabuchi.RealisticSurvival.System.Player.PlayerManager
import com.github.kotyabuchi.RealisticSurvival.System.Player.PlayerStatus
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

    fun initDB() {
        val dbFile = File(main.dataFolder, dbFileName)
        val dbHeader = "jdbc:sqlite:" + dbFile.absolutePath
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
        val dbFile = File(main.dataFolder, dbFileName)
        val dbHeader = "jdbc:sqlite:" + dbFile.absolutePath
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
            val dbFile = File(main.dataFolder, dbFileName)
            var stmt: Statement
            val dbHeader = "jdbc:sqlite:" + dbFile.absolutePath
            var pstmt: PreparedStatement
            try {
                DriverManager.getConnection(dbHeader).use { conn ->  //try-with-resources
                    conn.autoCommit = false
                    stmt = conn.createStatement()
                    val rs = stmt.executeQuery("SELECT * FROM jobs")
                    conn.commit()
                    val skills = mutableMapOf<JobType, Int>()
                    while (rs.next()) {
                        skills[JobType.valueOf(rs.getString("skill_name"))] = rs.getInt("skill_id")
                    }
                    try {
                        pstmt = conn.prepareStatement("REPLACE INTO player_job_status VALUES (?, ?, ?)")
                        statusList.forEach { status ->
                            JobType.values().forEach { job ->
                                skills[job]?.let { skillId ->
                                    val jobStatus = status.getJobStatus(job.jobClass)
                                    pstmt.setString(1, status.player.uniqueId.toString())
                                    pstmt.setInt(2, skillId)
                                    pstmt.setDouble(3, jobStatus.getTotalExp())
                                    pstmt.addBatch()
                                }
                            }
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

    fun startAutoSaveScheduler() {
        val interval = 20 * 60 * 5L
        object : BukkitRunnable() {
            override fun run() {
                savePlayerStatus()
            }
        }.runTaskTimer(main, interval, interval)
    }
}