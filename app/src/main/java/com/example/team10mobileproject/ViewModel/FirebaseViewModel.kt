package com.example.team10mobileproject.ViewModel

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.painter.Painter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


import androidx.lifecycle.viewModelScope
import coil.compose.rememberAsyncImagePainter
import com.example.team10mobileproject.Repo.Book
import com.example.team10mobileproject.Repo.FirebaseRepo
import com.example.team10mobileproject.Repo.SignInResponse
import com.example.team10mobileproject.Repo.SignUpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
class FirebaseViewModel(private val firebaseRepo: FirebaseRepo) : ViewModel() {
    private val _selectedBook = MutableLiveData<Book>()
    val selectedBook: LiveData<Book> = _selectedBook
    val sid = mutableStateOf("")
    private val _recentlyViewedBooks = MutableLiveData<List<Book>>()
    val recentlyViewedBooks: LiveData<List<Book>> = _recentlyViewedBooks
        // LiveData to communicate the success event back to the UI
    val borrowBookSuccess = mutableStateOf(false)
    var signUpResponse by mutableStateOf<SignUpResponse>(Response.Success(false))
        private set
    var signInResponse by mutableStateOf<SignInResponse>(Response.Success(false))
        private set

    private val _hardCopies = MutableLiveData<Int>()
    val hardCopies: LiveData<Int> = _hardCopies

    // LiveData to observe soft copies count
    private val _softCopies = MutableLiveData<Int>()
    val softCopies: LiveData<Int> = _softCopies

    // Method to start observing hard copies of a book
    fun observeHardCopies(bookId: String) {
        viewModelScope.launch {
            firebaseRepo.observeHardCopies(bookId).collect { hardCopies ->
                _hardCopies.value = hardCopies
            }
        }
    }

    // Method to start observing soft copies of a book
    fun observeSoftCopies(bookId: String) {
        viewModelScope.launch {
            firebaseRepo.observeSoftCopies(bookId).collect { softCopies ->
                _softCopies.value = softCopies
            }
        }
    }

    fun borrowBook( bookId: String, copyType: String) {
        viewModelScope.launch {
               val response = firebaseRepo.borrowBook(sid.value, bookId, copyType)
            if (response is Response.Success) {
                // Post the success event to LiveData
                borrowBookSuccess.value = true
            } else if (response is Response.Failure) {
                borrowBookSuccess.value = false
            }
        }
    }

    fun setSelectedBook(book: Book) {
        _selectedBook.value = book
    }

    fun recentlyViewed(bookname: String, sid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            firebaseRepo.recentlyViewed(bookname,sid)

        }
    }
    fun retrieveAllBooks(onSuccess: (List<Book>) -> Unit, onFailure: (DatabaseError) -> Unit) {
        viewModelScope.launch(Dispatchers.IO){
        firebaseRepo.retrieveAllBooks(
            onSuccess = onSuccess,
            onFailure = onFailure
        )
        }
    }

    fun getRecentlyViewedBooks(sid: String) {
        viewModelScope.launch {
            firebaseRepo.getRecentlyViewedBooks(sid).collect { books ->
                Log.d("RecentlyViewedBooks", "New data emitted: ${books.size} books")
                _recentlyViewedBooks.value = books
            }
        }
    }


    //Fire Base User Functions
    fun registerStudent(username: String, password: String, sid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            firebaseRepo.registerStudent(username, password, sid)
        }
    }
     suspend fun firebaseSignUpWithEmailAndPassword(email: String, password: String) {
         signUpResponse = firebaseRepo.firebaseSignUpWithEmailAndPassword(email, password)

    }
    suspend fun firebaseSignInWithEmailAndPassword(email: String, password: String) {
        signInResponse = firebaseRepo.firebaseSignInWithEmailAndPassword(email, password)
    }

    suspend fun sendPasswordResetEmail(email: String) {
        firebaseRepo.sendPasswordResetEmail(email)
    }
    fun signOut() {
        firebaseRepo.signOut()
    }


}



class FirebaseViewModelFactory(private val firebaseRepo: FirebaseRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FirebaseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FirebaseViewModel(firebaseRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}