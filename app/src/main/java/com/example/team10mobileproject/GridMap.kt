import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.team10mobileproject.R

@Composable
fun ImageWithGridOverlay(showDialog: Boolean, onClose: () -> Unit, xPosition: Int, yPosition: Int) {
    if (showDialog) {
        Dialog(onDismissRequest = { onClose() }) {
            BoxWithConstraints {
                val imageSize = maxWidth.coerceAtMost(maxHeight)
                Image(
                    painter = painterResource(id = R.drawable.map),
                    contentDescription = "Library Grid",
                    modifier = Modifier.size(imageSize)
                )

                // Calculate the position of the cross based on the grid size
                val crossPositionX = xPosition
                val crossPositionY = yPosition

                // Place the cross image at the calculated position
                Image(
                    painter = painterResource(id = R.drawable.close), // Use your cross image here
                    contentDescription = "Cross",
                    modifier = Modifier
                        .size(20.dp) // Adjust the size as needed
                        .align(Alignment.TopStart)
                        .offset(x = crossPositionX.dp, y = crossPositionY.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ImageWithGridOverlayPreview() {
    ImageWithGridOverlay(showDialog = true, onClose = { /* Handle close action */ }, xPosition = 100, yPosition = 150)
}