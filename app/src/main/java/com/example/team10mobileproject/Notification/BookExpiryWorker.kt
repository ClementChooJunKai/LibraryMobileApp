package com.example.team10mobileproject.Notification

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.team10mobileproject.MainActivity
import com.example.team10mobileproject.R

class BookExpiryWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    @SuppressLint("MissingPermission")
    override fun doWork(): Result {
        createNotificationChannel(applicationContext)
        val userId = inputData.getString("userId")
        val uniqueBookId = inputData.getString("uniqueBookId")
        // Define the Intent to start the BorrowPage activity
        val intent = Intent(applicationContext, MainActivity::class.java)
        // Create a PendingIntent that will start the BorrowPage activity
        // Use FLAG_IMMUTABLE for the PendingIntent
        val pendingIntent =
            PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        // Create a BigTextStyle for the notification
        val bigTextStyle = NotificationCompat.BigTextStyle()
            .bigText("Hello $userId, Your book $uniqueBookId is due to expire.")
        val builder = NotificationCompat.Builder(applicationContext, "book_expiry_channel")
            .setSmallIcon(R.drawable.notification) // Use your own icon
            .setContentTitle("Book Expiry Reminder")
            .setContentText("Hello $userId")
            .setContentText("Hello $userId, Your book $uniqueBookId is due to expire.")
            .setStyle(bigTextStyle)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            // Set the PendingIntent to the notification
            .setContentIntent(pendingIntent)
            // Set the notification to be auto-cancelled when the user taps it
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(applicationContext)) {
            notify(0, builder.build())
        }

        return Result.success()
    }

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Book Expiry Reminders"
            val descriptionText = "Notifications for book expiry reminders"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("book_expiry_channel", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}