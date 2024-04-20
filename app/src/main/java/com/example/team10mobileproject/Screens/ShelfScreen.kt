package com.example.team10mobileproject.Screens

import ShowPopup
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.team10mobileproject.Repo.Book
import com.example.team10mobileproject.ViewModel.FirebaseViewModel
import com.example.team10mobileproject.ui.theme.MontserratFontFamily


@Composable
fun ShelfScreen(
    navController: NavController = rememberNavController(),
    viewModel: FirebaseViewModel
){
    // Observing the shelfBooks LiveData from the ViewModel and converting it to a state
    val shelfBooks by viewModel.shelfBooks.observeAsState(initial = emptyList())
    // State for storing the list of books fetched from the ViewModel
    var books by remember { mutableStateOf(listOf<Book>()) }
    // State for storing the search text entered by the user
    var searchText by remember { mutableStateOf("") }
    // State for storing the filtered list of books based on the search text
    val filteredBooks = remember { mutableStateListOf<Book>() }
    // State to control the visibility of the popup
    var showPopup by remember { mutableStateOf(false) }
    // State to store the currently selected book
    var selectedBook by remember { mutableStateOf<Book?>(null) }
    // Fetching the shelf books from the ViewModel
    viewModel.getShelfBooks()
    // LaunchedEffect to fetch the shelf books when the screen is launched
    LaunchedEffect(Unit) {
        viewModel.getShelfBooks()
        books = shelfBooks
    }
    // LaunchedEffect to filter the books based on the search text
    LaunchedEffect(searchText) {
        filteredBooks.clear()
        if (searchText.isEmpty()) {
            filteredBooks.addAll(books)
        } else {
            filteredBooks.addAll(
                books.filter {
                    it.Course.contains(searchText, ignoreCase = true) || it.Title.contains(searchText, ignoreCase = true) || it.Description.contains(searchText, ignoreCase = true)
                }
            )
        }
    }

    // LazyColumn to display the list of books
    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        item {
            // Displaying the shelf number
            Text(
                text = "Shelf #${viewModel.shelfNumber.value}",
                fontFamily = MontserratFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier.padding(start = 22.dp, top = 18.dp)
            )
            // SearchBar for searching books
            SearchBar(
                searchText = viewModel.detectedText.value,
                onSearchTextChange = { newText ->
                    viewModel.detectedText.value = newText
                },
                onClearClick = {
                    viewModel.detectedText.value = ""
                    filteredBooks.clear()
                    filteredBooks.addAll(books)
                }
            )
            Divider()
        }
        // Displaying each book in the filtered list
        items(filteredBooks) { book ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 20.dp)
                    .clickable {
                        selectedBook = book
                        showPopup = true
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Displaying the book's image
                Image(
                    painter = loadNetworkImage(book.Url),
                    contentDescription = null,
                    modifier = Modifier
                        .size(110.dp, 155.dp)
                        .shadow(15.dp),
                    contentScale = ContentScale.Crop
                )
                // Displaying the book's title and description
                Column(
                    modifier = Modifier
                        .padding(top = 6.dp, start = 12.dp)
                        .height(150.dp)
                ) {
                    Text(
                        text = "Title:",
                        fontFamily = MontserratFontFamily,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = book.Title,
                        fontFamily = MontserratFontFamily,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.height(70.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Description:",
                        fontFamily = MontserratFontFamily,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "by ${book.Description}",
                        fontFamily = MontserratFontFamily,
                    )
                }
            }
        }
    }
    // Showing a popup with book details when a book is selected
    if (showPopup) {
        ShowPopup(
            viewModel = viewModel,
            navController = navController,
            sid = viewModel.sid, // Adjust this if necessary
            book = selectedBook ?: Book("", "", "", ""), // Use the selectedBook state
            onDismiss = { showPopup = false }
        )
    }
    // BottomBar for navigation
    Box(){
        BottomBar(
            modifier = Modifier.align(Alignment.BottomCenter),
            navController = navController
        )
    }
}




