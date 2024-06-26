package com.example.team10mobileproject.Screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.team10mobileproject.R
import com.example.team10mobileproject.Screen
import com.example.team10mobileproject.ViewModel.FirebaseViewModel

/**
 * Composable function for displaying the setting screen.
 * @param modifier The modifier for the setting screen.
 * @param navController The navigation controller.
 * @param viewModel The view model to manage Firebase data.
 */
@Composable
fun SettingScreen(modifier: Modifier = Modifier,
                   navController: NavController = rememberNavController(),
                           viewModel: FirebaseViewModel
) {
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {

    }

    DisposableEffect(Unit) {
        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        onDispose {

        }
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.getStudentName()


    }
    Box(modifier = Modifier
        .fillMaxSize()
        .background(
            color = MaterialTheme.colorScheme.background
        )
    ) {
        UserProfileScreen(    viewModel,navController)
        BottomBar(
            modifier = Modifier.align(Alignment.BottomCenter),
            navController = navController
        )
    }
}

/**
 * Composable function for displaying the user profile screen.
 * @param viewModel The view model to manage Firebase data.
 * @param navController The navigation controller.
 */
@Composable
fun UserProfileScreen(    viewModel: FirebaseViewModel,  navController: NavController = rememberNavController()) {
    var text by remember { mutableStateOf("") }
    var checked by remember { mutableStateOf(true) }

    Box(modifier = Modifier
        .fillMaxSize()
    ) {
        Image(
            painter = rememberAsyncImagePainter("https://play-lh.googleusercontent.com/C9CAt9tZr8SSi4zKCxhQc9v4I6AOTqRmnLchsu1wVDQL0gsQ3fmbCVgQmOVM1zPru8UH=w240-h480-rw"),
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .align(Alignment.TopCenter)
                .padding(top = 20.dp)
        )
        Text(
            text = viewModel.Name.value,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 130.dp)
        )
        Text(
            text = viewModel.sid.value+"@sit.singaporetech.edu.sg",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 160.dp)
        )
        OutlinedTextField(
            value = viewModel.Name.value,
            onValueChange = { text = it },
            label = { Text("Display Name") },
            modifier = Modifier
                .width(350.dp)
                .align(Alignment.TopCenter)
                .padding(top = 200.dp)
        )
        Column(
            modifier = Modifier
                .padding(top = 300.dp, start = 20.dp)
                .align(Alignment.TopCenter)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(top = 20.dp)
                    .clickable(onClick = {
                        viewModel.signOut()
                        navController.navigate(Screen.LoginScreen.route)

                    })

            ) {


                    Icon(
                        painter = painterResource(id = R.drawable.logout),
                        contentDescription = "Logout icon",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(24.dp) // Adjust the size as needed
                    )


                Text(
                    text = "Logout",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

