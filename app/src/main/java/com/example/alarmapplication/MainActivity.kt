package com.example.alarmapplication

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.alarmapplication.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var sp: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    var SHARED_PREF_NAME = "AlarmStatus"
    val calendar = Calendar.getInstance()
    lateinit var pendingIntent:PendingIntent
    private lateinit var intent:Intent
    lateinit var alarmManager:AlarmManager

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //This is Modified from Surajit Branch

        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        intent = Intent(this@MainActivity, AlarmReceiver::class.java)
        pendingIntent = PendingIntent.getBroadcast(
            this@MainActivity,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sp = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        editor = sp.edit()

        binding.alarmtxt.setOnClickListener {
            calendar.set(Calendar.HOUR_OF_DAY, binding.timePicker.hour)
            calendar.set(Calendar.MINUTE, binding.timePicker.minute)
            calendar.set(Calendar.SECOND, 0)

            if (calendar.timeInMillis < System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_MONTH, 1)
            }
            binding.alarmtxt.text = SimpleDateFormat("HH:mm").format(calendar.time)
        }

        binding.onOfBtn.setOnClickListener {
            if (sp.getString("status", "") == "ON") {
                // Turn the alarm off
                editor.putString("status", "OFF")
                editor.apply()
                binding.onOfBtn.setBackgroundColor(Color.GREEN)
                binding.onOfBtn.text = "ON >>"
                cancelAlarm()
                Toast.makeText(this@MainActivity, "Alarm is Off", Toast.LENGTH_LONG).show()
            } else {
                // Turn the alarm on
                editor.putString("status", "ON")
                editor.apply()
                setAlarm(calendar.timeInMillis)
                binding.onOfBtn.text = "OFF >>"
                binding.onOfBtn.setBackgroundColor(Color.RED)
                Toast.makeText(this@MainActivity, "Alarm is On", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setAlarm(timeInMillis: Long) {
//        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        val intent = Intent(this@MainActivity, AlarmReceiver::class.java)
//        pendingIntent = PendingIntent.getBroadcast(
//            this@MainActivity,
//            ALARM_REQUEST_CODE,
//            intent,
//            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        )

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun cancelAlarm(){
//        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        val intent = Intent(this, AlarmReceiver::class.java)
//        val pendingIntent = PendingIntent.getBroadcast(
//            this,
//            ALARM_REQUEST_CODE,
//            intent,
//            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        )

        alarmManager.cancel(pendingIntent)
        alarmManager.cancelAll()
    }

    companion object {
        private const val ALARM_REQUEST_CODE = 0
    }
}
