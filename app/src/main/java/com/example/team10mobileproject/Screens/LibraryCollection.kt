package com.example.team10mobileproject.Screens

import ShowPopup
import android.util.Log
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
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.team10mobileproject.Repo.Book
import com.example.team10mobileproject.Screen
import com.example.team10mobileproject.ViewModel.FirebaseViewModel
import com.example.team10mobileproject.ui.theme.MontserratFontFamily

/**
 * Composable function for displaying the library collection screen.
 * @param navController NavController used for navigation within the app.
 * @param viewModel ViewModel for managing data and business logic.
 */
@Composable
fun LibraryCollectionScreen(
    navController: NavController = rememberNavController(),
    viewModel: FirebaseViewModel
){
    // State to manage the visibility of the popup
    var showPopup by remember { mutableStateOf(false) }

    // State variables for managing book list and selection
    val filteredBooks = remember { mutableStateListOf<Book>() }
    var books by remember { mutableStateOf(listOf<Book>()) }
    var selectedBook by remember { mutableStateOf<Book?>(null) }

    // Fetch all books from Firebase upon screen initialization
    LaunchedEffect(key1 = true) {
        viewModel.retrieveAllBooks(
            onSuccess = { retrievedBooks ->
                // Update the list of books when retrieval is successful
                Log.d("LibraryCollectionPage", "Retrieved books: $retrievedBooks")
                filteredBooks.addAll(retrievedBooks)
                books = retrievedBooks
            },
            onFailure = { error ->
                // Handle failure (e.g., show error message)
                Log.e("Firebase", "Failed to retrieve books: $error")
            }
        )

    }

    // Filter books based on detected text changes
    LaunchedEffect(viewModel.detectedText.value) {
        filteredBooks.clear()
        if (viewModel.detectedText.value.isEmpty()) {
            filteredBooks.addAll(books)
        } else {
            filteredBooks.addAll(
                books.filter {
                    it.Title.contains(viewModel.detectedText.value, ignoreCase = true)|| it.Description.contains(viewModel.detectedText.value, ignoreCase = true) || it.Course.contains(viewModel.detectedText.value, ignoreCase = true)
                }
            )
        }
    }

    // Lazy column for displaying the list of books
    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        item {
            // Header section
            Column {
                Text(
                    text = "Library Collection",
                    fontFamily =  MontserratFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(start = 22.dp, top = 18.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ){
                    // Search bar and text button
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
                    Button(onClick = { navController.navigate(Screen.MainScreen.route)},
                        modifier = Modifier.size(90.dp, 50.dp)  ) {
                        Text(text = "Text" ,color = Color.White)
                    }

                }

                Divider()
            }
        }
        // List of filtered books
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
                // Book item layout
                Image(
                    painter = loadNetworkImage(book.Url),
                    contentDescription = null,
                    modifier = Modifier
                        .size(110.dp, 155.dp)
                        .shadow(15.dp),
                    contentScale = ContentScale.Crop
                )
                Column(
                    modifier = Modifier
                        .padding(top = 6.dp, start = 12.dp)
                        .height(150.dp)
                ) {
                    Text(
                        text = "Title:",
                        fontFamily =  MontserratFontFamily,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = book.Title,
                        fontFamily =  MontserratFontFamily,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.height(70.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Description:",
                        fontFamily =  MontserratFontFamily,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "by ${book.Description}",
                        fontFamily =  MontserratFontFamily,
                    )
                }

            }

        }

    }
    // Show popup if necessary
    if (showPopup) {
        ShowPopup(
            viewModel = viewModel,
            navController = navController,
            sid = viewModel.sid, // Adjust this if necessary
            book = selectedBook ?: Book("", "", "", ""), // Use the selectedBook state
            onDismiss = { showPopup = false }
        )
    }
    // Bottom navigation bar
    Box(){
        BottomBar(
            modifier = Modifier.align(Alignment.BottomCenter),
            navController = navController
        )
    }
}



