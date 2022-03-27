package com.alubecki.tiro

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.*


/**
 * Alarm used to refresh app widget (updatePeriodMillis can't be less than 30 min to save battery)
 *
 * Created by Aurelien Lubecki
 * on 27/03/2022.
 */
class AppWidgetAlarm(private val context: Context) {


    companion object {
        private const val ALARM_ID = 0
        private const val INTERVAL_MILLIS = 3 * 60 * 1000 //update widget every 3 min to save the battery
    }


    fun startAlarm() {

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MILLISECOND, INTERVAL_MILLIS)

        val alarmIntent = Intent(context, TIROWidgetProvider::class.java)
        alarmIntent.action = TIROWidgetProvider.ACTION_AUTO_UPDATE

        val pendingIntent = PendingIntent.getBroadcast(context, ALARM_ID, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        // RTC does not wake the device up
        alarmManager.setRepeating(AlarmManager.RTC, calendar.timeInMillis, INTERVAL_MILLIS.toLong(), pendingIntent)
    }

    fun stopAlarm() {

        val alarmIntent = Intent(TIROWidgetProvider.ACTION_AUTO_UPDATE)
        val pendingIntent = PendingIntent.getBroadcast(context, ALARM_ID, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }

}