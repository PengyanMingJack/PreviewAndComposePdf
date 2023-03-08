package com.zchd.pdf

import androidx.annotation.IntDef
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.text.SimpleDateFormat
import java.util.*


object DateUtil {

    // 日期格式
    const val DATE_FORMAT = "yyyy-MM-dd"
    const val TIME_FORMAT = "yyyy-MM-dd HH:mm:ss"
    const val FORMAT_YYYY_MM = "yyyy-MM"
    const val FORMAT_YYYY = "yyyy"
    const val FORMAT_HH_MM = "HH:mm"
    const val FORMAT_HH_MM2 = "HH小时mm分"
    const val FORMAT_HH_MM3 = "HH分mm秒"
    const val FORMAT_HH_MM_SS_CN = "HH小时mm分ss秒"
    const val FORMAT_HH_MM_SS = "HH:mm:ss"
    const val FORMAT_MM_SS = "mm:ss"
    const val FORMAT_MM_DD_HH_MM = "MM-dd HH:mm"
    const val FORMAT_MM_DD_HH_MM_SS = "MM-dd HH:mm:ss"
    const val FORMAT_YYYY_MM_DD_UNDERLINE = "yyyy-MM-dd"
    const val FORMAT_YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm"
    const val FORMAT_YYYY_MM_DD_HH_MM_ = "yyyy/MM/dd HH:mm"
    const val FORMAT_YYYY2MM2DD = "yyyy.MM.dd"
    const val FORMAT_YYYY2MM2DD_HH_MM = "yyyy.MM.dd HH:mm"
    const val FORMAT_YYYY2MM2DD_HH_MM_SS = "yyyy.MM.dd HH:mm"
    const val FORMAT_MMCDD_HH_MM = "MM月dd日 HH:mm"
    const val FORMAT_MMCDD = "MM月dd日"
    const val FORMAT_MM_DD = "M.dd"
    const val FORMAT_YYYYCMMCDD = "yyyy年MM月dd日"
    const val FORMAT_YYYYCMMCDD_HH_MM_SS = "yyyy年MM月dd日 HH:mm:ss"
    const val FORMAT_YYYYCMMCDD_HH_MM = "yyyy年MM月dd日 HH:mm"
    const val FORMAT_YYYYCMMCDD_HH_MM_C = "yyyy年MM月dd日HH时mm分"
    const val FORMAT_YYYY_MM2 = "yyyy年MM月"
    const val FORMAT_YYYY_MM3 = "yyyy年M月"
    const val FORMAT_YYYY_MM_HH = "yyyy年MM月dd日HH"
    const val FORMAT_YYYY_MM_DD = "yyyy/MM/dd"

    const val ONE_DAY = 1000 * 60 * 60 * 24.toLong()

    //判断选择的日期是否是本周（分从周日开始和从周一开始两种方式）
    fun isThisWeek(time: Date?): Boolean { //        //周日开始计算
//      Calendar calendar = Calendar.getInstance();
//周一开始计算
        val calendar: Calendar = Calendar.getInstance(Locale.CHINA)
        calendar.firstDayOfWeek = Calendar.MONDAY
        val currentWeek: Int = calendar.get(Calendar.WEEK_OF_YEAR)
        calendar.time = time ?: Date()
        val paramWeek: Int = calendar.get(Calendar.WEEK_OF_YEAR)
        return paramWeek == currentWeek
    }

    //判断选择的日期是否是今天
    fun isToday(time: Date): Boolean {
        return isThisTime(time, DATE_FORMAT)
    }

    //判断选择的日期是否是本月
    fun isThisMonth(time: Date): Boolean {
        return isThisTime(time, "yyyy-MM")
    }

    //判断选择的日期是否是本年
    fun isThisYear(time: Date): Boolean {
        return isThisTime(time, "yyyy")
    }

    @JvmStatic
    fun isThisYear(time: Long): Boolean {
        return isThisTime(Date(time), "yyyy")
    }

    //判断选择的日期是否是昨天
    fun isYesterDay(time: Date): Boolean {
        val cal: Calendar = Calendar.getInstance()
        val lt: Long = time.time / 86400000
        val ct: Long = cal.timeInMillis / 86400000
        return ct - lt == 1L
    }

