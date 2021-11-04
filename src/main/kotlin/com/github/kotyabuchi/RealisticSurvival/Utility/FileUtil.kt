package com.github.kotyabuchi.RealisticSurvival.Utility

import java.io.*

object FileUtil {
    fun saveFile(file: File, vararg objs: Any) {
        try {
            if (!file.exists()) file.createNewFile()
            val fw = FileWriter(file)
            val pw = PrintWriter(BufferedWriter(fw))
            for (obj in objs) {
                pw.print(obj)
            }
            pw.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}