package com.abner.manager.util

import android.os.Environment
import java.io.File
import java.text.DecimalFormat

object FileUtils {
    /**
     * SD is available
     */
    fun storageAvailable(): Boolean {
        return if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            val sd = File(Environment.getExternalStorageDirectory().absolutePath)
            sd.canWrite()
        } else {
            false
        }
    }

    /**
     * 格式化文件大小
     */
    @JvmStatic
    fun formatFileSize(size: Long): String {
        if (size <= 0) return "0"
        val units = arrayOf("b", "kb", "M", "G", "T")
        //计算单位的，原理是利用lg,公式是 lg(1024^n) = nlg(1024)，最后 nlg(1024)/lg(1024) = n。
        val digitGroups = (Math.log10(size.toDouble()) / Math.log10(1024.0)).toInt()
        //计算原理是，size/单位值。单位值指的是:比如说b = 1024,KB = 1024^2
        return DecimalFormat("#,##0.##").format(
            size / Math.pow(
                1024.0,
                digitGroups.toDouble()
            )
        ) + " " + units[digitGroups]
    }
}