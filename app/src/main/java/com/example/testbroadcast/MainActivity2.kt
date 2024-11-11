package com.example.testbroadcast

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.testbroadcast.databinding.ActivityMain2Binding

class MainActivity2 : AppCompatActivity() {

    private val CHANNEL_ID = "interactive_notification_channel"
    private val NOTIFICATION_ID = 1
    private var likeCount = 0
    private var dislikeCount = 0

    private lateinit var likeCountTextView: TextView
    private lateinit var dislikeCountTextView: TextView
    private lateinit var binding: ActivityMain2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize TextViews
        likeCountTextView = binding.likeCountTextView
        dislikeCountTextView = binding.dislikeCountTextView

        createNotificationChannel()

        // Register receiver for "Like" and "Dislike" actions
        registerReceiver(notificationReceiver, IntentFilter("ACTION_LIKE"), RECEIVER_EXPORTED)
        registerReceiver(notificationReceiver, IntentFilter("ACTION_DISLIKE"), RECEIVER_EXPORTED)

        val buttonShowNotification = findViewById<Button>(R.id.buttonShowNotification)
        buttonShowNotification.setOnClickListener {
            showInteractiveNotification()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Interactive Notification Channel"
            val descriptionText = "Channel for interactive notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showInteractiveNotification() {
        // Intent for "Like" button
        val likeIntent = Intent("ACTION_LIKE")
        val likePendingIntent = PendingIntent.getBroadcast(
            this, 0, likeIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Intent for "Dislike" button
        val dislikeIntent = Intent("ACTION_DISLIKE")
        val dislikePendingIntent = PendingIntent.getBroadcast(
            this, 0, dislikeIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Create notification with Like and Dislike actions
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Do you like this?")
            .setContentText("Like: $likeCount  Dislike: $dislikeCount")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .addAction(R.drawable.ic_like, "Like", likePendingIntent)
            .addAction(R.drawable.ic_dislike, "Dislike", dislikePendingIntent)
            .setOnlyAlertOnce(true)  // Notification won’t repeatedly play sound

        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(
                    this@MainActivity2,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Request the permission if not granted
                ActivityCompat.requestPermissions(
                    this@MainActivity2,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1
                )
                return
            }
            notify(NOTIFICATION_ID, builder.build())
        }
    }

    // BroadcastReceiver to handle "Like" and "Dislike" button actions
    private val notificationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                "ACTION_LIKE" -> {
                    Toast.makeText(this@MainActivity2,"walawe",Toast.LENGTH_LONG).show()
                    likeCount++
                    updateLikeDislikeCount()
                    showInteractiveNotification()  // Update notification with new count
                }
                "ACTION_DISLIKE" -> {
                    dislikeCount++
                    updateLikeDislikeCount()
                    showInteractiveNotification()  // Update notification with new count
                }
            }
        }
    }

    // Function to update TextViews in main layout
    private fun updateLikeDislikeCount() {
        likeCountTextView.text = "Like: $likeCount"
        dislikeCountTextView.text = "Dislike: $dislikeCount"
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(notificationReceiver)
    }
}
