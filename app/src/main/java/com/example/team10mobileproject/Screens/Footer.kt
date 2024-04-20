package com.example.team10mobileproject.Screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.team10mobileproject.R
import com.example.team10mobileproject.Screen

@Composable
fun BottomBar(modifier: Modifier = Modifier, navController: NavController = rememberNavController()
) {
    // Observing the current back stack entry to get the current route
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    val currentDestinationRoute = navController.currentDestination?.route


    // Defining the navigation items with their corresponding routes and icons
    val navItems = listOf(
        Screen.Homepage.route to "home",
        Screen.LibraryCollectionScreen.route to "magnifyingglass", // This ShelfScreen should be replaced to search page or smthing.
        Screen.BorrowScreen.route to "library",
        Screen.SettingScreen.route to "profile" // @Jace, change this Screen.ShelfScreen to the path of profile/settings page.
    )

    // A Box composable that fills the maximum size, containing the navigation bar and a QR scanner button
    Box(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                .background(MaterialTheme.colorScheme.primary)
                .padding(vertical = 5.dp, horizontal = 0.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Spacer to align the navigation items to the center
            Spacer(modifier = Modifier.width(0.dp))
            // Iterating over each navigation item to create an IconButton for it
            navItems.forEach { (route, icon) ->
                Row(modifier=modifier ,verticalAlignment = Alignment.CenterVertically) {
                    // Determining if the current route matches the navigation item's route
                    val isSelected = currentRoute == route
                    // Adjusting the icon based on whether the navigation item is selected
                    val iconResource = if (isSelected) "${icon}filled" else icon

                    // Creating an IconButton for the navigation item
                    IconButton(onClick = { navController.navigate(route) },
                        modifier = Modifier.testTag("${iconResource}Icon") ) {
                        Icon(
                            painter = painterResource(id = getIconResourceId(iconResource)),
                            contentDescription = route,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(25.dp)
                        )
                    }
                }
            }
            // Spacer to align the navigation items to the center
            Spacer(modifier = Modifier.width(0.dp))
        }

        // A QR scanner button placed at the bottom center of the screen
        IconButton(
            onClick = {navController.navigate(Screen.QrScanner.route)} ,
            modifier = Modifier
                .padding(bottom = 35.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.onSecondary)
                .align(Alignment.BottomCenter)
                .padding(10.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.qr),
                contentDescription = "home",
                tint = Color.Black,
                modifier = Modifier.size(25.dp)
            )
        }
    }
}

// A helper function to get the resource ID of an icon based on its name
@Composable
fun getIconResourceId(icon: String): Int {
    return when (icon) {
        "home" -> R.drawable.home
        "homefilled" -> R.drawable.homefilled
        "magnifyingglass" -> R.drawable.magnifyingglass
        "magnifyingglassfilled" -> R.drawable.magnifyingglassfilled
        "library" -> R.drawable.library
        "libraryfilled" -> R.drawable.libraryfilled
        "profile" -> R.drawable.profile
        "profilefilled" -> R.drawable.profilefilled
        else -> throw IllegalArgumentException("Invalid icon name")
    }
}
