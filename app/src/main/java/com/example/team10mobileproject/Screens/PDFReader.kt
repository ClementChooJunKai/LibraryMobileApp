
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.team10mobileproject.Proximity.ProximitySensors
import com.example.team10mobileproject.Screens.BottomBar
import com.example.team10mobileproject.ViewModel.FirebaseViewModel
import com.example.team10mobileproject.ui.theme.gradientBackgroundBrush
import com.rajat.pdfviewer.PdfRendererView
import com.rajat.pdfviewer.compose.PdfRendererViewCompose

/**
 * Composable function for displaying a PDF reader.
 * @param modifier Modifier to adjust the layout.
 * @param navController NavController used for navigation within the app.
 * @param viewModel ViewModel for managing data and business logic.
 */
@Composable
fun PDFReader(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController(),
    viewModel: FirebaseViewModel
) {

    val context = LocalContext.current
    val near = remember { mutableStateOf(false) }
    val proximitySensors = remember { ProximitySensors(context) }
    val showDialog = remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        proximitySensors.startListening()
    }

    DisposableEffect(Unit) {
        onDispose {
            proximitySensors.stopListening() // Correctly using the instance method
        }
    }

    // Implement a callback to handle sensor events
    proximitySensors.setOnSensorValuesChangedListener { values ->
        // Assuming the first value is the distance in centimeters
        val distance = values.firstOrNull() ?: 0f
        near.value = distance < 5f // Update the 'near' state based on the distance
        if (near.value) {
            showDialog.value = true
        }
        Log.d("ProximitySensor", "Distance: $distance cm")
    }


    Box(modifier = Modifier
        .fillMaxSize()
        .background(
            brush = gradientBackgroundBrush(
                isVerticalGradient = true,
            )
        )) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Log.d("pdf link",viewModel.pdfUrl.value)
            MyPdfScreenFromUrl(
                url = viewModel.pdfUrl.value,
                modifier = Modifier.fillMaxSize()
            )
        }
        BottomBar(
            modifier = Modifier.align(Alignment.BottomCenter),
            navController = navController
        )
    }
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text("Reading Too Near") },
            text = { Text("Please move the device further away.") },
            confirmButton = {
                TextButton(onClick = { showDialog.value = false }) {
                    Text("OK")
                }

            }
        )
    }
}

/**
 * Composable function for displaying a PDF screen from a URL.
 * @param url The URL of the PDF.
 * @param modifier Modifier to adjust the layout.
 */
@Composable
fun MyPdfScreenFromUrl(url: String, modifier: Modifier = Modifier) {
    val lifecycleOwner = LocalLifecycleOwner.current
    PdfRendererViewCompose(
        modifier = modifier,
        url = url,
        lifecycleOwner = lifecycleOwner,
        statusCallBack = object : PdfRendererView.StatusCallBack {
            override fun onPdfLoadStart() {
                Log.i("statusCallBack", "onPdfLoadStart")
            }

            override fun onPdfLoadProgress(
                progress: Int,
                downloadedBytes: Long,
                totalBytes: Long?
            ) {
                //Download is in progress
            }

            override fun onPdfLoadSuccess(absolutePath: String) {
                Log.i("statusCallBack", "onPdfLoadSuccess")
            }

            override fun onError(error: Throwable) {
                Log.i("statusCallBack", "onError")
            }

            override fun onPageChanged(currentPage: Int, totalPage: Int) {
                //Page change. Not require
            }
        }
    )
}


