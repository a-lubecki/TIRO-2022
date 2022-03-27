package com.alubecki.tiro

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.NumberPicker
import android.widget.TimePicker
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.concurrent.timerTask


class MainActivity : AppCompatActivity() {


    private var timer: Timer? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        layoutSleepTime.setOnClickListener {
            showPickerSleepTime()
        }

        layoutReservedHours.setOnClickListener {
            showPickerReservedHours()
        }

        buttonAdd.setOnClickListener {
            addWidgetOnHomeScreen()
        }

        updateUI()
    }

    override fun onResume() {
        super.onResume()

        timer = Timer()
        timer?.scheduleAtFixedRate(timerTask {

            Handler(Looper.getMainLooper()).post {
                updateResultTime()
            }

        }, 0, 20 * 1000) //every 20 sec for better refresh
    }

    override fun onPause() {
        super.onPause()

        timer?.cancel()
        timer = null
    }

    private fun showPickerSleepTime() {

        val picker = TimePicker(this@MainActivity).apply {

            setIs24HourView(true)

            currentHour = ResultTimeManager.getSleepTimeHours(this@MainActivity)
            currentMinute = ResultTimeManager.getSleepTimeMinutes(this@MainActivity)
        }

        AlertDialog.Builder(this).apply {

            setView(picker)

            setPositiveButton("OK") { d, _ ->
                ResultTimeManager.saveSleepTimeMs(this@MainActivity, picker.currentHour * 60 * 60 * 1000L + picker.currentMinute * 60 * 1000L)
                updateUI()
            }

            setNegativeButton("Annuler") { d, _ -> }

        }.create().show()
    }

    private fun showPickerReservedHours() {

        val picker = NumberPicker(this@MainActivity).apply {

            minValue = 0
            maxValue = 23

            value = ResultTimeManager.getReservedDurationHours(this@MainActivity)
        }

        AlertDialog.Builder(this).apply {

            setView(picker)

            setPositiveButton("OK") { _, _ ->
                ResultTimeManager.saveReservedDurationMs(this@MainActivity, picker.value * 60 * 60 * 1000L)
                updateUI()
            }

            setNegativeButton("Annuler") { d, _ -> d.cancel() }

        }.create().show()
    }

    private fun updateUI() {

        textViewSleepTime.text = ResultTimeManager.getSleepTimeText(this)
        textViewReservedHours.text = ResultTimeManager.getReservedDurationText(this)

        updateResultTime()

        refreshWidgets()
    }

    private fun updateResultTime() {
        textViewResultTime.text = ResultTimeManager.getResultTimeText(this)
    }

    @SuppressLint("NewApi")
    private fun addWidgetOnHomeScreen() {

        val appWidgetManager = AppWidgetManager.getInstance(this)

        if (appWidgetManager.isRequestPinAppWidgetSupported) {
            appWidgetManager.requestPinAppWidget(ComponentName(this, TIROWidgetProvider::class.java), null, null)
        }
    }

    private fun refreshWidgets() {

        sendBroadcast(Intent(this, TIROWidgetProvider::class.java).apply {

            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE

            val ids: IntArray = AppWidgetManager.getInstance(application).getAppWidgetIds(ComponentName(application, TIROWidgetProvider::class.java))
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        })
    }

}
