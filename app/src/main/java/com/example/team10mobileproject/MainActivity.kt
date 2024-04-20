package com.example.team10mobileproject




import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.team10mobileproject.Repo.FirebaseRepo
import com.example.team10mobileproject.ViewModel.FirebaseViewModel
import com.example.team10mobileproject.ViewModel.FirebaseViewModelFactory
import com.example.team10mobileproject.ui.theme.Team10MobileProjectTheme


class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)



        setContent {
            Team10MobileProjectTheme {
                val firebaseRepo = FirebaseRepo(applicationContext)
                val viewModelFactory = FirebaseViewModelFactory(firebaseRepo)
                val viewModel = ViewModelProvider(this, viewModelFactory)[FirebaseViewModel::class.java]
                val navController = rememberNavController()


                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    NavGraph(
                        navController = navController,
                        activity = this@MainActivity,
                        viewModel = viewModel,

                    )

                }
            }
        }
    }


}













