package com.example.team10mobileproject.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Header(
    onNavigationIconClick: () -> Unit, // Function to be called when the navigation icon is clicked
    onFavoriteIconClick: () -> Unit, // Function to be called when the favorite icon is clicked
) {
    // A Row composable that fills the maximum width of the screen, has a fixed height of 56.dp,
    // uses the primary color from the MaterialTheme for its background, and has horizontal padding of 16.dp.
    // It aligns its children vertically in the center and arranges them horizontally with space between them.
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // An IconButton that displays an arrow back icon. When clicked, it triggers the onNavigationIconClick function.
        IconButton(onClick = onNavigationIconClick) {
            Icon(
                imageVector = Icons.Filled.ArrowBack, // The icon to be displayed
                contentDescription = "Open menu" // Accessibility description for the icon
            )
        }

        // An IconButton that displays a favorite border icon. When clicked, it triggers the onFavoriteIconClick function.
        IconButton(onClick = onFavoriteIconClick) {
            Icon(
                imageVector = Icons.Default.FavoriteBorder, // The icon to be displayed
                contentDescription = "Mark as favorite" // Accessibility description for the icon
            )
        }
    }
}