
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.team10mobileproject.Screens.BottomBar


import com.example.team10mobileproject.R
import com.example.team10mobileproject.Repo.Book
import com.example.team10mobileproject.Screen


import com.example.team10mobileproject.ViewModel.FirebaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch



@Composable
fun loadNetworkImage(url: String): Painter {
    return rememberAsyncImagePainter(url)
}


@Composable
fun Homepage(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController(),
    viewModel: FirebaseViewModel,

) {
    val recentlyViewedBooks by viewModel.recentlyViewedBooks.observeAsState(initial = emptyList())
    // State to manage the visibility of the popup
    var showPopup by remember { mutableStateOf(false) }
    var clickedBookIndex by remember { mutableStateOf(0) }
    var selectedBook by remember { mutableStateOf<Book?>(null) }
    var books by remember(viewModel) {
        mutableStateOf<List<Book>>(emptyList())
    }

    Log.d("SID_Value", "SID value: ${viewModel.sid.value}") // Log the value of sid


    viewModel.getRecentlyViewedBooks(viewModel.sid.value)
    LaunchedEffect(key1 = Unit) {
        viewModel.getRecentlyViewedBooks(viewModel.sid.value)
    }
    LaunchedEffect(key1 = true) {
        viewModel.retrieveAllBooks(
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 15.dp)
        ) {
            // Your content goes here
            Spacer(modifier = Modifier.height(30.dp))
            HeadingItem(viewModel.sid)
            Spacer(modifier = Modifier.height(8.dp))

            Spacer(modifier = Modifier.height(5.dp))
            CarouselSlider(books) { index ->
                // Update the state to show the popup and set the clicked book index
                clickedBookIndex = index
                selectedBook = books.getOrNull(index)
                showPopup = true
            }

            RecommendedBooks(
                recentlyViewedBooks = recentlyViewedBooks,
                onBookClick = { book ->
                    selectedBook = book
                    showPopup = true
                }
            )
        }

    // Align BottomBar directly to the bottom of the Box
    BottomBar(
        modifier = Modifier.align(Alignment.BottomCenter),
        navController = navController
    )
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
    }

    // Show the popup if the state is set to true



@Composable
fun ShowPopup(
    viewModel: FirebaseViewModel,
    navController: NavController,
    sid: MutableState<String>,
    book: Book,
    onDismiss: () -> Unit
) {
    viewModel.recentlyViewed(book.Title, sid = sid.value)
    Dialog(onDismissRequest = onDismiss) {
        // Popup content
        Box(
            modifier = Modifier
                .padding(16.dp)
                .background(Color.White)
                .size(500.dp)
                .clip(RoundedCornerShape(8.dp))
        ) {
            Column(
                modifier = Modifier
                    .padding(25.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Use LazyColumn to allow scrolling for long descriptions
                LazyColumn(
                    modifier = Modifier.weight(1f), // This makes the LazyColumn take up available space
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Text(
                            text = "Title: ${book.Title}",
                            style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    item {
                        Text(
                            text = "Description: ${book.Description}",
                            style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp)) // Add some space between the content and the buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = {
                        viewModel.setSelectedBook(book)
                        navController.navigate(Screen.BookDetails.route) }) {
                        Text("Read More")
                    }
                    Button(onClick = onDismiss) {
                        Text("Close")
                    }
                }
            }
        }
    }
}


@Composable
private fun CarouselSlider(books: List<Book>, onItemClick: (Int) -> Unit) {
    var index by remember { mutableStateOf(0) }
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = true, block = {
        coroutineScope.launch {
            while (true) {
                delay(5000)
                if (index == books.size - 1) index = 0
                else index++
                scrollState.animateScrollToItem(index)
            }
        }
    })

    // Listen for scroll state changes
    LaunchedEffect(scrollState) {
        snapshotFlow { scrollState.firstVisibleItemIndex }
            .collect { visibleItemIndex ->
                index = visibleItemIndex
            }
    }

    Column(
        modifier = Modifier.padding(5.dp)
    ) {

        Text(
            text = "For you",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.padding(bottom = 15.dp)
        )
        Box(modifier = Modifier) {

            LazyRow(
                state = scrollState,
                modifier = Modifier,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                itemsIndexed(books) { index, book ->
                    Box(modifier = Modifier.clickable { onItemClick(index) }) {
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

        }
        Box(
            modifier = Modifier
                .width(70.dp)
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally) // Center horizontally
                .padding(top = 20.dp) // Add padding if needed
        ) {
            DotIndicator(selectedIndex = index, totalItems = books.size) { newIndex ->
                index = newIndex
                coroutineScope.launch {
                    scrollState.animateScrollToItem(index)
                }
            }
        }

    }
}

@Composable
private fun DotIndicator(selectedIndex: Int, totalItems: Int, onDotClick: (Int) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        modifier = Modifier.padding(top = 6.dp)
    ) {
        repeat(totalItems) { index ->
            Box(
                modifier = Modifier
                    .size(13.dp)
                    .clip(CircleShape)
                    .background(if (index == selectedIndex) MaterialTheme.colorScheme.onPrimary else Color.LightGray)
                    .clickable {
                        onDotClick(index)
                    }
            )
        }
    }
}


@Composable
fun HeadingItem(sid: MutableState<String>){
    Column {
        Text(
            text = "Welcome ",
            style = MaterialTheme.typography.titleLarge,
            fontStyle = FontStyle.Normal,
            modifier = Modifier.padding(bottom =  8.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = sid.value +" !",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            fontStyle = FontStyle.Normal,
            modifier = Modifier.padding(bottom =  16.dp)
        )
    }
    Box(
        modifier = Modifier.shadow(
            elevation = 5.dp, // Set the elevation of the shadow
            shape = RoundedCornerShape(10.dp) // Set the shape of the shadow
        )
    ) {
        Text(
            text = "Course: Computing Science",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .padding(bottom = 4.dp)
                .fillMaxWidth()
                .background(Color.White)
                .padding(5.dp)
                .align(Alignment.Center)
        )
    }



}

@Composable
fun RecommendedBooks(
    recentlyViewedBooks: List<Book>,
    onBookClick: (Book) -> Unit // This is the callback function
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .height(220.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
        ) {
            Text(
                text = "Recently Viewed",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(bottom = 5.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                for (book in recentlyViewedBooks.reversed()) {
                    BookItem(book, onBookClick) // Pass the callback function to BookItem
                }
            }
        }
    }
}

@Composable
fun BookItem(book: Book, onBookClick: (Book) -> Unit) {
    Box(
        modifier = Modifier
            .height(150.dp)
            .width(109.dp)
            .shadow(15.dp)
            .background(Color.LightGray)
            .clickable { onBookClick(book) }
    ) {
        Image(
            painter = loadNetworkImage(book.Url),
            contentDescription = "Book Cover",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
    }
}








