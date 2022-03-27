package com.alubecki.tiro

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import java.text.NumberFormat
import java.util.*

/**
 * Created by Aurelien Lubecki
 * on 27/03/2022.
 */
object ResultTimeManager {


    private const val FILE_NAME = "com.alubecki.tiro.time"

    private const val FIELD_SLEEP_TIME_MS = "sleepTimeMs"
    private const val FIELD_RESERVED_DURATION_MS = "reservedDurationMs"

    private const val DEFAULT_SLEEP_TIME_MS = 0L
    private const val DEFAULT_RESERVED_DURATION_MS = 3000L


    private fun getFile(context: Context): SharedPreferences {
        return context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
    }

    fun saveSleepTimeMs(context: Context, timeMs: Long) {

        val t = if (timeMs < 0 || timeMs > 24 * 60 * 60 * 1000L) 0 else timeMs

        return getFile(context).edit {
            putLong(FIELD_SLEEP_TIME_MS, t)
            apply()
        }
    }

    fun getSleepTimeMs(context: Context): Long {
        return getFile(context).getLong(FIELD_SLEEP_TIME_MS, DEFAULT_SLEEP_TIME_MS)
    }

    fun getSleepTimeHours(context: Context): Int {
        return (getSleepTimeMs(context) / (60 * 60 * 1000)).toInt()
    }

    fun getSleepTimeMinutes(context: Context): Int {
        return (getSleepTimeMs(context) % (60 * 60 * 1000) / (60 * 1000)).toInt()
    }

    fun saveReservedDurationMs(context: Context, durationMs: Long) {

        val d = if (durationMs < 0 || durationMs > 24 * 60 * 60 * 1000) 0 else durationMs

        return getFile(context).edit {
            putLong(FIELD_RESERVED_DURATION_MS, d)
            apply()
        }
    }

    fun getReservedDurationMs(context: Context): Long {
        return getFile(context).getLong(FIELD_RESERVED_DURATION_MS, DEFAULT_RESERVED_DURATION_MS)
    }

    fun getReservedDurationHours(context: Context): Int {
        return (getReservedDurationMs(context) / (60 * 60 * 1000)).toInt()
    }

    fun getResultTimeMs(context: Context): Long {

        val time = Date().time
        val currentTime = Date().time % (24 * 60 * 60 * 1000) + TimeZone.getDefault().getOffset(time)

        var diff = getSleepTimeMs(context) - currentTime
        if (diff < 0) {
            diff += 24 * 60 * 60 * 1000
        }

        //remove more reserved duration at the beginning of the day
        val reservedHoursProrata = getReservedDurationMs(context) * diff / (24 * 60 * 60 * 1000)
        var res = diff - reservedHoursProrata
        if (res < 0) {
            res = 0
        }

        return res
    }

    fun getResultTimeHours(context: Context): Int {
        return (getResultTimeMs(context) / (60 * 60 * 1000)).toInt()
    }

    fun getResultTimeMinutes(context: Context): Int {
        return (getResultTimeMs(context) % (60 * 60 * 1000) / (60 * 1000)).toInt()
    }

    fun getSleepTimeText(context: Context): String {

        val fHours = NumberFormat.getInstance()
        fHours.minimumIntegerDigits = 2

        return "" + getSleepTimeHours(context) + ":" + fHours.format(getSleepTimeMinutes(context))
    }

    fun getReservedDurationText(context: Context): String {
        return "" + getReservedDurationHours(context)
    }

    fun getResultTimeText(context: Context): String {

        val fHours = NumberFormat.getInstance()
        fHours.minimumIntegerDigits = 2
        return "" + getResultTimeHours(context) + "h" + fHours.format(getResultTimeMinutes(context))
    }

}