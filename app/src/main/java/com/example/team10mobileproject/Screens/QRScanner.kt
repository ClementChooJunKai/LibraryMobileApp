package com.example.team10mobileproject.Screens
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
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

import com.google.zxing.BarcodeFormat
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.CompoundBarcodeView

@Composable
fun QRScanner(
    activity: ComponentActivity,
    onBarcodeScanned: (String) -> Unit,
    navController: NavController = rememberNavController()
) {
    val context = LocalContext.current
    val barcodeView = rememberBarcodeView(context, activity, onBarcodeScanned)

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
    context: Context,
    activity: ComponentActivity,
    onBarcodeScanned: (String) -> Unit
): CompoundBarcodeView {
    val barcodeView = remember {
        CompoundBarcodeView(context).apply {
            decoderFactory = com.journeyapps.barcodescanner.DefaultDecoderFactory(listOf(BarcodeFormat.QR_CODE))
            initializeFromIntent(activity.intent)
            decodeSingle(callback(activity, onBarcodeScanned))
        }
    }

    return barcodeView
}

private fun callback(activity: ComponentActivity, onBarcodeScanned: (String) -> Unit) = object : BarcodeCallback {
    override fun barcodeResult(result: BarcodeResult?) {
        result?.let {
            // Handle scanned result
            Log.d(TAG, "Scanned QR code: ${it.text}")
            onBarcodeScanned(it.text)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it.text))
            activity.startActivity(intent)
        }
    }


    private fun onBarcodeScanned(text: String?) {
        if (text != null) {
            Log.d(TAG, "QR code content: $text")
        } else {
            Log.w(TAG, "Empty QR code content")
        }
    }

    override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) {
        // Do nothing for now
    }
}
