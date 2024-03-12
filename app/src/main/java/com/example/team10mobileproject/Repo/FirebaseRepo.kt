package com.example.team10mobileproject.Repo

import android.annotation.SuppressLint
import android.util.Log
import com.example.team10mobileproject.ViewModel.Response
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

typealias SignUpResponse = Response<Boolean>
typealias SendEmailVerificationResponse = Response<Boolean>
typealias SignInResponse = Response<Boolean>
typealias ReloadUserResponse = Response<Boolean>
typealias SendPasswordResetEmailResponse = Response<Boolean>
typealias RevokeAccessResponse = Response<Boolean>


data class Book(
    val Title: String = "",
    val Url: String = "",
    val Course: String = "",
    val Description: String = "",

) {
    // No-argument constructor
    constructor() : this("", "", "","")
}
data class BorrowBook(
    val Title: String = "",
    val Url: String = "",
    val Course: String = "",
    val Description: String = "",
    val BorrowDate: String ="",
    val ExpiryDate: String ="",
    val copyType: String ="",

) {
    // No-argument constructor
    constructor() : this("", "", "","","","")
}
class FirebaseRepo {

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db = Firebase.database("https://mobileproject-63310-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val accountRef = db.getReference("Account")



    suspend fun markAsFavorite(userId: String, book: Book) {
        // Construct the path to the user's wishlist
        val wishlistRef = db.getReference("Account").child(userId).child("Wishlist")

        // Create a unique ID for the book in the wishlist
        // Assuming you have a way to generate a consistent ID for each book
        // For simplicity, let's use the book's title as the ID
        val bookId = book.Title // This should be unique for each book

        // Check if the book is already in the wishlist
        val bookSnapshot = wishlistRef.child(bookId).get().await()
        if (bookSnapshot.exists()) {
            // If the book is already in the wishlist, delete it
            wishlistRef.child(bookId).removeValue().await()
        } else {
            // If the book is not in the wishlist, add it
            val bookData = mapOf(
                "title" to book.Title,
                "url" to book.Url,
                "course" to book.Course,
                "description" to book.Description,
                // Add other relevant book details here if needed
            )
            wishlistRef.child(bookId).setValue(bookData).await()
        }
    }


    fun getBorrowedBooks(userId: String): Flow<List<BorrowBook>> = callbackFlow {
        val borrowedBooksRef = db.getReference("Account").child(userId).child("BorrowedBooks")

        Log.d("getBorrowedBooks", "Starting to retrieve borrowed books for user: $userId")

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("getBorrowedBooks", "Data changed for user: $userId")

                val borrowedBooks = mutableListOf<BorrowBook>()

                snapshot.children.forEach { borrowedBookSnapshot ->
                    val bookId = borrowedBookSnapshot.key?.split("-")?.firstOrNull()
                    val copyType = borrowedBookSnapshot.key?.split("-")?.lastOrNull()

                    if (bookId != null && copyType != null) {
                        Log.d("getBorrowedBooks", "Processing bookId: $bookId, copyType: $copyType")

                        val bookRef = db.getReference("Books").child(bookId)
                        bookRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(bookSnapshot: DataSnapshot) {
                                Log.d("getBorrowedBooks", "Book details retrieved for bookId: $bookId")

                                val title = bookSnapshot.child("Title").getValue(String::class.java) ?: ""
                                val url = bookSnapshot.child("Url").getValue(String::class.java) ?: ""
                                val course = bookSnapshot.child("Course").getValue(String::class.java) ?: ""
                                val description = bookSnapshot.child("Description").getValue(String::class.java) ?: ""
                                val borrowDate = borrowedBookSnapshot.child("BorrowDate").getValue(String::class.java) ?: ""
                                val expiryDate = borrowedBookSnapshot.child("ExpiryDate").getValue(String::class.java) ?: ""
                                val book = BorrowBook(title, url, course, description, borrowDate, expiryDate, copyType)
                                borrowedBooks.add(book)

                                // Check if all books have been processed
                                if (borrowedBooks.size.toLong() == snapshot.childrenCount) {
                                    Log.d("getBorrowedBooks", "All books processed for user: $userId")
                                    trySend(borrowedBooks)
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.e("getBorrowedBooks", "Failed to retrieve book details: ${error.message}")
                                close(Exception("Failed to retrieve book details: ${error.message}"))
                            }
                        })
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("getBorrowedBooks", "Failed to retrieve borrowed books: ${error.message}")
                close(Exception("Failed to retrieve borrowed books: ${error.message}"))
            }
        }

        borrowedBooksRef.addListenerForSingleValueEvent(listener)

        awaitClose {
            Log.d("getBorrowedBooks", "Removing event listener for user: $userId")
            borrowedBooksRef.removeEventListener(listener)
        }
    }
    fun returnBook(userId: String, bookId: String, copyType: String, onComplete: (Response<Boolean>) -> Unit) {
        // Remove the book from the user's borrowed books
        val borrowedBooksRef = db.getReference("Account").child(userId).child("BorrowedBooks")
        val uniqueBookId = "$bookId-$copyType"
        borrowedBooksRef.child(uniqueBookId).removeValue()

        // Increment the quantity of the book
        val booksRef = db.getReference("Books").child(bookId)
        val incrementTask = booksRef.child(copyType + "Copies").runTransaction(object : Transaction.Handler {
            override fun doTransaction(currentData: MutableData): Transaction.Result {
                val currentQuantity = currentData.getValue(Long::class.java) ?: 0
                currentData.value = currentQuantity + 1
                return Transaction.success(currentData)
            }

            override fun onComplete(error: DatabaseError?, committed: Boolean, currentData: DataSnapshot?) {
                if (error != null) {
                    Log.e("returnBook", "Failed to return book: ${error.message}")
                    onComplete(Response.Failure(Exception("Failed to return book: ${error.message}")))
                } else if (committed) {
                    onComplete(Response.Success(true))
                } else {
                    onComplete(Response.Failure(Exception("Transaction not committed")))
                }
            }
        })
    }
    // Function to get the hard copies of a book as a flow
    fun observeHardCopies(bookId: String): Flow<Int> {
        return callbackFlow {
            val bookRef = db.getReference("Books").child(bookId)
            val listener = bookRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val hardCopies = snapshot.child("hardCopies").getValue(Int::class.java) ?: 0
                    trySend(hardCopies)
                }

                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }
            })

            awaitClose { bookRef.removeEventListener(listener) }
        }.stateIn(CoroutineScope(Dispatchers.IO), SharingStarted.Lazily, 0)
    }

    // Function to get the soft copies of a book as a flow
    fun observeSoftCopies(bookId: String): Flow<Int> {
        return callbackFlow {
            val bookRef = db.getReference("Books").child(bookId)
            val listener = bookRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val softCopies = snapshot.child("softCopies").getValue(Int::class.java) ?: 0
                    trySend(softCopies)
                }

                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }
            })

            awaitClose { bookRef.removeEventListener(listener) }
        }.stateIn(CoroutineScope(Dispatchers.IO), SharingStarted.Lazily, 0)
    }
    suspend fun borrowBook(userId: String, bookId: String, copyType: String): Response<Boolean> {
        return try {
            val bookRef = db.getReference("Books").child(bookId)
            val bookSnapshot = bookRef.get().await()
            val availableCopies =
                bookSnapshot.child(copyType + "Copies").getValue(Long::class.java) ?: 0

            if (availableCopies > 0) {
                // Update the availability
                bookRef.child(copyType + "Copies").setValue(availableCopies - 1).await()

                // Record the borrowed book in the user's account
                val borrowedBooksRef =
                    db.getReference("Account").child(userId).child("BorrowedBooks")
                val borrowDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                val expiryDate = SimpleDateFormat(
                    "yyyy-MM-dd",
                    Locale.getDefault()
                ).format(Date().time + 14 * 24 * 60 * 60 * 1000) // 14 days from now

                // Append the copyType to the bookId to create a unique identifier for each version
                val uniqueBookId = "$bookId-$copyType"

                val borrowedBookData = mapOf(
                    "Title" to bookSnapshot.child("Title").getValue(String::class.java),
                    "BorrowDate" to borrowDate,
                    "ExpiryDate" to expiryDate,
                    "CopyType" to copyType // Optionally, store the copy type for reference
                )

                borrowedBooksRef.child(uniqueBookId).setValue(borrowedBookData).await()

                Response.Success(true)
            } else {
                Response.Failure(Exception("No copies available"))
            }
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    // Function to retrieve all books from Firebase Realtime Database
    suspend fun retrieveAllBooks(
        onSuccess: (List<Book>) -> Unit,
        onFailure: (DatabaseError) -> Unit
    ) {
        val booksRef = db.getReference("Books")

        // Attach a ValueEventListener to retrieve the data
        booksRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val books = mutableListOf<Book>()

                for (bookSnapshot in snapshot.children) {
                    val title = bookSnapshot.child("Title").getValue(String::class.java) ?: ""
                    val imageUrl = bookSnapshot.child("Url").getValue(String::class.java) ?: ""
                    val course = bookSnapshot.child("Course").getValue(String::class.java) ?: ""
                    val description =
                        bookSnapshot.child("Description").getValue(String::class.java) ?: ""

                    val book = Book(title, imageUrl, course, description)
                    books.add(book)

                }

                // Pass the list of books to the onSuccess callback
                onSuccess(books)
            }

            override fun onCancelled(error: DatabaseError) {
                // Pass the DatabaseError object to the onFailure callback
                onFailure(error)
            }
        })
    }

    fun getRecentlyViewedBooks(sid: String): Flow<List<Book>> = callbackFlow {
        val recentlyViewedRef = accountRef.child(sid).child("RecentlyViewed")
        val booksRef = db.getReference("Books")

        val recentlyViewedListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val books = mutableListOf<String>()
                snapshot.children.forEach { bookSnapshot ->
                    val bookName = bookSnapshot.getValue(String::class.java)
                    if (bookName != null) {
                        books.add(bookName)
                    }
                }
                val recentBooks = books.takeLast(3)

                val bookDetailsList = mutableListOf<Book>()
                val booksListener = object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        recentBooks.forEach { recentBook ->
                            snapshot.children.forEach { bookSnapshot ->
                                val bookName =
                                    bookSnapshot.child("Title").getValue(String::class.java)
                                if (bookName != null && bookName == recentBook) {
                                    val bookDetails = bookSnapshot.getValue(Book::class.java)
                                    if (bookDetails != null) {
                                        bookDetailsList.add(bookDetails)
                                    }
                                }
                            }
                        }
                        trySend(bookDetailsList)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        close(Exception("Failed to retrieve book details: ${error.message}"))
                    }
                }
                booksRef.addListenerForSingleValueEvent(booksListener)
            }

            override fun onCancelled(error: DatabaseError) {
                close(Exception("Failed to retrieve recently viewed books: ${error.message}"))
            }
        }
        recentlyViewedRef.addListenerForSingleValueEvent(recentlyViewedListener)

        awaitClose {
            recentlyViewedRef.removeEventListener(recentlyViewedListener)

        }
    }

    fun registerStudent(
        username: String = "",
        password: String = "",
        sid: String = ""
    ) {

        accountRef.child(sid).child("Sid").setValue(sid)
        accountRef.child(sid).child("Name").setValue(username)
        accountRef.child(sid).child("Password").setValue(password)

    }

    fun recentlyViewed(bookName: String, sid: String) {
        val recentlyViewedRef = accountRef.child(sid).child("RecentlyViewed")

        // Get the current number of recently viewed books
        recentlyViewedRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val count = snapshot.childrenCount.toInt()
                var bookExists = false

                // Check if the book is already in the list
                snapshot.children.forEach { bookSnapshot ->
                    val existingBookName = bookSnapshot.getValue(String::class.java)
                    if (existingBookName != null && existingBookName == bookName) {
                        bookExists = true
                    }
                }

                if (!bookExists) {
                    if (count >= 3) {
                        // If there are already three books, remove the oldest one
                        val oldestBookKey = snapshot.children.first().key
                        recentlyViewedRef.child(oldestBookKey!!).removeValue()
                    }
                    // Add the new book
                    recentlyViewedRef.push().setValue(bookName)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    suspend fun firebaseSignUpWithEmailAndPassword(
        email: String, password: String
    ): SignUpResponse {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            Log.d("FirebaseRepo", "Success")
            Response.Success(true)
        } catch (e: Exception) {
            Log.d("FirebaseRepo", "Failed", e)
            Response.Failure(e)
        }
    }

    suspend fun firebaseSignInWithEmailAndPassword(
        email: String, password: String
    ): SignInResponse {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Response.Success(true)
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    suspend fun sendPasswordResetEmail(email: String): SendPasswordResetEmailResponse {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Response.Success(true)
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    fun signOut() = auth.signOut()

    suspend fun revokeAccess(): RevokeAccessResponse {
        return try {
            auth.currentUser?.delete()?.await()
            Response.Success(true)
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

}




