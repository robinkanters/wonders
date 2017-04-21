package com.robinkanters.wonders

import com.github.kittinunf.fuel.util.Base64
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.security.*
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object SecurityManager {
    val AES_ENCRYPTION_ALGORITHM = "AES"
    val CHAR_ENCODING = "UTF-8"
    val CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding"

    val charset: Charset = Charset.forName(CHAR_ENCODING)

    private fun String.withCipher(input: ByteArray, func: Cipher.() -> Unit): ByteArray {
        val cipher = Cipher.getInstance(this)
        cipher.func()

        return cipher.doFinal(input)
    }

    @Throws(NoSuchAlgorithmException::class, NoSuchPaddingException::class, InvalidKeyException::class, InvalidAlgorithmParameterException::class, IllegalBlockSizeException::class, BadPaddingException::class)
    fun decrypt(cipherText: ByteArray, key: ByteArray): ByteArray {
        return CIPHER_ALGORITHM.withCipher(cipherText) {
            init(2, SecretKeySpec(key, AES_ENCRYPTION_ALGORITHM), IvParameterSpec(ByteArray(16)))
        }
    }

    @Throws(NoSuchAlgorithmException::class, NoSuchPaddingException::class, InvalidKeyException::class, InvalidAlgorithmParameterException::class, IllegalBlockSizeException::class, BadPaddingException::class)
    fun encrypt(plainText: ByteArray, key: ByteArray, initialVector: ByteArray): ByteArray {
        return CIPHER_ALGORITHM.withCipher(plainText) {
            init(1, SecretKeySpec(key, AES_ENCRYPTION_ALGORITHM), IvParameterSpec(initialVector))
        }
    }

    @Throws(UnsupportedEncodingException::class)
    fun getKeyBytes(key: String): ByteArray {
        val keyBytes = ByteArray(16)
        val parameterKeyBytes = key.toByteArray(charset)
        System.arraycopy(parameterKeyBytes, 0, keyBytes, 0, Math.min(parameterKeyBytes.size, keyBytes.size))

        return keyBytes
    }

    @Throws(UnsupportedEncodingException::class, InvalidKeyException::class, NoSuchAlgorithmException::class, NoSuchPaddingException::class, InvalidAlgorithmParameterException::class, IllegalBlockSizeException::class, BadPaddingException::class)
    fun encrypt(plainText: String, key: String): String {
        val plainTextbytes = plainText.toByteArray(charset)
        val keyBytes = getKeyBytes(key)

        return Base64.encodeToString(encrypt(plainTextbytes, keyBytes, keyBytes), 0)
    }

    @Throws(KeyException::class, GeneralSecurityException::class, GeneralSecurityException::class, InvalidAlgorithmParameterException::class, IllegalBlockSizeException::class, BadPaddingException::class, IOException::class)
    fun decrypt(encryptedText: String, key: String): String {
        val cipheredBytes = Base64.decode(encryptedText, 0)
        val keyBytes = getKeyBytes(key)

        return String(decrypt(cipheredBytes, keyBytes), charset)
    }
}
