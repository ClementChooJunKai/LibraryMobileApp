import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
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
import com.example.team10mobileproject.Repo.Book
import com.example.team10mobileproject.Screen
import com.example.team10mobileproject.Screens.BottomBar
import com.example.team10mobileproject.ViewModel.FirebaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


/**
 * Loads an image from the network using the provided [url].
 * @param url The URL of the image to be loaded.
 * @return A [Painter] representing the loaded image.
 */
@Composable
fun loadNetworkImage(url: String): Painter {
    return rememberAsyncImagePainter(url)
}

/**
 * Composable function representing the homepage of the application.
 * @param modifier Optional modifier for the layout.
 * @param navController NavController used for navigation within the app.
 * @param viewModel ViewModel for managing data and business logic.
 */
@Composable
fun Homepage(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController(),
    viewModel: FirebaseViewModel,
) {
    // Observing recently viewed books from the view model
    val recentlyViewedBooks by viewModel.recentlyViewedBooks.observeAsState(initial = emptyList())

    // State to manage the visibility of the popup
    var showPopup by remember { mutableStateOf(false) }
    var clickedBookIndex by remember { mutableIntStateOf(0) }
    var selectedBook by remember { mutableStateOf<Book?>(null) }
    val course by viewModel.course.observeAsState(initial = "")
    var books by remember(viewModel) {
        mutableStateOf<List<Book>>(emptyList())
    }

    // Logging the value of sid
    Log.d("SID_Value", "SID value: ${viewModel.sid.value}")

    // Fetching recently viewed books and course information
    viewModel.getRecentlyViewedBooks(viewModel.sid.value)
    LaunchedEffect(key1 = Unit) {
        viewModel.getRecentlyViewedBooks(viewModel.sid.value)
        viewModel.getCourse()
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

    // Main content of the homepage
    Box(modifier = Modifier.fillMaxSize().testTag("Box1")) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 15.dp)
        ) {
            // Heading item and carousel slider
            Spacer(modifier = Modifier.height(30.dp))
            HeadingItem(viewModel.sid, course)
            Spacer(modifier = Modifier.height(8.dp))
            Spacer(modifier = Modifier.height(5.dp))
            CarouselSlider(books) { index ->
                // Update the state to show the popup and set the clicked book index
                clickedBookIndex = index
                selectedBook = books.getOrNull(index)
                showPopup = true
            }

            // Recommended books section
            RecommendedBooks(
                recentlyViewedBooks = recentlyViewedBooks,
                onBookClick = { book ->
                    selectedBook = book
                    showPopup = true
                }
            )
        }

        // Bottom bar aligned to the bottom of the screen
        BottomBar(
            modifier = Modifier.align(Alignment.BottomCenter),
            navController = navController
        )
    }

    // Show the popup if the state is set to true
    if (showPopup) {
        ShowPopup(
            viewModel = viewModel,
            navController = navController,
            sid = viewModel.sid, // Corrected here
            book = selectedBook ?: Book("", "", "", ""), // Use the selectedBook state
            onDismiss = { showPopup = false }
        )
    }
}

/**
 * Composable function for displaying a popup dialog with book details.
 * @param viewModel ViewModel for managing data and business logic.
 * @param navController NavController used for navigation within the app.
 * @param sid MutableState containing the user's session ID.
 * @param book Book object representing the selected book.
 * @param onDismiss Callback function to dismiss the popup.
 */
