package com.vincent.givetake.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_DEFAULT
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.vincent.givetake.R
import com.vincent.givetake.ui.activity.home.MainActivity
import com.vincent.givetake.utils.Constant
import kotlin.random.Random

class MessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val type = message.data[Constant.FCM_TYPE]
        val itemName = message.data[Constant.FCM_ITEM_NAME]
        val chatName = message.data[Constant.FCM_CHAT_NAME]

        var notificationId = Random(System.currentTimeMillis()).nextInt()
        var channelId = "channel_id"
        if (type == Constant.FCM_TYPE_CHAT) {
            channelId = "chat_message"
            notificationId = 999
        }else if (type == Constant.FCM_TYPE_ITEM) {
            channelId = "item_message"
            notificationId = 998
        }

        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val builder = NotificationCompat.Builder(this, channelId)
        builder.setSmallIcon(R.drawable.ic_notifications)
        if (type == Constant.FCM_TYPE_ITEM) {
            builder.setContentTitle("Tawawan Barang")
            builder.setStyle(NotificationCompat.BigTextStyle().bigText("Tawaran baru untuk barang $itemName"))
        }else if (type == Constant.FCM_TYPE_CHAT) {
            builder.setContentTitle("Pesan Obrolan Baru")
            builder.setStyle(NotificationCompat.BigTextStyle().bigText("Pesan baru untuk barang $itemName"))
        }
        builder.priority = PRIORITY_DEFAULT
        builder.setContentIntent(pendingIntent)
        builder.setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "GiveTake App"
            val channelDesc = "Notification for Givetake App"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance)
            channel.description = channelDesc
            val notificationManager = getSystemService(NotificationManager::class.java)

            notificationManager.createNotificationChannel(channel)
        }

        val notificationManagerCompat = NotificationManagerCompat.from(this)
        notificationManagerCompat.notify(notificationId, builder.build())

    }

}