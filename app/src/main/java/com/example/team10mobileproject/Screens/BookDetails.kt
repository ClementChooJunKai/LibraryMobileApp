package com.example.team10mobileproject.Screens

import ImageWithGridOverlay
import ShowPopup
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.team10mobileproject.R
import com.example.team10mobileproject.Repo.Book
import com.example.team10mobileproject.ViewModel.FirebaseViewModel
import com.example.team10mobileproject.ui.theme.MontserratFontFamily
import loadNetworkImage


val images = listOf(

    R.drawable.book1, // Replace "image2" with the name of your local drawable resource
    R.drawable.book2, // Replace "image3" with the name of your local drawable resource
    R.drawable.book3
)
@Composable
fun BookDetails(modifier: Modifier = Modifier,
                navController: NavController = rememberNavController(),
                viewModel: FirebaseViewModel) {
    var showPopupOthers by remember { mutableStateOf(false) }
    var showPopup by remember { mutableStateOf(false) }
    var showPopupMap by remember { mutableStateOf(false) }
    val selectedBook by viewModel.selectedBook.observeAsState()
    val location by viewModel.location.observeAsState(Pair(0, 0)) // Default value is (0, 0)
    val wishlistBooks by viewModel.wishlistedBooks.observeAsState(emptyList())
    val course by viewModel.course.observeAsState(initial = "")
    val isInWishlist = wishlistBooks.any { it.Title == selectedBook?.Title }
    var books by remember(viewModel) {
        mutableStateOf<List<Book>>(emptyList())
    }

    LaunchedEffect(Unit) {
        viewModel.getWishlistedBooks()
        viewModel.getCourse()
    }
    LaunchedEffect(Unit) {
        selectedBook?.let { viewModel.observeBookLocation(it.Title) }
    }
    LaunchedEffect(key1 = course) {
        viewModel.retrieveBookOnCourse(
            course,
            onSuccess = { retrievedBooks ->
                // Update the list of books when retrieval is successful
                Log.d("Firebase", "Retrieved books: $retrievedBooks")
                books = retrievedBooks
            },
            onFailure = { error ->
                // Handle failure (e.g., show error message)
                Log.e("Firebase", "Failed to retrieve books: $error")
            }
        )

    }

    Box(modifier = Modifier.fillMaxSize()) {


        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .testTag("BookDetailsLazyColumn")
        ) {
            item {
                // Use the Header composable at the top

                LaunchedEffect(selectedBook) {
                    selectedBook?.let { book ->
                        viewModel.observeHardCopies(book.Title) // Assuming book.Title is the book ID
                        viewModel.observeSoftCopies(book.Title)
                    }
                }

                Column {



                    Spacer(modifier = Modifier.height(25.dp)) // Add some vertical space
                    Row {
                        Spacer(modifier = Modifier.width(80.dp)) // Add some vertical space
                        selectedBook?.let { loadNetworkImage(it.Url) }?.let {
                            Image(
                                painter = it,
                                contentDescription = null,
                                modifier = Modifier.size(200.dp)
                            )
                        }
                        IconButton(onClick = { selectedBook?.let { viewModel.markBookAsFavorite(it) } },modifier = Modifier.testTag("WishlistIcon")) {
                            Icon(
                                imageVector = Icons.Default.FavoriteBorder,
                                contentDescription = "Mark as favorite",
                                tint = if (isInWishlist) Color.Red else Color.Unspecified
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp)) // Add some vertical space
                    selectedBook?.let {
                        Text(
                            text = it.Title,
                            fontFamily = MontserratFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 24.sp,
                            textAlign = TextAlign.Center ,
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.CenterHorizontally)
                        )
                    }
                    Spacer(modifier = Modifier.height(40.dp)) // Add some vertical space


                    Text(
                        text = "What's it about?",
                        fontSize = 20 .sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,


                        )
                    selectedBook?.let {
                        Text(
                            text = it.Description,
                            color = Color.Black
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Spacer(modifier = Modifier.width(50.dp)) // Add some horizontal space
                        Button(
                            onClick = { showPopupOthers = true },
                            modifier = Modifier
                                .padding(16.dp)
                                .testTag("BorroWButton")
                                .background(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(8.dp)
                                )
                        ) {
                            Text(text = "Borrow", color = Color.White)
                        }
                        Button(
                            onClick = { showPopupMap = true },
                            modifier = Modifier
                                .padding(10.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(8.dp)
                                )
                        ) {
                            Text(text = "Location", color = Color.White)
                        }
                    }

                }
                Text(
                    text = "More Like This",
                    fontFamily = MontserratFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )




                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    itemsIndexed(books) { index, book ->
                        Box(modifier = Modifier.clickable {
                            // Update the selectedBook state with the clicked book
                            viewModel.updateSelectedBook(book)
                            showPopup = true
                        }) {
                            Card(
                                modifier = Modifier.height(150.dp),
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = 8.dp
                                )
                            ) {
                                // Assuming `imageUrl` is the property of the Book class
                                Image(
                                    painter = loadNetworkImage(book.Url),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .width(100.dp)
                                        .height(150.dp),
                                    contentScale = ContentScale.FillBounds
                                )
                            }
                        }
                    }
                }
                if (showPopup) {
                    ShowPopup(
                        viewModel = viewModel,
                        navController = navController,
                        sid = viewModel.sid, // Corrected here
                        book = selectedBook ?: Book("", "", "",""), // Use the selectedBook state
                        onDismiss = { showPopup = false }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))


            }
        }
            BottomBar(
                modifier = Modifier.align(Alignment.BottomCenter),
                navController = navController
            )

            // Show the popup if the state is set to true
            if (showPopupOthers) {
                ShowBorrowPopup(
                    showPopup = showPopupOthers,
                    onDismiss = { showPopupOthers = false },
                    onBorrowTypeSelected = { type ->
                        showPopupOthers = false
                    },
                    selectedBook?.Title,
                    viewModel

                )
            }
        if (showPopupMap) {
            ImageWithGridOverlay(showDialog = showPopupMap,
                onClose = { showPopupMap = false },
                xPosition = location.first, yPosition = location.second)

        }

    }
}
@Composable
fun ShowBorrowPopup(
    showPopup: Boolean,
    onDismiss: () -> Unit,
    onBorrowTypeSelected: (String) -> Unit,
    title: String?,
    viewModel: FirebaseViewModel
) {
    val hardCopies by viewModel.hardCopies.observeAsState(0)
    val softCopies by viewModel.softCopies.observeAsState(0)
    val context = LocalContext.current // Assuming you are using Compose
    val sideEffectExecuted = remember { mutableStateOf(false) }
    if (showPopup) {
        Dialog(onDismissRequest = onDismiss) {
            // Popup content
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(8.dp)
                    ) // Set background color to transparent
                    .size(300.dp)

            ) {

                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Choose Type",
                        fontFamily = FontFamily.Serif,
                        textAlign = TextAlign.Center,

                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Borrow Type Buttons
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Physical Copies Left: $hardCopies",
                                fontSize = 16.sp,
                                color = Color.Black
                            )
                            Button(onClick = {
                                if (title != null) {
                                    viewModel.borrowBook(title, "hard")

                                }

                            }) {
                                Text("Physical", fontFamily = FontFamily.Serif, color = Color.White)
                            }

                        }
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Virtual Copies Left: $softCopies",
                                fontSize = 16.sp,
                                color = Color.Black
                            )
                            Button(modifier =Modifier.testTag("Virtual"), onClick={
                                if (title != null) {
                                    viewModel.borrowBook(title, "soft")

                                }
                            }) {
                                Text("Virtual", fontFamily = FontFamily.Serif, color = Color.White)
                            }

                        }
                        if (viewModel.borrowBookSuccess.value) {
                            Toast.makeText(context, "Book borrowed successfully!", Toast.LENGTH_SHORT).show()
                            // Reset borrowBookSuccess to false after showing the toast
                            viewModel.borrowBookSuccess.value = false
                        }


                    }
                }
            }
        }




    }
}



