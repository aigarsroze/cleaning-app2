package com.cleaningapp.utils

import android.content.Context
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object LogUtils {

    private const val LOG_FILE_NAME = "cleaning_log.txt"

    fun writeLog(context: Context, message: String) {
        val logFile = File(context.filesDir, LOG_FILE_NAME)
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        try {
            val writer = FileWriter(logFile, true)
            writer.append("$timestamp: $message\n")
            writer.flush()
            writer.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getLogFile(context: Context): File {
        return File(context.filesDir, LOG_FILE_NAME)
    }
}
