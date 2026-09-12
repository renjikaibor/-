package com.aicodeassistant.utils

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.io.IOException
import java.security.GeneralSecurityException

object EncryptionUtils {

    private const val MASTER_KEY_ALIAS = "master_key"
    private const val PREF_NAME = "secure_prefs"

    fun getEncryptedSharedPreferences(context: Context): androidx.security.crypto.EncryptedSharedPreferences {
        val masterKey = getOrCreateMasterKey(context)
        return EncryptedSharedPreferences.create(
            PREF_NAME,
            masterKey,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    private fun getOrCreateMasterKey(context: Context): MasterKey {
        val keyGenParameterSpec = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .setUserAuthenticationRequired(false)
            .build()
        return keyGenParameterSpec
    }

    @Throws(GeneralSecurityException::class, IOException::class)
    fun encryptString(context: Context, plainText: String): String {
        val masterKey = getOrCreateMasterKey(context)
        val cipher = javax.crypto.Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, masterKey.key)
        val iv = cipher.iv
        val encrypted = cipher.doFinal(plainText.toByteArray())
        return android.util.Base64.encodeToString(iv + encrypted, android.util.Base64.DEFAULT)
    }

    @Throws(GeneralSecurityException::class, IOException::class)
    fun decryptString(context: Context, encryptedText: String): String {
        val masterKey = getOrCreateMasterKey(context)
        val decoded = android.util.Base64.decode(encryptedText, android.util.Base64.DEFAULT)
        val iv = decoded.copyOfRange(0, 12)
        val encrypted = decoded.copyOfRange(12, decoded.size)
        val cipher = javax.crypto.Cipher.getInstance("AES/GCM/NoPadding")
        val gcmSpec = javax.crypto.spec.GCMParameterSpec(128, iv)
        cipher.init(javax.crypto.Cipher.DECRYPT_MODE, masterKey.key, gcmSpec)
        val decrypted = cipher.doFinal(encrypted)
        return String(decrypted)
    }
}
