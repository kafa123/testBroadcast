package com.example.testbroadcast

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.testbroadcast.databinding.ActivityMainBinding
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val CHANNEL_ID = "download_notification_channel"
    private val NOTIFICATION_ID = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Membuat Notification Channel (untuk Android Oreo dan yang lebih baru)
        createNotificationChannel()

        val buttonStartDownload = binding.buttonStartDownload
        buttonStartDownload.setOnClickListener {
            showDownloadProgressNotification()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Download Notification Channel"
            val descriptionText = "Channel untuk notifikasi progress download"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showDownloadProgressNotification() {
        // Membuat builder untuk notifikasi
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)  // Tambahkan icon download
            .setContentTitle("Mengunduh File")
            .setContentText("Sedang mengunduh...")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOnlyAlertOnce(true)  // Notifikasi tidak akan berulang kali memberi suara

        val notificationManager = NotificationManagerCompat.from(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        // Gunakan Coroutine untuk mengupdate progress notifikasi
        CoroutineScope(Dispatchers.IO).launch {
            for (progress in 0..100 step 10) {
                delay(500)  // Simulasi waktu unduhan per 10%

                // Update progress notifikasi
                builder.setProgress(100, progress, false)

                notificationManager.notify(NOTIFICATION_ID, builder.build())
            }

            // Mengubah notifikasi menjadi "selesai" ketika progress mencapai 100%
            builder.setContentText("Unduhan selesai")
                .setProgress(0, 0, false)
            notificationManager.notify(NOTIFICATION_ID, builder.build())
        }
    }
}
