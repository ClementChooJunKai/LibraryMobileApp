package com.example.team10mobileproject.Screens

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.team10mobileproject.ui.theme.MontserratFontFamily
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BorrowScreen(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController()
){
    val titles = listOf("Hard Copy", "E-Books", "Wishlist")
    var tabIndex by remember { mutableStateOf(0) }
    val pagerState = rememberPagerState(pageCount = { titles.size })
    val scope = rememberCoroutineScope()
    LaunchedEffect(pagerState.currentPage) {
        tabIndex = pagerState.currentPage
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
                    0 -> BorrowedBooks()
                    1 -> BorrowedBooks()
                    2 -> BorrowedBooks()
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
fun BorrowedBooks() {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(books) { book ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = book.book_img_id),
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
                        text = "Description:",
                        fontFamily =  MontserratFontFamily,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = book.description,
                        fontFamily =  MontserratFontFamily,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.height(70.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "ISBN:",
                        fontFamily =  MontserratFontFamily,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "by ${book.isbn}",
                        fontFamily =  MontserratFontFamily,
                    )
                }
            }
        }
    }
}


@Preview
@Composable
fun BorrowScreenPreview() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        BorrowScreen()
    }

}