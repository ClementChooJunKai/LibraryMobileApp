package com.example.team10mobileproject.Screens


import ShowPopup
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.team10mobileproject.Repo.Book
import com.example.team10mobileproject.Repo.BorrowBook
import com.example.team10mobileproject.Screen
import com.example.team10mobileproject.ViewModel.FirebaseViewModel
import com.example.team10mobileproject.ViewModel.Response
import com.example.team10mobileproject.ui.theme.MontserratFontFamily
import kotlinx.coroutines.launch

/**
 * Composable function for loading a network image.
 * @param url The URL of the image.
 * @return The painter for the image.
 */
@Composable
fun loadNetworkImage(url: String): Painter {
    return rememberAsyncImagePainter(url)
}

/**
 * Composable function for displaying the borrow screen.
 * @param modifier The modifier for the borrow screen.
 * @param navController The navigation controller.
 * @param viewModel The view model to manage Firebase data.
 * Borrow screen that allow users to choose between 3 tabs
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BorrowScreen(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController(),
    viewModel: FirebaseViewModel
){
    // Initialization
    val titles = listOf("Hard Copy", "E-Books", "Wishlist")
    var tabIndex by remember { mutableStateOf(0) }
    val pagerState = rememberPagerState(pageCount = { titles.size })
    val scope = rememberCoroutineScope()
    val borrowedBooks by viewModel.borrowedBooks.observeAsState(emptyList())

    val wishlistBooks by viewModel.wishlistedBooks.observeAsState(emptyList())

    // Fetch data from Firebase
    LaunchedEffect( viewModel.refreshTrigger.value) {
        viewModel.getBorrowedBooks()
        viewModel.refreshTrigger.value = false

    }

    // Handle tab change and fetch wishlist books
    LaunchedEffect(pagerState.currentPage) {
        tabIndex = pagerState.currentPage

        viewModel.getWishlistedBooks()
    }

    // UI layout
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Title
        Text(
            text = "Borrowed",
            fontFamily =  MontserratFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            modifier = Modifier.padding(start = 22.dp, top = 18.dp)
        )
        // Tab row
        TabRow(selectedTabIndex = tabIndex, modifier = Modifier.fillMaxWidth()) {
            titles.forEachIndexed { index, title ->
                Tab(selected = index == tabIndex,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                        tabIndex = index
                    },  modifier = Modifier.testTag(title)) {
                    Text(
                        text = title,
                        fontFamily =  MontserratFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
        // Pager for switching between hard copy, e-books, and wishlisted items
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) { index ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                when(index){
                    0 -> BorrowedBooks(borrowedBooks,"hard",viewModel,navController)
                    1 -> BorrowedBooks(borrowedBooks,"soft",viewModel,navController)
                    2 -> wishList(wishlistBooks,viewModel,navController)
                }
            }
        }
    }
    Box(){
        BottomBar(
            modifier = Modifier.align(Alignment.BottomCenter),
            navController = navController
        )
    }
}

/**
 * Composable function for displaying borrowed books.
 * @param borrowedBooks The list of borrowed books.
 * @param copyType The type of copy.
 * @param viewModel The view model to manage Firebase data.
 * @param navController The navigation controller.
 */
