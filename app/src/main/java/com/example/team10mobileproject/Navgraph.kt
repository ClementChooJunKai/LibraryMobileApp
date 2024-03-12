package com.example.team10mobileproject


import Homepage
import LoginScreen
import PDFReader
import android.util.Log

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.team10mobileproject.Screens.BookDetails
import com.example.team10mobileproject.Screens.BottomBar
import com.example.team10mobileproject.Screens.QRScanner
import com.example.team10mobileproject.Screens.ShelfScreen
import com.example.team10mobileproject.Screens.BorrowScreen
import com.example.team10mobileproject.Screens.SettingScreen
import com.example.team10mobileproject.ViewModel.FirebaseViewModel
import com.google.firebase.auth.FirebaseAuth

sealed class Screen(val route: String){
    object Homepage: Screen(route = "Homepage_screen")
    object LoginScreen: Screen(route = "Login_screen")
    object BookDetails: Screen(route = "BookDetails_screen")
    object QrScanner: Screen(route = "QrScanner_screen")
    object Footer: Screen(route = "Footer_screen")
    object PDFReader: Screen(route = "PDFReader_screen")
    object ShelfScreen : Screen(route="Shelf_screen")
    object BorrowScreen : Screen(route="Borrow_screen")
    object SettingScreen : Screen(route="Settings_screen")
}

@Composable
fun NavGraph(
    navController: NavHostController,
    activity: ComponentActivity,
    viewModel: FirebaseViewModel,

) {

    val isAuthenticated = FirebaseAuth.getInstance().currentUser != null
    val startDestination = if (isAuthenticated) Screen.Homepage.route else Screen.LoginScreen.route
    val uid = FirebaseAuth.getInstance().currentUser?.email
    val localPart = uid?.split("@")?.firstOrNull() ?: ""
    viewModel.sid.value = localPart
    Log.d("sid", viewModel.sid.value)
    NavHost(
        navController = navController,
        startDestination = startDestination // Set the start destination
    ) {
        composable(route = Screen.Homepage.route) {
            Homepage(navController = navController,viewModel= viewModel)
        }
        composable(route = Screen.LoginScreen.route) {
            LoginScreen(navController = navController, viewModel= viewModel)
        }
        composable(route = Screen.BookDetails.route) {
            BookDetails(navController = navController, viewModel= viewModel)
        }
        composable(route = Screen.Footer.route) {
            BottomBar(navController = navController)
        }
        composable(route = Screen.QrScanner.route) {
            QRScanner(
                navController = navController,
                activity = activity, // Provide the instance of ComponentActivity
                onBarcodeScanned = { barcode ->
                    // Handle the scanned barcode here
                    println("Scanned barcode: $barcode")
                }
            )
        }
        composable(route = Screen.PDFReader.route) {
            PDFReader(navController = navController)
        }

        composable(route = Screen.ShelfScreen.route) {
            ShelfScreen(navController = navController)
        }
        composable(route = Screen.BorrowScreen.route) {
            BorrowScreen(navController = navController, viewModel= viewModel)
        }
        composable(route = Screen.SettingScreen.route) {
            SettingScreen(navController = navController,viewModel= viewModel)
        }

    }
}