    private fun isThisTime(date: Date, pattern: String): Boolean {
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        val param: String = sdf.format(date) //参数时间
        val now: String = sdf.format(Date()) //当前时间
        return param == now
    }

    /**
     * 获取某年某月有多少天
     */
    fun getDayOfMonth(year: Int, month: Int): Int {
        val c: Calendar = Calendar.getInstance()
        c.set(year, month, 0) //输入类型为int类型
        return c.get(Calendar.DAY_OF_MONTH)
    }

    /**
     * 获取两个时间相差的天数
     *
     * @return time1 - time2相差的天数
     */
    fun getDayOffset(time1: Long, time2: Long): Int { // 将小的时间置为当天的0点
        val offsetTime: Long = if (time1 > time2) {
            time1 - getDayStartTime(getCalendar(time2)).timeInMillis
        } else {
            getDayStartTime(getCalendar(time1)).timeInMillis - time2
        }
        return (offsetTime / ONE_DAY).toInt()
    }

    fun getCalendar(time: Long): Calendar {
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = time
        return calendar
    }

    fun getDayStartTime(calendar: Calendar): Calendar {
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar
    }

    @JvmStatic
    fun getTimeAgoText(time: Long): String {
        val ONE_MINUTE = 60000L
        val ONE_HOUR = 3600000L
        val ONE_DAY = 86400000L
        val ONE_WEEK = 604800000L

        val ONE_SECOND_AGO = "秒前"
        val ONE_MINUTE_AGO = "分钟前"
        val ONE_HOUR_AGO = "小时前"
        val ONE_DAY_AGO = "天前"
        val ONE_MONTH_AGO = "月前"
        val ONE_YEAR_AGO = "年前"

        val delta: Long = Date().time - time
        if (delta < 1L * ONE_MINUTE) {
            val seconds: Long = toSeconds(delta)
            return "${(if (seconds <= 0) 1 else seconds)}${ONE_SECOND_AGO}"
        }
        if (delta < 45L * ONE_MINUTE) {
            val minutes: Long = toMinutes(delta)
            return "${(if (minutes <= 0) 1 else minutes)}${ONE_MINUTE_AGO}"
        }
        if (delta < 24L * ONE_HOUR) {
            val hours: Long = toHours(delta)
            return "${(if (hours <= 0) 1 else hours)}${ONE_HOUR_AGO}"
        }
        if (delta < 48L * ONE_HOUR) {
            return "1${ONE_DAY_AGO}"
        }
        if (delta < 30L * ONE_DAY) {
            val days: Long = toDays(delta)
            return "${(if (days <= 0) 1 else days)}${ONE_DAY_AGO}"
        }
        return if (delta < 12L * 4L * ONE_WEEK) {
            val months: Long = toMonths(delta)
            "${(if (months <= 0) 1 else months)}${ONE_MONTH_AGO}"
        } else {
            val years: Long = toYears(delta)
            "${(if (years <= 0) 1 else years)}${ONE_YEAR_AGO}"
        }
    }

    private fun toSeconds(date: Long): Long {
        return date / 1000L
    }

    private fun toMinutes(date: Long): Long {
        return toSeconds(date) / 60L
    }

    private fun toHours(date: Long): Long {
        return toMinutes(date) / 60L
    }

    private fun toDays(date: Long): Long {
        return toHours(date) / 24L
    }

    private fun toMonths(date: Long): Long {
        return toDays(date) / 30L
    }

    private fun toYears(date: Long): Long {
        return toMonths(date) / 365L
    }

    @JvmStatic
    fun formatString(
        milliseconds: Long, type: String = FORMAT_YYYYCMMCDD,
    ): String? {
        val currentTime = Date(milliseconds)
        val minute = 60 * 1000// 1分钟
        val hour = 60 * minute// 1小时
        val day = 24 * hour// 1天
        val diff = Date().time - currentTime.time
        val r: Long
        if (diff < 2 * minute) {
            return "刚刚"
        }
        if (diff >= 2 * minute && diff < hour) {
            r = (diff / minute)
            return "${r}分钟前"
        }
        if (diff in hour until day) {
            r = (diff / hour)
            return "${r}小时前"
        }
        if (diff in day until 7 * day) {
            r = (diff / day)
            return "${r}天前"
        }
        return convertToString(milliseconds, type)
    }

