package com.abner.manager.util

import java.text.SimpleDateFormat
import java.util.*

object TimeUtils {
    /**
     * 将时间转换成日期
     */
    fun formatDateToStr(timeInMillis: Long, dateFormat: String?): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMillis
        val date = calendar.time
        return formatDateToStr(date, dateFormat)
    }

    /**
     * Date转换成字符串日期
     */
    fun formatDateToStr(date: Date?, dateFormat: String?): String {
        val sdf = SimpleDateFormat(dateFormat)
        return sdf.format(date)
    }
}