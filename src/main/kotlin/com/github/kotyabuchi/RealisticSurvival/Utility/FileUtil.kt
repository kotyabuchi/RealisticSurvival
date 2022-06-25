package com.github.kotyabuchi.RealisticSurvival.Utility

import java.io.*
import java.nio.file.Files

fun saveFile(path: String, vararg objs: Any) {
    saveFile(File(path), objs)
}

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

fun readFile(file: File): String {
    return Files.readString(file.toPath())
}