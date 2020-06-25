package com.drugstore.data.repository.core

import android.annotation.TargetApi
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties.*
import android.util.Base64
import java.io.IOException
import java.security.*
import java.security.spec.MGF1ParameterSpec.SHA1
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import javax.crypto.spec.OAEPParameterSpec
import javax.crypto.spec.PSource.PSpecified.DEFAULT

@TargetApi(Build.VERSION_CODES.M)
object CryptoUtils {

    private const val KEY_ALIAS = "key_for_password"
    private const val KEY_STORE = "AndroidKeyStore"
    private const val TRANSFORMATION = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding"

    val cipher: Cipher?
        get() {
            val cipher = createCipher(
                TRANSFORMATION
            )
                ?: return null
            val keyStore = createAndLoadKeyStore(
                KEY_STORE
            )
                ?: return null

            return if (generateKeyPair(
                    keyStore,
                    KEY_ALIAS,
                    KEY_STORE
                ) && initDecodeCipher(
                    keyStore,
                    KEY_ALIAS,
                    cipher
                )
            ) {
                cipher
            } else {
                null
            }
        }

    fun encode(inputString: String, cipher: Cipher): String? {
        val keyStore = createAndLoadKeyStore(
            KEY_STORE
        )
            ?: return null

        try {
            if (generateKeyPair(
                    keyStore,
                    KEY_ALIAS,
                    KEY_STORE
                ) && initEncodeCipher(
                    keyStore,
                    KEY_ALIAS,
                    cipher
                )
            ) {
                val bytes = cipher.doFinal(inputString.toByteArray())
                return Base64.encodeToString(bytes, Base64.NO_WRAP)
            }
        } catch (e: GeneralSecurityException) {
            e.printStackTrace()
        }
        return null
    }

    fun decode(encodedString: String, cipher: Cipher): String? {
        try {
            val bytes = Base64.decode(encodedString, Base64.NO_WRAP)
            return String(cipher.doFinal(bytes))
        } catch (e: GeneralSecurityException) {
            e.printStackTrace()
        }
        return null
    }

    private fun createCipher(transformation: String): Cipher? {
        try {
            return Cipher.getInstance(transformation)
        } catch (e: GeneralSecurityException) {
            e.printStackTrace()
        }
        return null
    }

    private fun createAndLoadKeyStore(type: String): KeyStore? {
        try {
            val keyStore = KeyStore.getInstance(type)
            keyStore.load(null)
            return keyStore
        } catch (e: GeneralSecurityException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    private fun generateKeyPair(keyStore: KeyStore, alias: String, provider: String): Boolean {
        try {
            if (keyStore.containsAlias(alias)) return true
        } catch (e: KeyStoreException) {
            e.printStackTrace()
            return false
        }

        try {
            val keyPairGenerator =
                KeyPairGenerator.getInstance(KEY_ALGORITHM_RSA, provider) ?: return false
            keyPairGenerator.initialize(
                KeyGenParameterSpec.Builder(alias, PURPOSE_ENCRYPT or PURPOSE_DECRYPT)
                    .setDigests(DIGEST_SHA256, DIGEST_SHA512)
                    .setEncryptionPaddings(ENCRYPTION_PADDING_RSA_OAEP)
                    .setUserAuthenticationRequired(true)
                    .build()
            )
            keyPairGenerator.generateKeyPair()
            return true
        } catch (e: GeneralSecurityException) {
            e.printStackTrace()
        } catch (e: NoClassDefFoundError) {
            e.printStackTrace()
        }
        return false
    }

    private fun initEncodeCipher(keyStore: KeyStore, alias: String, cipher: Cipher): Boolean {
        try {
            val key = keyStore.getCertificate(alias).publicKey

            // workaround for using public key
            // from https://developer.android.com/reference/android/security/keystore/KeyGenParameterSpec.html
            val unrestricted = KeyFactory.getInstance(key.algorithm).generatePublic(X509EncodedKeySpec(key.encoded))
            // from https://code.google.com/p/android/issues/detail?id=197719
            val spec = OAEPParameterSpec("SHA-256", "MGF1", SHA1, DEFAULT)

            cipher.init(Cipher.ENCRYPT_MODE, unrestricted, spec)
            return true
        } catch (exception: KeyPermanentlyInvalidatedException) {
            deleteInvalidKey(
                keyStore,
                alias
            )
        } catch (e: GeneralSecurityException) {
            e.printStackTrace()
        }
        return false
    }

    private fun initDecodeCipher(keyStore: KeyStore, alias: String, cipher: Cipher): Boolean {
        try {
            val key = keyStore.getKey(alias, null) as PrivateKey
            cipher.init(Cipher.DECRYPT_MODE, key)
            return true
        } catch (exception: KeyPermanentlyInvalidatedException) {
            deleteInvalidKey(
                keyStore,
                alias
            )
        } catch (e: GeneralSecurityException) {
            e.printStackTrace()
        }
        return false
    }

    private fun deleteInvalidKey(keyStore: KeyStore, alias: String) {
        try {
            keyStore.deleteEntry(alias)
        } catch (e: KeyStoreException) {
            e.printStackTrace()
        }
    }
}