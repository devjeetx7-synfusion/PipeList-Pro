package com.synfusion.pipelistpro.core.utils

import android.content.Context
import android.content.Intent
import com.synfusion.pipelistpro.features.settings.CrashActivity
import java.io.PrintWriter
import java.io.StringWriter

class GlobalCrashHandler(private val applicationContext: Context) : Thread.UncaughtExceptionHandler {
    private val defaultHandler: Thread.UncaughtExceptionHandler? = Thread.getDefaultUncaughtExceptionHandler()

    override fun uncaughtException(thread: Thread, exception: Throwable) {
        try {
            val sw = StringWriter()
            val pw = PrintWriter(sw)
            exception.printStackTrace(pw)
            val stackTrace = sw.toString()

            val intent = Intent(applicationContext, CrashActivity::class.java).apply {
                putExtra(CrashActivity.EXTRA_CRASH_INFO, stackTrace)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
            applicationContext.startActivity(intent)

            android.os.Process.killProcess(android.os.Process.myPid())
            kotlin.system.exitProcess(10)
        } catch (e: Exception) {
            e.printStackTrace()
            defaultHandler?.uncaughtException(thread, exception)
        }
    }
}
