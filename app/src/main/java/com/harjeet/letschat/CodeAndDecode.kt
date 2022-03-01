package com.harjeet.letschat

import android.os.Build
import androidx.annotation.RequiresApi
import java.io.UnsupportedEncodingException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

//Encrypt and Decrypt Data
object CodeAndDecode {
    private var secretKey: SecretKeySpec? = null
    private var key: ByteArray ?=null
    fun setKey(myKey: String) {
        var sha: MessageDigest? = null
        try {
            key = myKey.toByteArray(charset("UTF-8"))
            sha = MessageDigest.getInstance("SHA-1")
            key = sha.digest(key)
            key = Arrays.copyOf(key, 16)
            secretKey = SecretKeySpec(key, "AES")
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /************ here we are performing encryption */
    @RequiresApi(Build.VERSION_CODES.O)
    fun encrypt(stringToEncrypt: String, secret: String): String? {
        try {
            setKey(secret)
            val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            return Base64.getEncoder()
                .encodeToString(cipher.doFinal(stringToEncrypt.toByteArray(charset("UTF-8"))))
        } catch (e: Exception) {
            println("Error arised while encrypting: $e")
        }
        return null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    /************ here we are performing decryption */
    fun decrypt(stringToDecrypt: String?, secret: String): String? {
        try {
            setKey(secret)
            val cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING")
            cipher.init(Cipher.DECRYPT_MODE, secretKey)
            return String(cipher.doFinal(Base64.getDecoder().decode(stringToDecrypt)))
        } catch (e: Exception) {
            println("Error arised while decrypting: $e")
        }
        return null
    }
}