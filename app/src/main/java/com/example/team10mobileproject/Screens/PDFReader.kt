
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.team10mobileproject.Screens.BottomBar
import com.example.team10mobileproject.ui.theme.gradientBackgroundBrush
import com.rajat.pdfviewer.PdfRendererView
import com.rajat.pdfviewer.compose.PdfRendererViewCompose
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun PDFReader(modifier: Modifier = Modifier,
             navController: NavController = rememberNavController()) {
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
            MyPdfScreenFromUrl(
                url = "https://css4.pub/2015/usenix/example.pdf",
                modifier = Modifier.fillMaxSize()
            )
            }
        BottomBar(
            modifier = Modifier.align(Alignment.BottomCenter),
            navController = navController
        )
    }
}

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

@Preview
@Composable
fun ReaderPreview() {
    PDFReader()
}