package com.cleaningapp.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

object EmailUtils {

    fun sendLogFile(context: Context) {
        val logFile: File = LogUtils.getLogFile(context)

        if (!logFile.exists()) {
            LogUtils.writeLog(context, "No logs to send")
            return
        }

        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            logFile
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf("chs.latvia@gmail.com"))
            putExtra(Intent.EXTRA_SUBJECT, "Cleaning App Logs")
            putExtra(Intent.EXTRA_TEXT, "Log file attached from CleaningApp")
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(intent, "Send Email"))
    }
}
