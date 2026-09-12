package com.aicodeassistant.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        when (action) {
            "ACTION_DISMISS" -> {
                val notificationId = intent.getIntExtra("notification_id", -1)
                if (notificationId != -1) {
                    NotificationManagerCompat.from(context).cancel(notificationId)
                }
            }
            "ACTION_REPLY" -> {
                // Handle quick reply
                val replyText = intent.getStringExtra("reply_text")
                // TODO: Send reply to AI chat
            }
        }
    }
}