@Composable
fun BorrowedBooks(borrowedBooks: List<BorrowBook>, copyType: String = "",viewModel: FirebaseViewModel,navController: NavController) {
    val context = LocalContext.current // Assuming you are using Compose
    val returnBookResult by viewModel.returnBookResult.observeAsState(initial = Response.Success(false))
    val filteredBooks = if (copyType.isNotEmpty()) {
        borrowedBooks.filter { it.copyType == copyType }
    } else {
        borrowedBooks
    }
    // Lazy column for scrollable list of borrowed books
    LazyColumn(
        modifier = Modifier.height(530.dp)
    ) {
        items(filteredBooks) { book ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp, horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Image of the book
                Image(
                    painter = loadNetworkImage( book.Url),
                    contentDescription = null,
                    modifier = Modifier
                        .size(110.dp, 170.dp)
                        .shadow(15.dp),
                    contentScale = ContentScale.Crop
                )
                // Details of the book
                Column(
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .height(250.dp)
                ) {
                    Text(
                        text = "Description:",
                        fontFamily =  MontserratFontFamily,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = book.Description,
                        fontFamily =  MontserratFontFamily,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.height(70.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Borrowed Date:",
                        fontFamily =  MontserratFontFamily,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = " ${book.BorrowDate}",
                        fontFamily =  MontserratFontFamily,
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Expired by:",
                        fontFamily =  MontserratFontFamily,
                        fontWeight = FontWeight.Bold
                    )
                    Row{

                    }
                    Text(
                        text = " ${book.ExpiryDate}",
                        fontFamily =  MontserratFontFamily,
                    )
                    Spacer(modifier = Modifier.height(5.dp))

                    // Return button for borrowed books
                        Row{
                            Button(modifier = Modifier.height(70.dp).testTag("Return"),onClick = { viewModel.returnBook(book.Title,book.copyType)

                                if (returnBookResult is Response.Success) {
                                    Toast.makeText(
                                        context,
                                        "Returned Successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else if (returnBookResult is Response.Failure) {
                                    Toast.makeText(
                                        context,
                                        "Failed to return",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }}) {
                                Text(text = "Return Book", color = Color.White)
                            }
                            Spacer(modifier = Modifier.width(5.dp))
                            // Read button for e-books
                            if(copyType == "soft") {
                                Button(modifier = Modifier.height(70.dp).testTag("Read"),onClick = {viewModel.pdfUrl.value = book.Pdf
                                    navController.navigate(
                                    Screen.PDFReader.route)}) {
                                    Text(text = "Read", color = Color.White)
                                }
                        }


                    }

                }
            }
        }

    }
}
/**
 * Composable function for displaying the wishlist screen.
 * This function displays a list of wishlisted books with details and options to interact with them.
 * @param wishListBooks The list of wishlisted books.
 * @param viewModel The view model to manage Firebase data.
 * @param navController The navigation controller.
 */
@Composable
fun wishList(wishListBooks: List<Book>, viewModel: FirebaseViewModel, navController: NavController) {
    val context = LocalContext.current // Assuming you are using Compose
    val returnBookResult by viewModel.returnBookResult.observeAsState(initial = Response.Success(false))
    var showPopup by remember { mutableStateOf(false) }
    var selectedBook by remember { mutableStateOf<Book?>(null) }

    // Lazy column for scrollable list of wishlisted books
    LazyColumn(
        modifier = Modifier.height(530.dp)
    ) {
        items(wishListBooks) { book ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp, horizontal = 20.dp)
                    .clickable {
                        selectedBook = book
                        showPopup = true
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Image of the book
                Image(
                    painter = loadNetworkImage(book.Url),
                    contentDescription = null,
                    modifier = Modifier
                        .size(110.dp, 170.dp)
                        .shadow(15.dp),
                    contentScale = ContentScale.Crop
                )
                // Details of the book
                Column(
                    modifier = Modifier
                        .padding(top = 6.dp, start = 12.dp)
                        .height(240.dp)
                ) {
                    Text(
                        text = "Title:",
                        fontFamily = MontserratFontFamily,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = book.Title ,
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
                        text = " ${book.Description}",
                        fontFamily = MontserratFontFamily,
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Expired by:",
                        fontFamily = MontserratFontFamily,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = " ${book.Course}",
                        fontFamily = MontserratFontFamily,
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                }
            }
            // Show popup dialog for interacting with the selected book
            if (showPopup) {
                ShowPopup(
                    viewModel = viewModel,
                    navController = navController, // You might need to pass the navController as a parameter to wishList
                    sid = viewModel.sid, // Adjust this if necessary
                    book = selectedBook ?: Book("", "", "", ""), // Use the selectedBook state
                    onDismiss = { showPopup = false }
                )
            }
        }
    }
}


