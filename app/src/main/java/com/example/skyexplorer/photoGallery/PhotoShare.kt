package com.example.skyexplorer.photoGallery

import android.util.Log
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.skyexplorer.components.BouncyButton
import java.io.File
import androidx.core.net.toUri


@Composable
fun ShareButton(
    imagePath: String,
    viewModel: PhotoGalleryViewModel
) {
    val context = LocalContext.current

    BouncyButton(
        onClick = {
            val uri = imagePath.toUri()
            val path = uri.path

            if (path == null) {
                Log.e("SHARE", "Uri path is null")
                return@BouncyButton
            }



            val file = File(path)
            Log.d("SHARE", "exists=${file.exists()}")

            if (file.exists()) {
                viewModel.shareImage(context,file,"Zdjęcie nieba z aplikacji SkyExplorer")
            }
        }
    ) {
        Icon(
            modifier =
                Modifier.size(35.dp),
            imageVector = Icons.Default.Share,
            contentDescription = "Udostępnij"
        )
    }
}
