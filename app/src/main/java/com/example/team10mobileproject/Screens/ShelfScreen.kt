package com.example.team10mobileproject.Screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.team10mobileproject.R
import com.example.team10mobileproject.ui.theme.MontserratFontFamily

data class Book(val book_img_id: Int, val title: String, val description: String, val isbn: String)

val books = listOf(
    Book(R.drawable.book, "Kotlin", "Kotlin in Action guides experienced Java developers from,Kotlin in Action guides experienced Java developers from,Kotlin in Action guides experienced Java developers from,Kotlin in Action guides experienced Java developers from", "102.1221.3"),
    Book(R.drawable.book1,"Acoustic", "Book Description 2", "102.1221.4"),
    Book(R.drawable.book2,"Book", "Book Description 3", "102.1221.5"),
    Book(R.drawable.book3,"Male Acoucism", "Book Description 4", "102.1221.6"),
)

@Composable
fun ShelfScreen(
    navController: NavController = rememberNavController()
){
    var shelfNumber by remember { mutableStateOf(22) }
    var searchText by remember { mutableStateOf("") }
    val filteredBooks = remember { mutableStateListOf<Book>() }

    LaunchedEffect(searchText) {
        filteredBooks.clear()
        if (searchText.isEmpty()) {
            filteredBooks.addAll(books)
        } else {
            filteredBooks.addAll(
                books.filter {
                    it.description.contains(searchText, ignoreCase = true)|| it.title.contains(searchText, ignoreCase = true) || it.isbn.contains(searchText, ignoreCase = true)
                }
            )
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        item {
            Column {
                Text(
                    text = "Shelf #$shelfNumber",
                    fontFamily =  MontserratFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(start = 22.dp, top = 18.dp)
                )
                SearchBar(
                    searchText = searchText,
                    onSearchTextChange = { newText ->
                        searchText = newText
                    },
                    onClearClick = {
                        searchText = ""
                        filteredBooks.clear()
                        filteredBooks.addAll(books)
                    }
                )
                Divider()
//                Spacer(modifier = Modifier.height(4.dp))
            }
        }
        items(filteredBooks) { book ->
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

    Box(){
        BottomBar(
            modifier = Modifier.align(Alignment.BottomCenter),
            navController = navController
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    onClearClick: () -> Unit
){
    var isCrossIconVisible by remember { mutableStateOf(false) }
    LaunchedEffect(searchText) {
        isCrossIconVisible = searchText.isNotEmpty()
    }
    TextField(
        value = searchText,
        onValueChange = { onSearchTextChange(it) },
        modifier = Modifier
            .fillMaxWidth()
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


@Preview
@Composable
fun ShelfScreenPreview() {
    Surface(
        modifier = Modifier.fillMaxSize(),
//            .background(
//                brush = gradientBackgroundBrush(
//                    isVerticalGradient = true,
//                )
//            ),
        color = MaterialTheme.colorScheme.background
    ) {
        ShelfScreen()
    }

}