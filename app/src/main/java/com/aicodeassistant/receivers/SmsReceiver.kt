package com.aicodeassistant.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Telephony
import android.telephony.SmsMessage
import com.aicodeassistant.domain.repository.ChatRepository
import com.aicodeassistant.domain.model.ChatMessage
import com.aicodeassistant.domain.model.MessageRole

class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION != intent.action) return

        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        messages.forEach { sms ->
            val sender = sms.originatingAddress
            val body = sms.messageBody
            val timestamp = sms.timestampMillis

            // Save as a system message or notify the chat service
            // For now, we'll just log it
            android.util.Log.d("SmsReceiver", "SMS from $sender: $body")
        }
    }
}
