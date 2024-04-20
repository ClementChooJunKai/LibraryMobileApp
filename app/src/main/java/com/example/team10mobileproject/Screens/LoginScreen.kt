
import android.annotation.SuppressLint
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*



import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Icon
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.team10mobileproject.ViewModel.FirebaseViewModel
import com.example.team10mobileproject.R
import com.example.team10mobileproject.Screen

import com.example.team10mobileproject.ViewModel.Response
import com.example.team10mobileproject.ui.theme.gradientBackgroundBrush
import kotlinx.coroutines.launch


/**
 * Composable function for displaying the login screen.
 * @param navController NavController used for navigation within the app.
 * @param viewModel ViewModel for managing data and business logic.
 */
@SuppressLint("UnrememberedMutableState")
@Composable
fun LoginScreen(
    navController: NavController = rememberNavController(),
    viewModel: FirebaseViewModel,
) {

    val context = LocalContext.current // Assuming you are using Compose
    var signUpClicked by remember { mutableStateOf(false) }
    var signInClicked by remember { mutableStateOf(false) }
    var showForgetPasswordDialog by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var tabController by remember { mutableIntStateOf(0) }
    var username by remember { mutableStateOf("") } // Add sid variable
    var inputSid by remember { mutableStateOf("") }
    BackHandler(onBack = {
        navController.popBackStack()
        navController.navigate(Screen.LoginScreen.route)
    })

    Box(modifier = Modifier.background(gradientBackgroundBrush(true))) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()


            ) {
                // Display the image
                Image(
                    painter = painterResource(id = R.drawable.librarylogin),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()

                        .height(350.dp) // Adjust the height as needed
                        .blur(radiusX = 5.dp, radiusY = 5.dp),
                    contentScale = ContentScale.FillWidth

                )
                // Display the SVG logo on top of the image
                Image(
                    painter = painterResource(id = R.drawable.readifylogo), // Replace with your SVG resource
                    contentDescription = null,
                    modifier = Modifier
                        .size(350.dp)
                        .padding(16.dp),

                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.secondary)

                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                    .background(MaterialTheme.colorScheme.secondary)
                    .fillMaxSize()
                    .padding(horizontal = 15.dp)
            ) {
                Column(Modifier.padding(15.dp)) {
                    TabRow(
                        selectedTabIndex = tabController,
                        modifier = Modifier.fillMaxWidth(),

                        contentColor = MaterialTheme.colorScheme.primary
                    ) {
                        Tab(
                            selected = tabController == 0,
                            onClick = { tabController = 0 },
                            modifier = Modifier.testTag("LoginTab"),
                            content = {
                                Text(text = "Login", modifier = Modifier.padding(vertical = 8.dp))
                            }
                        )
                        Tab(
                            selected = tabController == 1,
                            onClick = { tabController = 1 },
                            modifier = Modifier.testTag("SignUpTab"),
                            content = {
                                Text(text = "Sign Up", modifier = Modifier.padding(vertical = 8.dp))
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(30.dp))

                    if (tabController == 0) {
                        // Login fields
                        TextField(
                            value = inputSid,
                            onValueChange = { inputSid = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .testTag("SID"),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Person,
                                    contentDescription = null
                                )
                            },
                            label = { Text("SID") }
                        )

                        val passwordVisualTransformation by remember {
                            mutableStateOf<VisualTransformation>(
                                PasswordVisualTransformation()
                            )
                        }

                        TextField(
                            value = password,
                            onValueChange = {
                                password = it
                                errorMessage = ""
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .testTag("Password"),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Lock,
                                    contentDescription = null
                                )
                            },
                            label = { Text("Password") },
                            visualTransformation = passwordVisualTransformation,
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Password
                            ),
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (inputSid.isNotEmpty() && password.isNotEmpty()) {
                                    signInClicked = true

                                } else {
                                    errorMessage = "Please enter both username and password"
                                }
                            },
                            colors  = ButtonDefaults.buttonColors(contentColor = Color.White),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("Login"),
                            enabled = inputSid.isNotEmpty() && password.isNotEmpty()
                        ) {
                            Text("Login" )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { showForgetPasswordDialog = true },
                            colors  = ButtonDefaults.buttonColors(contentColor = Color.White),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            enabled = true // Modify this based on your validation logic
                        ) {
                            Text("Forget Password")
                        }

                        // Show forget password dialog if `showForgetPasswordDialog` is true
                        if (showForgetPasswordDialog) {
                            ForgetPasswordDialog(
                                sid = remember { mutableStateOf(inputSid) } , onDismiss = { showForgetPasswordDialog = false },  viewModel
                            )
                        }

                        if (signInClicked) {
                            LaunchedEffect(Unit) {
                                if (signInClicked) {
                                    // Call firebaseSignInWithEmailAndPassword and wait for its result
                                    viewModel.firebaseSignInWithEmailAndPassword(
                                        inputSid + "@sit.singaporetech.edu.sg",
                                        password
                                    )

                                    // Process the result
                                    when (val signInResponse = viewModel.signInResponse) {
                                        is Response.Success ->  {
                                            viewModel.sid.value =inputSid
                                            navController.navigate("Homepage_screen")

                                            signInClicked = false
                                        }

                                        is Response.Failure-> signInResponse.apply {
                                            signInClicked = false
                                            Toast.makeText(
                                                context,
                                                "Sign-in failed:Wrong SID or Password!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }


                                    }
                                }
                            }

                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = errorMessage,
                            color = Color.Red,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        )
                    } else {
                        // Sign up fields
                        TextField(
                            value = username,
                            onValueChange = { username = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .testTag("SignUpUsername"),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Person,
                                    contentDescription = null
                                )
                            },
                            label = { Text("Name") }
                        )

                        TextField(
                            value = inputSid,
                            onValueChange = { newValue ->
                                // Filter out non-numeric characters
                                val filteredValue = newValue.filter { it.isDigit() }
                                // Check if the new value is exactly 7 characters long or if the user is deleting characters
                                if (filteredValue.length <= 7 || filteredValue.length < inputSid.length) {
                                    inputSid = filteredValue
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .testTag("SignUpSid")
                            ,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Person,
                                    contentDescription = null
                                )
                            },
                            label = { Text("SID") },
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                        )

                        TextField(
                            value = password,
                            onValueChange = { password = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .testTag("SignUpPassword"),

                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Lock,
                                    contentDescription = null
                                )
                            },
                            label = { Text("Password") },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { signUpClicked = true },
                            colors  = ButtonDefaults.buttonColors(contentColor = Color.White),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("SignUpButton"),
                            enabled = username.isNotEmpty() && inputSid.length == 7 && password.isNotEmpty() // Add other sign-up field validations
                        ) {
                            Text("Sign Up")
                        }

                        if (signUpClicked) {

                            LaunchedEffect(Unit) {
                                // Call firebaseSignUpWithEmailAndPassword and wait for its result
                                viewModel.firebaseSignUpWithEmailAndPassword(
                                    inputSid + "@sit.singaporetech.edu.sg",
                                    password
                                )

                                // Process the result
                                when (val signUpResponse = viewModel.signUpResponse) {

                                    is Response.Success -> {

                                        viewModel.registerStudent(username, password, inputSid)
                                        signUpClicked = false
                                        Toast.makeText(
                                            context,

                                            "Sign-up Successfully!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                    is Response.Failure -> signUpResponse.apply {

                                        signUpClicked = false
                                        Toast.makeText(
                                            context,
                                            "Sign-up failed: ${e.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }


                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


/**
 * Composable function for displaying the forget password dialog.
 * @param sid MutableState used to manage the SID input field.
 * @param onDismiss Function to dismiss the dialog.
 * @param viewModel ViewModel for managing data and business logic.
 */
@Composable
fun ForgetPasswordDialog(
    sid: MutableState<String>, // Change this to MutableState<String>
    onDismiss: () -> Unit,
    viewModel: FirebaseViewModel
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Forget Password") },
        text = {
            // Input field for SID
            OutlinedTextField(
                value = sid.value, // Use sid.value here
                onValueChange = { newValue ->
                    sid.value = newValue // Update sid.value here
                },
                label = { Text("SID") }
            )
        },
        confirmButton = {
            Row(  modifier = Modifier
                .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            )
            {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            viewModel.sendPasswordResetEmail(sid.value + "@sit.singaporetech.edu.sg")
                        }
                        Toast.makeText(
                            context,
                            "Sent reset password email!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    colors  = ButtonDefaults.buttonColors(contentColor = Color.White)
                )
                {
                    Text("Reset Password")
                }
            }
        },
    )
}


