package com.example.pilldispenser

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.fixedRateTimer
import android.Manifest
import android.content.SharedPreferences
import androidx.core.app.ActivityCompat
import java.text.ParseException

class MainActivity : AppCompatActivity() {
    private lateinit var timeTextView: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UpcomingPillAdapter
    private lateinit var noPillsTextView: TextView
    private val handler = Handler(Looper.getMainLooper())
    private var redFlag = false
    private val processedLogIds = mutableSetOf<Int>()
    private val PERMISSION_REQUEST_CODE = 1001 // Choose any unique integer
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private val PROCESSED_LOG_IDS_KEY = "processed_log_ids"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Pill Dispenser"

        timeTextView = findViewById(R.id.timeTextView)
        recyclerView = findViewById(R.id.recyclerView)
        noPillsTextView = findViewById(R.id.noPillsTextView)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = UpcomingPillAdapter(emptyList())
        recyclerView.adapter = adapter

        // Initialize class-level sharedPreferences and editor
        sharedPreferences = getSharedPreferences("MySharedPrefs", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        // Restore processedLogIds from SharedPreferences
        val processedLogIdsSet = sharedPreferences.getStringSet(PROCESSED_LOG_IDS_KEY, null)
        if (processedLogIdsSet != null) {
            processedLogIds.addAll(processedLogIdsSet.map { it.toInt() })
        }

        createNotificationChannel()

        updateCurrentTime()
        startRepeatingTask()
    }

    private fun saveProcessedLogIds() {
        // Save processedLogIds to SharedPreferences
        editor.putStringSet(PROCESSED_LOG_IDS_KEY, processedLogIds.map { it.toString() }.toSet())
        editor.apply()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, OpeningActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun updateCurrentTime() {
        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        timeTextView.text = "Current time: $currentTime"
    }

    private fun fetchPills() {
        RetrofitInstance.getInstance().apiInterface.getPillSchedule().enqueue(object :
            Callback<UpcomingPills> {
            override fun onResponse(call: Call<UpcomingPills>, response: Response<UpcomingPills>) {
                if (response.isSuccessful) {
                    redFlag = false
                    val pills = response.body()?.upcomingPills ?: emptyList()
                    if (pills.isEmpty()) {
                        Log.d("MainActivity", "No upcoming pills")
                        noPillsTextView.visibility = View.VISIBLE
                        recyclerView.visibility = View.GONE
                    } else {
                        noPillsTextView.visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE
                        adapter.updatePills(pills)
                    }
                } else {
                    if (!redFlag) {
                        Toast.makeText(this@MainActivity, "Error fetching pill schedule", Toast.LENGTH_SHORT).show()
                    }
                    redFlag = true
                    noPillsTextView.visibility = View.GONE
                    Log.d("MainActivity", "Error fetching pill schedule")
                }
            }

            override fun onFailure(call: Call<UpcomingPills>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Failed to fetch pill schedule", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchLogsAndSendNotifications() {
        RetrofitInstance.getInstance().apiInterface.getLogs().enqueue(object :
            Callback<LogsResponse> {
            override fun onResponse(call: Call<LogsResponse>, response: Response<LogsResponse>) {
                if (response.isSuccessful) {
                    val logs = response.body()?.logs ?: emptyList()
                    sendNotificationIfRequired(logs)
                    Log.d("MainActivity", "Fetching logs")
                } else {
                    Log.d("MainActivity", "Error fetching logs")
                }
            }

            override fun onFailure(call: Call<LogsResponse>, t: Throwable) {
                Log.d("MainActivity", "Failed to fetch logs")
            }
        })
    }

    private fun startRepeatingTask() {
        fixedRateTimer("timer", false, 0L, 5000) {
            handler.post {
                updateCurrentTime()
                fetchPills()
                fetchLogsAndSendNotifications()
            }
        }
    }

    private fun sendNotificationIfRequired(logs: List<com.example.pilldispenser.Log>) {
        val currentTime = Calendar.getInstance()
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(currentTime.time)

        for (i in logs.indices.reversed()) {
            val log = logs[i]
            val logDate = log.date // Extract YYYY-MM-DD from log time

            // Check if the log is already processed
            /*if (processedLogIds.contains(log.id)) {
                Log.d("Notification","Logs already shown")
                continue
            }*/

            // Compare if the log date is today and the log time has already passed
            if (logDate == today && isTimePassed(log.time)) {
                sendNotification(log)
                processedLogIds.add(log.id)
                saveProcessedLogIds()
                Log.d("Notification", "Log is being shown")
                break // Exit loop after sending the first eligible notification
            }
        }
    }

    private fun isTimePassed(logTime: String): Boolean {
        return try {
            // If log time is in "HH:mm" format
            val logDateTime = SimpleDateFormat("HH:mm", Locale.getDefault()).parse(logTime)
            val currentTime = Calendar.getInstance().time

            // To compare only time (ignoring date), set the same date for both times
            val calendarLog = Calendar.getInstance().apply {
                time = logDateTime
                set(Calendar.YEAR, currentTime.year)
                set(Calendar.MONTH, currentTime.month)
                set(Calendar.DAY_OF_MONTH, currentTime.day)
            }

            logDateTime != null && calendarLog.time.before(currentTime)
        } catch (e: ParseException) {
            // Log the error and return false if parsing fails
            Log.e("MainActivity", "ParseException: Unparseable date: $logTime", e)
            false
        }
    }

    private fun sendNotification(log: com.example.pilldispenser.Log) {
        // Check if we have the necessary permission (e.g., VIBRATE)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.VIBRATE)
            == PackageManager.PERMISSION_GRANTED) {

            // We have the permission, proceed with creating and sending the notification
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_IMMUTABLE)

            val builder = NotificationCompat.Builder(this, "PillDispenserChannel")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Pill Log")
                .setContentText("${log.pill_name} is ${log.intake_status} at ${log.time}")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            with(NotificationManagerCompat.from(this)) {
                if (ActivityCompat.checkSelfPermission(
                        this@MainActivity,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    // ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    // public fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                    //                                        grantResults: IntArray)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    Log.d("Notification", "Stuck Here!")
                    return@with
                }
                notify(log.id, builder.build())
            }
        } else {
            // Handle the case where permission is not granted, if necessary
            // You can notify the user or handle it silently depending on your app's requirements
            Log.d("MainActivity", "Permission not granted for notifications")
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Pill Dispenser Channel"
            val descriptionText = "Channel for pill dispenser notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("PillDispenserChannel", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, retry sending notification
                    // You might want to re-call sendNotification(log) here
                } else {
                    // Permission denied, handle accordingly (e.g., show a message or disable notification feature)
                    Toast.makeText(this, "Permission denied, cannot send notifications", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
