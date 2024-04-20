import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.team10mobileproject.Repo.Book
import com.example.team10mobileproject.Repo.FirebaseRepo
import com.example.team10mobileproject.ViewModel.FirebaseViewModel
import com.example.team10mobileproject.ViewModel.Response
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever

// Specify the test runner to use Mockito for mocking
@RunWith(MockitoJUnitRunner::class)
class MyViewModelTest {
    // Declare a mock instance of FirebaseRepo
    @Mock
    lateinit var firebaseRepo: FirebaseRepo
    @Mock
    // Declare a mock instance of ViewModel
    lateinit var viewModel: FirebaseViewModel
    @Mock
    // Create a StandardTestDispatcher for testing coroutines
    private val dispatcher = StandardTestDispatcher()


    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    // Setup method to initialize mocks and the ViewModel before each test
    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        // Initialize mocks
        MockitoAnnotations.openMocks(this)
        // Set the main dispatcher for coroutines to the test dispatcher
        Dispatchers.setMain(dispatcher)

        // Initialize the ViewModel with the mock repository
        viewModel = FirebaseViewModel(firebaseRepo)
    }

    // Tear down method to reset the main dispatcher after each test
    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // Test method to verify that LiveData updates correctly when new data is fetched
    @Test
    @ExperimentalCoroutinesApi
    fun testLiveDataUpdate() = runTest {
        // Arrange: Prepare the test data and expected outcome
        val book1 = Book("Book Title 1", "Author 1")
        val book2 = Book("Book Title 2", "Author 2")
        val expectedBooks = listOf(book1, book2) // Predefined list of books
        val specificSid = "2202587"
        // Mock the repository to return the expected books when called
        whenever(firebaseRepo.getRecentlyViewedBooks(specificSid)).thenReturn(flowOf(expectedBooks))
        // Create an observer to check the LiveData updates
        val observer = Observer<List<Book>> {
            // Assert: Check if the observed data matches the expected outcome
            assertEquals(expectedBooks, it)
        }
        // Act: Observe the LiveData and trigger the data fetching
        viewModel.observeRecentlyViewedBooks(observer)
        viewModel.getRecentlyViewedBooks(specificSid)

        // Advance the test coroutine dispatcher to execute coroutines until they are idle
        advanceUntilIdle()

        // Remove the observer to avoid memory leaks
        viewModel.recentlyViewedBooks.removeObserver(observer)
    }

    @Test
    @ExperimentalCoroutinesApi
    fun testGetCourse() = runTest {
        // Arrange: Prepare the test data and expected outcome
        val expectedCourse = "CS101"
        val sid = "sid123"
        viewModel.sid.value = sid
        // Mock the repository to return the expected course when called
        whenever(firebaseRepo.getCourse(sid)).thenReturn(flowOf(expectedCourse))
        // Create an observer to check the LiveData updates
        val observer = Observer<String> {
            // Assert: Check if the observed data matches the expected outcome
            assertEquals(expectedCourse, it)
        }
        // Act: Observe the LiveData and trigger the data fetching
        viewModel.course.observeForever(observer)
        viewModel.getCourse()

        // Advance the test coroutine dispatcher to execute coroutines until they are idle
        advanceUntilIdle()

        // Remove the observer to avoid memory leaks
        viewModel.course.removeObserver(observer)
    }

    @Test
    @ExperimentalCoroutinesApi
    fun testBorrowBookSuccess() = runTest {
        // Arrange: Prepare the test data
        val bookId = "123"
        val copyType = "hard"
        val sid = "sid123"
        val expectedResponse = Response.Success(true)
        viewModel.sid.value = sid
        // Mock the FirebaseRepo to return a success response
        whenever(firebaseRepo.borrowBook(sid, bookId, copyType)).thenReturn(expectedResponse)

        // Act: Call the borrowBook function
        viewModel.borrowBook(bookId, copyType)

        advanceUntilIdle() // Advance the test coroutine dispatcher until idle to ensure all coroutines have completed
        // Assert: Verify that the borrowBookSuccess LiveData is updated to true

        viewModel.borrowBookSuccess.value
        assertEquals(true, viewModel.borrowBookSuccess.value)
    }

    @Test
    @ExperimentalCoroutinesApi
    fun testGetStudentNameSuccess() = runTest {
        // Arrange: Prepare the test data and expected outcome
        val expectedName = "John Doe"
        val sid = "sid123"
        viewModel.sid.value = sid
        // Mock the repository to invoke the callback with the expected name when called
        whenever(firebaseRepo.getStudent(eq(sid), any())).thenAnswer { invocation ->
            val callback = invocation.getArgument<(String?) -> Unit>(1)
            callback(expectedName)
            Unit
        }
        // Create an observer to check the LiveData updates
        val observer = Observer<String> {
            // Assert: Check if the observed data matches the expected outcome
            assertEquals(expectedName, it)
        }
        // Act: Observe the LiveData and trigger the data fetching

        viewModel.getStudentName()

        // Advance the test coroutine dispatcher to execute coroutines until they are idle
        advanceUntilIdle()

        // Remove the observer to avoid memory leaks

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testMarkBookAsFavoriteSuccess() = runTest {
        // Arrange: Prepare the test data
        val book = Book(Title = "Introduction to Programming",
            Url = "https://example.com/programming",
            Course = "CS101",
            Description = "A beginner's course in programming.",
            hardCopies = 10,
            softCopies = 5,
            Pdf = "https://example.com/programming.pdf")
        val sid = "sid123"
        viewModel.sid.value = sid


        // Act: Call the markBookAsFavorite function
        viewModel.markBookAsFavorite(book)

        // Assert: Verify that the heartSuccess MutableState is updated to true
        advanceUntilIdle() // Advance the test coroutine dispatcher until idle to ensure all coroutines have completed
        assertEquals(true, viewModel.heartSuccess.value)
    }
}

