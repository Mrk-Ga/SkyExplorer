package com.example.skyexplorer.photoGallery

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NavigateBefore
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.skyexplorer.components.BouncyButton
//import com.example.skyexplorer.components.BouncyButton
import com.example.skyexplorer.ui.theme.*


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PhotoGalleryScreen(
    viewModel: PhotoGalleryViewModel = viewModel(),
    onNavigateToConstellations: () -> Unit,
    constellationName: String
) {
    val photos by viewModel.photos.collectAsState()

    val filteredPhotos = remember(photos, constellationName) {
        viewModel.photoFilter(photos, constellationName)
    }

    val photoInfo = viewModel.loadConstellationsInfo(constellationName)

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { filteredPhotos.size }
    )



    Log.d("PHOTO INFORMATIONS", photoInfo.toString())

    Scaffold(
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(40.dp)
            ){
                BouncyButton(
                    onClick = onNavigateToConstellations,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.onBackground
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.NavigateBefore,
                        contentDescription = "Go to previous screen",
                        modifier = Modifier
                            .size(50.dp)
                    )
                }

                Spacer(
                    modifier = Modifier
                        .width(150.dp)
                )

                Box(
                    modifier = Modifier
                        .size(50.dp),
                    contentAlignment = Alignment.CenterEnd,


                ) {
                    if (filteredPhotos.isNotEmpty()) {
                        //Log.d("SHARE", "exists=${file.exists()}")
                        //Log.d("SHARE", "uri=${filteredPhotos[0].uri}")
                        ShareButton(
                            imagePath = filteredPhotos[pagerState.currentPage].uri,
                            viewModel
                        )
                    }
                }



            }
        }
    ) { innerPadding ->



        LazyColumn (
            modifier = Modifier
                .padding(top=30.dp),
            contentPadding = PaddingValues(
                    //top = innerPadding.calculateTopPadding(),
                    bottom = innerPadding.calculateBottomPadding()
            )

        ){

            item{
                PhotoPager(filteredPhotos, constellationName,
                    onDeletePhoto = {
                        viewModel.deletePhoto(it)
                    },
                    //pagerState = pagerState

                )
            }

            item{
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 20.dp),
                        //.size(height = 300.dp, width = 600.dp)

                    contentAlignment = Alignment.Center,

                ){
                    Column(
                        modifier = Modifier
                            .padding(5.dp)
                            .fillMaxSize()
                            .align(Alignment.Center),
                    ) {


                        Text(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally),
                            text = photoInfo.polish,
                            fontWeight = FontWeight.Bold,
                            fontSize = MaterialTheme.typography.headlineMedium.fontSize

                        )
                        Text(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally),
                            text = "lat. " + photoInfo.latin,
                            fontWeight = FontWeight.Bold,
                            fontSize = MaterialTheme.typography.headlineMedium.fontSize
                        )
                    }
                }
            }


            item{
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        //.size(height = 300.dp, width = 600.dp)
                        .padding(horizontal = 10.dp, vertical = 30.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Black),
                    contentAlignment = Alignment.Center,



                ){
                    Text(
                        text = photoInfo.description,
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxSize(),
                            //.align(Alignment.CenterHorizontally),
                        textAlign = TextAlign.Justify
                    )

                }

            }

        }








    }
}


