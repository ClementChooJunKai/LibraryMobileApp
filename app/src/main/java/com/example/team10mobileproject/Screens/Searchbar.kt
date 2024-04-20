package com.example.team10mobileproject.Screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.team10mobileproject.R

/**
 * Composable function for displaying a search bar.
 * @param searchText The current text in the search bar.
 * @param onSearchTextChange Callback function invoked when the search text changes.
 * @param onClearClick Callback function invoked when the clear icon is clicked.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    onClearClick: () -> Unit,

){
    var isCrossIconVisible by remember { mutableStateOf(false) }
    LaunchedEffect(searchText) {
        isCrossIconVisible = searchText.isNotEmpty()
    }
    TextField(
        value = searchText,
        onValueChange = { onSearchTextChange(it) },
        modifier = Modifier.testTag("Searchbar")

            .padding(horizontal = 16.dp, vertical = 8.dp),
        placeholder = {
            Text(
                text = "Title, Author, ISBN",
                color = Color.Gray
            )
        },
        shape = RoundedCornerShape(percent = 50),
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.magnifyingglass),
                contentDescription = "Search Icon"
            )
        },
        trailingIcon = {
            if (isCrossIconVisible) {
                IconButton(onClick = {
                    onSearchTextChange("")
                    onClearClick()
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.cross_icon),
                        contentDescription = "Clear Icon"
                    )
                }
            }
        },
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        )
    )
}