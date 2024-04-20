package com.example.team10mobileproject.Camera
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.team10mobileproject.ViewModel.FirebaseViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState


@OptIn(ExperimentalPermissionsApi::class)
    @Composable
    fun MainScreen(navController: NavController,viewModel: FirebaseViewModel) {

        val cameraPermissionState: PermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)

        MainContent(
            hasPermission = cameraPermissionState.status.isGranted,
            onRequestPermission = cameraPermissionState::launchPermissionRequest,
            navController,viewModel
        )
    }

    @Composable
    private fun MainContent(
        hasPermission: Boolean,
        onRequestPermission: () -> Unit,
        navController: NavController,viewModel: FirebaseViewModel
    ) {

        if (hasPermission) {
            CameraContent(navController,viewModel)
        } else {
            NoPermissionScreen(onRequestPermission)
        }
    }





