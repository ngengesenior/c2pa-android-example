@file:OptIn(ExperimentalTime::class)

package com.proofmode.c2pa.c2pa

import android.content.Context
import android.location.Location
import android.net.Uri
import com.proofmode.c2pa.utils.generateOrGetKeyPair
import com.proofmode.c2pa.utils.getAppVersionName
import com.proofmode.c2pa.utils.keyPairToPemStrings
import org.contentauth.c2pa.C2PA
import org.contentauth.c2pa.SignerInfo
import org.contentauth.c2pa.SigningAlgorithm
import java.io.File
import java.io.FileOutputStream
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Creates a temporary local file from a given content URI.
 */
private fun createTempFileFromUri(context: Context, uri: Uri): File? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        // Create a temporary file in the app's cache directory
        val tempFile = File.createTempFile("c2pa_temp", uri.lastPathSegment, context.cacheDir)
        // Copy the content
        FileOutputStream(tempFile).use { outputStream ->
            inputStream.copyTo(outputStream)
        }
        inputStream.close()
        tempFile
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun signWithC2PA(uri: Uri, context: Context,fileFormat: String,
                   location: Location?) {
    // 1. Create a temporary file from the source URI to work with
    val tempFile = createTempFileFromUri(context, uri) ?: return

    // 2. Set up the signer and manifest
    val keyPair = generateOrGetKeyPair(context.applicationContext.packageName)
    val (publicKeyPem, privateKeyPem) = keyPairToPemStrings(keyPair)

    // The key generation is RSA, so the algorithm must be PS256.
    if (publicKeyPem == null || privateKeyPem == null) {
        // Cannot proceed without keys. Clean up and return.
        tempFile.delete()
        return
    }
    val signerInfo = SignerInfo(SigningAlgorithm.PS256, publicKeyPem, privateKeyPem)

    // Create a placeholder manifest

    val manifest = ManifestBuilder(
        "${context.packageName}/${context.getAppVersionName()?:""}",
        fileFormat
    )
        .addAction("c2pa.created",
            whenIso = Clock.System.now().toString(),
            softwareAgent = "ProofmodeC2PA,${context.packageName}/${context.getAppVersionName()?:""}")
        .addAuthorInfo("ProofmodeC2PA,${context.packageName}/${context.getAppVersionName() ?: ""}",
            description = "Test app for C2PA by Proofmode")

        .apply {
            location?.let {
                addLocationInfo(it.latitude,it.latitude)
            }
        }
        .toJson()



    try {
        // 3. Sign the temporary file in-place
        C2PA.signFile(tempFile.absolutePath, tempFile.absolutePath, manifest, signerInfo)

        // 4. Write the signed temporary file back to the original URI
        context.contentResolver.openOutputStream(uri, "w")?.use { outputStream ->
            tempFile.inputStream().use { inputStream ->
                inputStream.copyTo(outputStream)
            }
        }
    } catch (e: Exception) {
        // Handle potential signing or writing errors
        e.printStackTrace()
    } finally {
        // 5. Clean up the temporary file
        tempFile.delete()
    }
}