    @JvmStatic
    fun formatStringDefault(
        milliseconds: Long, type: String = FORMAT_YYYY_MM_DD_HH_MM,
    ): String? {
        val currentTime = Date(milliseconds)
        val minute = 60 * 1000// 2分钟
        val hour = 60 * minute// 1小时
        val day = 24 * hour// 1天
        val diff = Date().time - currentTime.time
        val r: Long
        if (diff < 2 * minute) {
            return "刚刚"
        }
        if (diff >= 2 * minute && diff < hour) {
            r = (diff / minute)
            return "${r}分钟前"
        }
        if (diff in hour until day) {
            r = (diff / hour)
            return "${r}小时前"
        }
        if (diff in day until 7 * day) {
            r = (diff / day)
            return "${r}天前"
        }
        return convertToString(milliseconds, type)
    }

    /**
     * 获取当前时间是星期几
     *
     * @param dt
     * @return
     */
    fun getWeekOfDate(dt: Date = Date()): String {
        val weekDays =
            arrayOf("星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六")
        val cal: Calendar = Calendar.getInstance()
        cal.time = dt
        var w: Int = cal.get(Calendar.DAY_OF_WEEK) - 1
        if (w < 0) w = 0
        return weekDays[w]
    }

    fun getFirstDayOfWeek(): Date {
        val cal = Calendar.getInstance()
        cal[Calendar.DAY_OF_WEEK] = cal.firstDayOfWeek
        return cal.time
    }

    /**
     *
     */
    fun getSevenDayList(): List<Date> {
        val calendar = Calendar.getInstance()
        val date = calendar.time
        val list = ArrayList<Date>()
        for (index in 0 until 7) {
            val newDate = Date(date.time + 24 * 60 * 60 * 1000 * index)
            list.add(newDate)
        }
        return list
    }

    fun getWeekOfDateStr(date: Date): String? {
        val weekDays =
            arrayOf("周日", "周一", "周二", "周三", "周四", "周五", "周六")
        val cal: Calendar = Calendar.getInstance()
        cal.time = date
        var w: Int = cal.get(Calendar.DAY_OF_WEEK) - 1
        if (w < 0) w = 0
        return weekDays[w]
    }

    /**
     * 将日期格式的字符串转换为长整型
     *
     * @param date
     * @param format
     * @return
     */
    @JvmStatic
    fun convertToLong(date: String?, format: String = TIME_FORMAT): Long {
        try {
            if (date.isNullOrEmpty())
                return 0
            val formatter = SimpleDateFormat(format, Locale.getDefault())
            return formatter.parse(date).time
        } catch (e: Exception) {
            e.printStackTrace()
            return 0
        }
    }

    @JvmStatic
    fun convertToStringTime(time: Long): String? {
        return convertToString(time, DATE_FORMAT)
    }

    @JvmStatic
    fun convertToString(time: Long): String? {
        return convertToString(time, TIME_FORMAT)
    }

    @JvmStatic
    fun convertToStringNormal(time: Long, format: String): String? {
        if (time > 0) {
            if (isThisYear(time)) {
                val formatter = SimpleDateFormat(format, Locale.getDefault())
                val date = Date(time)
                return formatter.format(date)
            } else {
                val formatter = SimpleDateFormat(FORMAT_YYYYCMMCDD_HH_MM, Locale.getDefault())
                val date = Date(time)
                return formatter.format(date)
            }
        }
        return ""
    }

    @JvmStatic
    fun convertToStringWithoutChinese(time: Long, format: String): String? {
        if (time > 0) {
            if (isThisYear(time)) {
                val formatter = SimpleDateFormat(format, Locale.getDefault())
                val date = Date(time)
                return formatter.format(date)
            } else {
                val formatter = SimpleDateFormat(FORMAT_YYYY_MM_DD_HH_MM_, Locale.getDefault())
                val date = Date(time)
                return formatter.format(date)
            }

        }
        return ""
    }


    @JvmStatic
    fun convertToString(time: Long, format: String): String? {
        if (time > 0) {
            val formatter = SimpleDateFormat(format, Locale.getDefault())
            val date = Date(time)
            return formatter.format(date)
        }
        return ""
    }


