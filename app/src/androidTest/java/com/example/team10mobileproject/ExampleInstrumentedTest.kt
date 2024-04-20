package com.example.team10mobileproject

import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Timer
import kotlin.concurrent.schedule

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

/**
 *  THESE ARE INDIVIDUAL TEST CASES SO DO NOT RUN THEM AT THE SAME TIME RUN THEM INDIVIDUALLY
 *
 */

//Testing with Signing up test case
    @Test
    fun testSignupSuccess() {
        composeTestRule.onNodeWithTag("SignUpTab").performClick()
        composeTestRule.onNodeWithTag("SignUpUsername").performTextInput("TestUser")
        composeTestRule.onNodeWithTag("SignUpSid").performTextInput("9876543")
        composeTestRule.onNodeWithTag("SignUpPassword").performTextInput("654321")
        // Enter SID and password


        // Check if the app navigates to the Homepage by checking for a specific element
        // Replace "Welcome to Homepage" with the actual text or element that uniquely identifies the Homepage in your app.

    }
    @Test
    //Testing with  Login test case
    fun testLoginScreenSuccess() {
        composeTestRule.onNodeWithTag("LoginTab").performClick()
        composeTestRule.onNodeWithTag("SID").performTextInput("2202587")
        composeTestRule.onNodeWithTag("Password").performTextInput("1234567")
        composeTestRule.onNodeWithTag("Login").performClick()
        composeTestRule.waitUntilTimeout(3000L)
        composeTestRule.onNodeWithTag("Box1").assertExists()

    }

    @Test
    //Testing with Borrowing of books test case
    fun testBorrowSuccess() {
        composeTestRule.onNodeWithTag("LoginTab").performClick()
        composeTestRule.onNodeWithTag("SID").performTextInput("2202587")
        composeTestRule.onNodeWithTag("Password").performTextInput("1234567")
        composeTestRule.onNodeWithTag("Login").performClick()
        composeTestRule.waitUntilTimeout(3000L)
        composeTestRule.onNodeWithTag("Box1").assertExists()
        composeTestRule.onNodeWithTag("BookExample0").performClick()
        composeTestRule.waitUntilTimeout(1000L)
        composeTestRule.onNodeWithTag("ReadMore").performClick()
        composeTestRule.waitUntilTimeout(2000L)
        composeTestRule.onNodeWithTag("BorroWButton").performClick()

        composeTestRule.waitUntilTimeout(1000L)
        composeTestRule.onNodeWithTag("Virtual").performClick()
        composeTestRule.onNodeWithTag("libraryIcon").performClick()
        composeTestRule.waitUntilTimeout(2000L)
        composeTestRule.onNodeWithTag("E-Books").performClick()
        composeTestRule.waitUntilTimeout(2000L)
        composeTestRule.onNodeWithText("Computer Science Distilled: Learn the Art of Solving Computational Problems").isDisplayed()
        }

    @Test
    //Testing with Borrowing and returning of books test case
    fun testBorrowandReturnSuccess() {
        composeTestRule.onNodeWithTag("LoginTab").performClick()
        composeTestRule.onNodeWithTag("SID").performTextInput("2202587")
        composeTestRule.onNodeWithTag("Password").performTextInput("1234567")
        composeTestRule.onNodeWithTag("Login").performClick()
        composeTestRule.waitUntilTimeout(3000L)
        composeTestRule.onNodeWithTag("Box1").assertExists()
        composeTestRule.onNodeWithTag("BookExample0").performClick()
        composeTestRule.waitUntilTimeout(1000L)
        composeTestRule.onNodeWithTag("ReadMore").performClick()
        composeTestRule.waitUntilTimeout(1000L)
        composeTestRule.onNodeWithTag("BorroWButton").performClick()
        composeTestRule.waitUntilTimeout(1000L)
        composeTestRule.onNodeWithTag("Virtual").performClick()
        composeTestRule.onNodeWithTag("libraryIcon").performClick()
        composeTestRule.waitUntilTimeout(2000L)
        composeTestRule.onNodeWithTag("E-Books").performClick()
        composeTestRule.waitUntilTimeout(2000L)
        composeTestRule.onNodeWithText("Computer Science Distilled: Learn the Art of Solving Computational Problems").isDisplayed()
        composeTestRule.onNodeWithTag("Return").performClick()
        composeTestRule.onNodeWithTag("homeIcon").performClick()
        composeTestRule.onNodeWithTag("BookExample0").performClick()
        composeTestRule.waitUntilTimeout(1000L)
        composeTestRule.onNodeWithTag("ReadMore").performClick()
        composeTestRule.waitUntilTimeout(1000L)
        composeTestRule.onNodeWithTag("BorroWButton").performClick()
        composeTestRule.waitUntilTimeout(1000L)
        composeTestRule.onNodeWithTag("Virtual").performClick()
    }
    //Testing with Wishlist test case
    @Test
    fun testWishlistSuccess() {
        composeTestRule.onNodeWithTag("LoginTab").performClick()
        composeTestRule.onNodeWithTag("SID").performTextInput("2202587")
        composeTestRule.onNodeWithTag("Password").performTextInput("1234567")
        composeTestRule.onNodeWithTag("Login").performClick()
        composeTestRule.waitUntilTimeout(3000L)
        composeTestRule.onNodeWithTag("Box1").assertExists()
        composeTestRule.onNodeWithTag("BookExample0").performClick()
        composeTestRule.waitUntilTimeout(1000L)
        composeTestRule.onNodeWithTag("ReadMore").performClick()
        composeTestRule.waitUntilTimeout(1000L)
        composeTestRule.onNodeWithTag("WishlistIcon").performClick()
        composeTestRule.onNodeWithTag("libraryIcon").performClick()
        composeTestRule.waitUntilTimeout(2000L)
        composeTestRule.onNodeWithTag("Wishlist").performClick()
        composeTestRule.waitUntilTimeout(2000L)
        composeTestRule.onNodeWithText("Computer Science Distilled: Learn the Art of Solving Computational Problems").isDisplayed()
        composeTestRule.onNodeWithTag("homeIcon").performClick()
        composeTestRule.onNodeWithTag("BookExample1").performClick()
        composeTestRule.waitUntilTimeout(1000L)
        composeTestRule.onNodeWithTag("ReadMore").performClick()
        composeTestRule.waitUntilTimeout(1000L)
    }

    //Testing with PDF test case
    @Test
    fun testPDFSuccess() {
        composeTestRule.onNodeWithTag("LoginTab").performClick()
        composeTestRule.onNodeWithTag("SID").performTextInput("2202587")
        composeTestRule.onNodeWithTag("Password").performTextInput("1234567")
        composeTestRule.onNodeWithTag("Login").performClick()
        composeTestRule.waitUntilTimeout(3000L)
        composeTestRule.onNodeWithTag("Box1").assertExists()
        composeTestRule.onNodeWithTag("BookExample0").performClick()
        composeTestRule.waitUntilTimeout(1000L)
        composeTestRule.onNodeWithTag("ReadMore").performClick()
        composeTestRule.waitUntilTimeout(1000L)
        composeTestRule.onNodeWithTag("BorroWButton").performClick()
        composeTestRule.waitUntilTimeout(1000L)
        composeTestRule.onNodeWithTag("Virtual").performClick()
        composeTestRule.onNodeWithTag("libraryIcon").performClick()
        composeTestRule.waitUntilTimeout(2000L)
        composeTestRule.onNodeWithTag("E-Books").performClick()
        composeTestRule.waitUntilTimeout(2000L)
        composeTestRule.onNodeWithText("Computer Science Distilled: Learn the Art of Solving Computational Problems").isDisplayed()
        composeTestRule.onNodeWithTag("E-Books").performClick()
        composeTestRule.onNodeWithTag("Read").performClick()
        composeTestRule.waitUntilTimeout(4000L)
    }

    //Testing the Search function in library collection
    @Test
    fun testSearchSuccess() {
        composeTestRule.onNodeWithTag("LoginTab").performClick()
        composeTestRule.onNodeWithTag("SID").performTextInput("2202587")
        composeTestRule.onNodeWithTag("Password").performTextInput("1234567")
        composeTestRule.onNodeWithTag("Login").performClick()
        composeTestRule.waitUntilTimeout(3000L)
        composeTestRule.onNodeWithTag("magnifyingglassIcon").performClick()
        composeTestRule.waitUntilTimeout(2000L)
        composeTestRule.onNodeWithTag("Searchbar").performTextInput("Computer Science Distilled")
        composeTestRule.waitUntilTimeout(3000L)
        composeTestRule.onNodeWithText("Computer Science Distilled: Learn the Art of Solving Computational Problems").isDisplayed()

    }






    fun ComposeContentTestRule.waitUntilTimeout(
        timeoutMillis: Long
    ) {
        AsyncTimer.start(timeoutMillis)
        this.waitUntil(
            condition = { AsyncTimer.expired },
            timeoutMillis = timeoutMillis + 1000
        )
    }

    object AsyncTimer {
        var expired = false
        fun start(delay: Long = 1000) {
            expired = false
            Timer().schedule(delay) {
                expired = true
            }
        }
    }


    }




