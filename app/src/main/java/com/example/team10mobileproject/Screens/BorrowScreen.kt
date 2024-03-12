package com.example.team10mobileproject.Screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.team10mobileproject.Repo.BorrowBook
import com.example.team10mobileproject.ViewModel.FirebaseViewModel
import com.example.team10mobileproject.ViewModel.Response
import com.example.team10mobileproject.ui.theme.MontserratFontFamily
import kotlinx.coroutines.launch



@Composable
fun loadNetworkImage(url: String): Painter {
    return rememberAsyncImagePainter(url)
}
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BorrowScreen(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController(),
    viewModel: FirebaseViewModel
){
    val titles = listOf("Hard Copy", "E-Books", "Wishlist")
    var tabIndex by remember { mutableStateOf(0) }
    val pagerState = rememberPagerState(pageCount = { titles.size })
    val scope = rememberCoroutineScope()
    val borrowedBooks by viewModel.borrowedBooks.observeAsState(emptyList())
   



    LaunchedEffect(pagerState.currentPage) {
        tabIndex = pagerState.currentPage
        viewModel.getBorrowedBooks(viewModel.sid.value)
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Borrowed",
            fontFamily =  MontserratFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            modifier = Modifier.padding(start = 22.dp, top = 18.dp)
        )
        TabRow(selectedTabIndex = tabIndex, modifier = Modifier.fillMaxWidth()) {
            titles.forEachIndexed { index, title ->
                Tab(selected = index == tabIndex,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                        tabIndex = index
                    }) {
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
                    0 -> BorrowedBooks(borrowedBooks,"hard",viewModel)
                    1 -> BorrowedBooks(borrowedBooks,"soft",viewModel)
                    2 -> BorrowedBooks(borrowedBooks,"",viewModel)
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


@Composable
fun BorrowedBooks(borrowedBooks: List<BorrowBook>, copyType: String = "",viewModel: FirebaseViewModel) {
    val context = LocalContext.current // Assuming you are using Compose
    val returnBookResult by viewModel.returnBookResult.observeAsState(initial = Response.Success(false))
    val filteredBooks = if (copyType.isNotEmpty()) {
        borrowedBooks.filter { it.copyType == copyType }
    } else {
        borrowedBooks
    }
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
                Image(
                    painter = loadNetworkImage( book.Url),
                    contentDescription = null,
                    modifier = Modifier
                        .size(110.dp, 170.dp)
                        .shadow(15.dp),
                    contentScale = ContentScale.Crop
                )
                Column(
                    modifier = Modifier
                        .padding(top = 6.dp, start = 12.dp)
                        .height(240.dp)
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
                    Button(onClick = { viewModel.returnBook(book.Title,book.copyType)

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
                }
            }
        }

    }
}


