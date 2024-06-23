package com.example.siliconfirebase

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.siliconfirebase.ui.theme.LoginScreen
import com.example.siliconfirebase.ui.theme.SignUpScreen
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "signup") {
                composable("signup") {
                    SignUpScreen(
                        signUp = { email, password -> signUp(email, password, navController) }
                    )
                }
                composable("login") {
                    LoginScreen(
                        signIn = { email, password -> signIn(email, password) }
                    )
                }
            }
        }
    }

    private fun signUp(email: String, password: String, navController: NavController) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    println("User created: $user")
                    navController.navigate("login")
                } else {
                    println("User couldn't be created")
                    println(task.exception?.message)
                }
            }
    }

    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser?.email
                    println("User Logged In: $user")
                } else {
                    println("User couldn't be logged in")
                    println("User couldn't be logged in${task.exception?.message}")
                }
            }
    }
}
