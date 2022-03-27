package com.alubecki.tiro

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.content.ComponentName


class TIROWidgetProvider : AppWidgetProvider() {


    companion object {
        const val ACTION_AUTO_UPDATE = "AUTO_UPDATE"
    }


    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (ACTION_AUTO_UPDATE == intent.action) {

            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(ComponentName(context.packageName, javaClass.name))
            updateWidgets(context, appWidgetManager, appWidgetIds)
        }
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {

        updateWidgets(context, appWidgetManager, appWidgetIds)
    }

    private fun updateWidgets(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {

        // Construct the RemoteViews object
        val views = RemoteViews(context.packageName, R.layout.tiro_widget)
        views.setTextViewText(R.id.appWidgetResultTime, ResultTimeManager.getResultTimeText(context))

        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
        views.setOnClickPendingIntent(R.id.appWidgetLayout, pendingIntent)

        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onEnabled(context: Context) {

        // start alarm only if first widget have been enabled
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(ComponentName(context.packageName, javaClass.name))

        if (appWidgetIds.size == 1) {
            AppWidgetAlarm(context.applicationContext).startAlarm()
        }
    }

    override fun onDisabled(context: Context) {

        // stop alarm only if all widgets have been disabled
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(ComponentName(context.packageName, javaClass.name))

        if (appWidgetIds.isEmpty()) {
            AppWidgetAlarm(context.applicationContext).stopAlarm()
        }
    }

}
