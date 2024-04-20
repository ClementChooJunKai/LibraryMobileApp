package com.example.team10mobileproject.Camera
import android.Manifest
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.team10mobileproject.Screen
import com.example.team10mobileproject.Screens.BottomBar
import com.example.team10mobileproject.ViewModel.FirebaseViewModel
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.CompoundBarcodeView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun QRScanner(
    viewModel: FirebaseViewModel,
    activity: ComponentActivity,
    onBarcodeScanned: (String) -> Unit,
    navController: NavController = rememberNavController()
) {
    val context = LocalContext.current
    val barcodeView = rememberBarcodeView(viewModel,navController,context, activity, onBarcodeScanned)

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { barcodeView },
            modifier = Modifier.fillMaxSize()

        )
        BottomBar(
            modifier = Modifier.align(Alignment.BottomCenter),
            navController = navController
        )
    }
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            barcodeView.resume()
        }
    }

    DisposableEffect(Unit) {
        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        onDispose {
            barcodeView.pause()
        }
    }
}

@Composable
fun rememberBarcodeView(
    viewModel: FirebaseViewModel,
    navController: NavController = rememberNavController(),
    context: Context,
    activity: ComponentActivity,
    onBarcodeScanned: (String) -> Unit
): CompoundBarcodeView {
    val barcodeView = remember {
        CompoundBarcodeView(context).apply {
            decoderFactory = com.journeyapps.barcodescanner.DefaultDecoderFactory(listOf(BarcodeFormat.QR_CODE))
            initializeFromIntent(activity.intent)
            decodeSingle(callback(viewModel,navController,activity, onBarcodeScanned))
        }
    }

    return barcodeView
}

private fun callback(   viewModel: FirebaseViewModel, navController: NavController,activity: ComponentActivity, onBarcodeScanned: (String) -> Unit) = object : BarcodeCallback {
    override fun barcodeResult(result: BarcodeResult?) {
        result?.let {
            // Handle scanned result


            if (it.text != null) {
                Log.d(TAG, "QR code content: $it")
                viewModel.shelfNumber.value = it.text.toInt()
                viewModel.getShelfBooks()
                activity.lifecycleScope.launch {
                    Log.d(TAG, "Before delay")
                    delay(2000) // Delay for 5000 milliseconds (5 seconds)
                    Log.d(TAG, "After delay")
                    navController.navigate(Screen.ShelfScreen.route)
                }
            } else {
                Log.w(TAG, "Empty QR code content")
            }
        }
    }





}