    fun getSurplusHour(time: Long): String {
        val day = (time / 1000 / 60 / 60 / 24).toInt()
        val hour = (time / 1000 / 60 / 60 % 24).toInt()
        val minute = (time / 1000 / 60 % 60).toInt()
        return "${hour + day * 24}小时${minute}分"
    }

    fun getSurplusHMS(time: Long): String {
        val day = (time / 1000 / 60 / 60 / 24).toInt()
        val hour = (time / 1000 / 60 / 60 % 24).toInt()
        val minute = (time / 1000 / 60 % 60).toInt()
        val second = (time / 1000 % 60).toInt()
        val surplusHour = hour + day * 24
        return "${if (surplusHour > 0) "${surplusHour}时" else ""}${minute}分${second}秒"
    }

    fun getSurplusWithoutSuffix(time: Long): String {
        val day = (time / 1000 / 60 / 60 / 24).toInt()
        val hour = (time / 1000 / 60 / 60 % 24).toInt()
        val minute = (time / 1000 / 60 % 60).toInt()
        val second = (time / 1000 % 60).toInt()
        val surplusHour = hour + day * 24
        return "${if (surplusHour > 0) "${surplusHour}:" else ""}${minute}:${second}"
    }

    fun getSurplusDHM(time: Long): String {
        val day = (time / 1000 / 60 / 60 / 24).toInt()
        val hour = (time / 1000 / 60 / 60 % 24).toInt()
        val minute = (time / 1000 / 60 % 60).toInt()
        return "${if (day > 0) "${day}天" else ""}${if (hour > 0) "${hour}时" else ""}${minute}分"
    }

    fun formatTime(time: Long, type: String): String {
        val sdf = SimpleDateFormat(type, Locale.getDefault())
        return sdf.format(time)
    }

    fun getNowHour(): Int {
        val calendar = Calendar.getInstance()
        return calendar.get(Calendar.HOUR_OF_DAY)
    }

    @JvmStatic
    fun getOrderListTime(startTime: Long?, endTime: Long?): String {
        return convertToString(
            startTime ?: 0,
            DATE_FORMAT
        ) + " " + getWeekOfDate(Date(startTime ?: 0)) + " " + convertToString(
            startTime ?: 0,
            FORMAT_HH_MM
        ) + "-" + convertToString(endTime ?: 0, FORMAT_HH_MM)
    }

    @JvmStatic
    fun isAfternoon(time: Long): Boolean {
        val calendar = GregorianCalendar.getInstance()
        calendar.timeInMillis = time
        return calendar.get(Calendar.AM_PM) == 1
    }

    @JvmStatic
    fun getOrderTime(startTime: Long?): String {
        return convertToString(
            startTime ?: 0,
            DATE_FORMAT
        ) + " " + getWeekOfDate(Date(startTime ?: 0)) + " " + convertToString(
            startTime ?: 0,
            FORMAT_HH_MM
        )
    }

    @JvmStatic
    fun getOrderWeekTime(startTime: Long?): String {
        return convertToString(
            startTime ?: 0,
            DATE_FORMAT
        ) + " " + getWeekOfDate(Date(startTime ?: 0))
    }

    /**
     * Return whether it is leap year.
     *
     * @param millis The milliseconds.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isLeapYear(millis: Long): Boolean {
        return isLeapYear(millis2Date(millis))
    }

    /**
     * Milliseconds to the date.
     *
     * @param millis The milliseconds.
     * @return the date
     */
    fun millis2Date(millis: Long): Date {
        return Date(millis)
    }

    /**
     * Return whether it is leap year.
     *
     * @param date The date.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isLeapYear(date: Date?): Boolean {
        val cal = Calendar.getInstance()
        cal.time = date
        val year = cal[Calendar.YEAR]
        return isLeapYear(year)
    }

    /**
     * Return whether it is leap year.
     *
     * @param year The year.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isLeapYear(year: Int): Boolean {
        return year % 4 == 0 && year % 100 != 0 || year % 400 == 0
    }

    object TimeConstants {
        const val MSEC = 1
        const val SEC = 1000
        const val MIN = 60000
        const val HOUR = 3600000
        const val DAY = 86400000

        @IntDef(MSEC, SEC, MIN, HOUR, DAY)
        @Retention(RetentionPolicy.SOURCE)
        annotation class Unit
    }
}