@Composable
fun ShowPopup(
    viewModel: FirebaseViewModel,
    navController: NavController,
    sid: MutableState<String>,
    book: Book,
    onDismiss: () -> Unit
) {
    // Log the recently viewed book and update the session ID
    viewModel.recentlyViewed(book.Title, sid = sid.value)

    // Display the popup dialog
    Dialog(onDismissRequest = onDismiss) {
        // Popup content
        Box(
            modifier = Modifier
                .padding(16.dp)
                .background(Color.White)
                .size(500.dp)
                .clip(RoundedCornerShape(30.dp))
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
                        Image(
                            painter = loadNetworkImage(book.Url),
                            contentDescription = null,
                            modifier = Modifier
                                .width(240.dp)
                                .height(320.dp),
                            contentScale = ContentScale.FillBounds
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp)) // Add some space between the content and the buttons

                // Buttons for reading more about the book and closing the popup
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(modifier = Modifier.testTag("ReadMore"),onClick = {
                        // Navigate to the book details screen
                        viewModel.setSelectedBook(book)
                        navController.navigate(Screen.BookDetails.route)
                    }) {
                        Text("Read More", color = Color.White)
                    }
                    Button(onClick = onDismiss) {
                        Text("Close", color = Color.White)
                    }
                }
            }
        }
    }
}

/**
 * Composable function for displaying a carousel slider of books.
 * @param books List of books to be displayed in the carousel.
 * @param onItemClick Callback function invoked when a book is clicked.
 */
@Composable
private fun CarouselSlider(books: List<Book>, onItemClick: (Int) -> Unit) {
    // State to track the current index of the carousel
    var index by remember { mutableStateOf(0) }
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Automatically scroll the carousel
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
        modifier = Modifier.padding(10.dp)
    ) {
        Text(
            text = "For you",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSecondary,
            modifier = Modifier.padding(bottom = 15.dp)
        )
        Box(modifier = Modifier) {

            LazyRow(
                state = scrollState,
                modifier = Modifier,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                itemsIndexed(books) { index, book ->
                    Box(modifier = Modifier.clickable { onItemClick(index) }.testTag("BookExample$index")) {
                        Card(
                            modifier = Modifier.height(150.dp),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 8.dp
                            )
                        ) {
                            // Display the book cover image
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
        // Display dot indicators for the carousel
        Box(
            modifier = Modifier
                .width(70.dp)
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
                .padding(top = 20.dp)
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

/**
 * Composable function for displaying dot indicators for the carousel slider.
 * @param selectedIndex Index of the currently selected item in the carousel.
 * @param totalItems Total number of items in the carousel.
 * @param onDotClick Callback function invoked when a dot is clicked.
 */
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

/**
 * Composable function for displaying the user's heading information.
 * @param sid MutableState containing the user's session ID.
 * @param course The current course information.
 */
@Composable
fun HeadingItem(sid: MutableState<String>, course: String){
    Column {
        // Display welcome message
        Text(
            text = "WELCOME ",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            fontStyle = FontStyle.Normal,
            fontSize = 30.sp,
            modifier = Modifier.padding(bottom =  8.dp)
                .testTag("ID")
        )
        Spacer(modifier = Modifier.width(10.dp))
        // Display user's session ID and course
        Text(
            text = sid.value +" !",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            fontStyle = FontStyle.Normal,
            modifier = Modifier.padding(bottom =  16.dp)
                .testTag("ID")
        )
    }
    // Display the course information
    Box(
        modifier = Modifier.shadow(
            elevation = 5.dp,
            shape = RoundedCornerShape(10.dp)
        )
    ) {
        Text(
            text = "Course: $course",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .padding(bottom = 4.dp)
                .fillMaxWidth()
                .background(Color.White)
                .padding(5.dp)
                .align(Alignment.Center)
        )
    }
}
/**
 * Composable function for displaying a list of recommended books.
 * @param recentlyViewedBooks List of recently viewed books to be displayed.
 * @param onBookClick Callback function invoked when a book is clicked.
 */
@Composable
fun RecommendedBooks(
    recentlyViewedBooks: List<Book>,
    onBookClick: (Book) -> Unit // This is the callback function
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .height(240.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
        ) {
            // Display the title for the section
            Text(
                text = "Recently Viewed",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier.padding(bottom = 5.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))
            // Display the list of recently viewed books
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

/**
 * Composable function for displaying an individual book item.
 * @param book The book to be displayed.
 * @param onBookClick Callback function invoked when the book item is clicked.
 */
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
        // Display the book cover image
        Image(
            painter = loadNetworkImage(book.Url),
            contentDescription = "Book Cover",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
    }
}









