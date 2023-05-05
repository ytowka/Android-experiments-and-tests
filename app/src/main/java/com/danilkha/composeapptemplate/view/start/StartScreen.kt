package com.danilkha.composeapptemplate.view.start

import android.Manifest
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.danilkha.composeapptemplate.utils.currentViewModel
import com.danilkha.composeapptemplate.utils.findActivity

@Composable
fun StartScreen(
    viewModel: StartViewModel = currentViewModel { it.startViewModel() },
    onImageClicked: (uri: String) -> Unit,
) {
    val state: StartState by viewModel.state.collectAsState()

    var currentImageUri: Uri = Uri.EMPTY
    val takePictureResult = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()){ success ->
        if(success){
            viewModel.processEvent(StartUserEvent.ImageTaken(currentImageUri.toString()))
        }
    }

    val requiredStoragePermission by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        }else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
    }
    val requiredCameraPermission = Manifest.permission.CAMERA

    val requestPermissionsContract = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        //viewModel.processEvent(StartUserEvent.CameraPermissionGranted(isGranted))
        viewModel.processEvent(StartUserEvent.GetAllImages)
    }

    val context = LocalContext.current
    LaunchedEffect(Unit){
        val storagePermission = shouldShowRequestPermissionRationale(context.findActivity(), requiredStoragePermission)
        val cameraPermission = shouldShowRequestPermissionRationale(context.findActivity(), requiredCameraPermission)
        if(storagePermission || cameraPermission){
            requestPermissionsContract.launch(arrayOf(requiredCameraPermission, requiredStoragePermission))
        }else{
            viewModel.processEvent(StartUserEvent.GetAllImages)
        }
    }

    ImageList(
        images = state.images,
        onImageClicked = onImageClicked,
        onTakeImageClicked = {
            //takePictureResult.launch()
        }
    )
}

@Composable
fun ImageList(
    images: List<String>,
    onImageClicked: (uri: String) -> Unit,
    onTakeImageClicked: () -> Unit,
){
    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ){
        item {
            TakeImageButton(onTakeImageClicked)
        }
        items(images){
            ImageCell(it){
                onImageClicked(it)
            }
        }
    }
}

@Composable
fun ImageCell(
    uri: String,
    onClick: () -> Unit
){
    BoxWithConstraints(
        modifier = Modifier
            .clickable{onClick()}
            .aspectRatio(1f)
            .clip(MaterialTheme.shapes.medium)
    ){
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(uri)
                .size(with(LocalDensity.current) { maxWidth.roundToPx() })
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun TakeImageButton(onClick: () -> Unit){
    Box(
        modifier = Modifier
            .clickable{onClick()}
            .aspectRatio(1f)
            .clip(MaterialTheme.shapes.medium)
            .background(color = Color.Gray),
        Alignment.Center
    ){
        Text("Take photo")
    }
}

