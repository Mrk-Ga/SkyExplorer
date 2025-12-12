package com.example.skyexplorer.photoShare

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File

fun shareImage(
    context: Context,
    imageFile: File,
    text: String
) {
    val shareUri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        imageFile
    )

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "image/*"
        putExtra(Intent.EXTRA_STREAM, shareUri)
        putExtra(Intent.EXTRA_TEXT, text)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    context.startActivity(
        Intent.createChooser(intent, "Udostępnij zdjęcie")
    )
}

@Composable
fun ShareButton(
    imagePath: String
) {
    val context = LocalContext.current

    IconButton(
        onClick = {
            val uri = Uri.parse(imagePath)
            val path = uri.path

            if (path == null) {
                Log.e("SHARE", "Uri path is null")
                return@IconButton
            }

            val file = File(path)
            Log.d("SHARE", "exists=${file.exists()}")

            if (file.exists()) {
                shareImage(
                    context = context,
                    imageFile = file,
                    text = "Zdjęcie nieba z aplikacji SkyExplorer"
                )
            }
        }
    ) {
        Icon(
            imageVector = Icons.Default.Share,
            contentDescription = "Udostępnij"
        )
    }
}
