package com.example.team10mobileproject.ViewModel


import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.team10mobileproject.Repo.Book
import com.example.team10mobileproject.Repo.BorrowBook
import com.example.team10mobileproject.Repo.FirebaseRepo
import com.example.team10mobileproject.Repo.SignInResponse
import com.example.team10mobileproject.Repo.SignUpResponse
import com.google.firebase.database.DatabaseError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FirebaseViewModel(private val firebaseRepo: FirebaseRepo) : ViewModel() {
    private val _selectedBook = MutableLiveData<Book>()
    val selectedBook: LiveData<Book> = _selectedBook
    val sid = mutableStateOf("")
    val Name = mutableStateOf("")
    val pdfUrl = mutableStateOf("")
    val _recentlyViewedBooks = MutableLiveData<List<Book>>()
    val recentlyViewedBooks: LiveData<List<Book>> = _recentlyViewedBooks
        // LiveData to communicate the success event back to the UI
    val borrowBookSuccess = mutableStateOf(false)
    private val _returnBookResult = MutableLiveData<Response<Boolean>>()
    val returnBookResult: LiveData<Response<Boolean>> = _returnBookResult
    var signUpResponse by mutableStateOf<SignUpResponse>(Response.Success(false))
        private set
    var signInResponse by mutableStateOf<SignInResponse>(Response.Success(false))
        private set

    private val _hardCopies = MutableLiveData<Int>()
    val hardCopies: LiveData<Int> = _hardCopies

    // LiveData to observe soft copies count
    private val _softCopies = MutableLiveData<Int>()
    val softCopies: LiveData<Int> = _softCopies

    private val _borrowedBooks = MutableLiveData<List<BorrowBook>>()
    val borrowedBooks: LiveData<List<BorrowBook>> = _borrowedBooks
    val heartSuccess = mutableStateOf(false)
    val isDataLoaded = MutableLiveData<Boolean>(false)

    private val _wishlistedBooks = MutableLiveData<List<Book>>()
    val wishlistedBooks: LiveData<List<Book>> = _wishlistedBooks
    val shelfNumber = mutableStateOf(0)

    private val _shelfBooks = MutableLiveData<List<Book>>()
    val shelfBooks: LiveData<List<Book>> = _shelfBooks

    private val _course = MutableLiveData<String>()
    val course: LiveData<String> = _course

    private val _location = MutableLiveData<Pair<Int, Int>>()
    val location: LiveData<Pair<Int, Int>> = _location
    var detectedText: MutableState<String> = mutableStateOf("")
    fun updateSelectedBook(book: Book) {
        _selectedBook.value = book
    }
    fun observeBookLocation(bookId: String) {
        viewModelScope.launch {
            firebaseRepo.observeLocation(bookId).collect { location ->
                _location.postValue(location)
            }
        }
    }
    fun getStudentName() {
        viewModelScope.launch {
            firebaseRepo.getStudent(sid.value) { name ->
                if (name != null) {
                    // Update the Name LiveData with the retrieved name
                    Name.value = name
                } else {
                    // Handle the case where the name could not be retrieved

                }
            }
        }
    }
    fun getCourse() {
        viewModelScope.launch {
            firebaseRepo.getCourse(sid.value).collect { course ->
                // Update your LiveData or StateFlow with the course string
                _course.value = course
            }
        }
    }
    fun getShelfBooks() {
        viewModelScope.launch {
            firebaseRepo.getShelfBooks(shelfNumber.value).collect { books ->
                _shelfBooks.value = books
                isDataLoaded.value = true
            }
        }
    }
    fun getWishlistedBooks() {
        viewModelScope.launch {
            firebaseRepo.getWishlistedBooks(sid.value).collect { books ->
                _wishlistedBooks.value = books
            }
        }
    }

    fun markBookAsFavorite( book: Book) {
        viewModelScope.launch {
            try {
                firebaseRepo.markAsFavorite(sid.value, book)
                heartSuccess.value=true
            } catch (e: Exception) {
                heartSuccess.value=false
            }
        }
    }



    val refreshTrigger = mutableStateOf(false)
    fun returnBook( bookId: String, copyType: String) {
        firebaseRepo.returnBook(sid.value, bookId, copyType) { response ->
            _returnBookResult.postValue(response)
            refreshTrigger.value = true



        }
    }
    fun getBorrowedBooks() {
        viewModelScope.launch {
            firebaseRepo.getBorrowedBooks(sid.value).collect { borrowedBooks ->
                _borrowedBooks.value = borrowedBooks
            }
        }
    }
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
    //To mock in unit testing to check actual data
    fun observeRecentlyViewedBooks(observer: Observer<List<Book>>) {
        recentlyViewedBooks.observeForever(observer)
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
    fun retrieveBookOnCourse(course: String,onSuccess: (List<Book>) -> Unit, onFailure: (DatabaseError) -> Unit) {
        viewModelScope.launch(Dispatchers.IO){
            firebaseRepo.retrieveBooksOnCourse(
                course,
                onSuccess = onSuccess,
                onFailure = onFailure
            )
        }
    }

    fun getRecentlyViewedBooks(sid: String) {
        viewModelScope.launch {
            firebaseRepo.getRecentlyViewedBooks(sid).collect { books ->

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