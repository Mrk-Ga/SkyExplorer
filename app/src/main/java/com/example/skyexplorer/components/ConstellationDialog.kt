package com.example.skyexplorer.components

import android.content.res.Resources
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.skyexplorer.camera.CameraViewModel

@kotlinx.serialization.Serializable
data class ConstellationInfo(
    val polish: String,
    val latin: String,
    val description: String
)


@Composable
fun DialogWithImage(
    viewModel: CameraViewModel,
    onDismissRequest: () -> Unit,
    onConfirmation: (name: String) -> Unit,

    ) {

    val constellationsInfoList = viewModel.loadConstellationsInfo()


    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(375.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            LazyColumn(
                modifier = Modifier
                    .heightIn(max = 400.dp) // maksymalna wysokość dialogu
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(constellationsInfoList.size) { index ->
                    val constellationInfo = constellationsInfoList[index]
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onConfirmation(constellationInfo.latin) }
                            .align(Alignment.CenterHorizontally)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .background(Color.Black),
                            contentAlignment = Alignment.Center
                        ){
                            Text(text=constellationInfo.polish)
                        }


                    }
                }
            }


            Button(
                onClick = onDismissRequest,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Zamknij")
            }
        }
    }
}

