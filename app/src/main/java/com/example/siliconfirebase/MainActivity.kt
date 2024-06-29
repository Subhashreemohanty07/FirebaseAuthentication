package com.example.siliconfirebase
import android.content.ContentValues.TAG
import android.content.Context

import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import android.Manifest
import android.graphics.Insets.add
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.siliconfirebase.ui.theme.SiliconfirebaseTheme
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.siliconfirebase.ui.theme.LoginScreen
import com.example.siliconfirebase.ui.theme.SignUpScreen
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalMapOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.storage
import java.util.UUID



class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            //for camera mic and location
            SiliconfirebaseTheme {
                HomePage()
//                Column(
//                    modifier = Modifier.fillMaxSize()
//                        .padding(bottom = 20.dp),
//                    verticalArrangement = Arrangement.Center,
//                    horizontalAlignment = Alignment.CenterHorizontally
//
//                ) {
//                    ImageUploadScreen()
//                    PermissionRequestButton(permission = Manifest.permission.CAMERA, label = "Request Camera Permission")
//                    PermissionRequestButton(permission = Manifest.permission.RECORD_AUDIO, label = "Request Microphone Permission")
////                    PermissionRequestButton(permission = Manifest.permission.ACCESS_FINE_LOCATION, label = "Request Location Permission")
//                }
            }

            //add image
//            SiliconfirebaseTheme {
//                ImageUploadScreen()
//            }
            //For Fetch User(Read)
//            SiliconfirebaseTheme {
//                Surface(color = MaterialTheme.colorScheme.background) {
//                    fetchUserDisplay(navController = rememberNavController())
//                }
//            }

            //for add user(WRITE)
//            AddUserScreen()
            // For OTP Verification
//            SiliconfirebaseTheme {
//                OTPScreen()
//
//            }
            //FOR SIGNIN AND SIGNUP
//            val navController = rememberNavController()
//            NavHost(navController = navController, startDestination = "signup") {
//                composable("signup") {
//                    SignUpScreen(
//                        signUp = { email, password -> signUp(email, password, navController) },
//                        navController = navController
//                    )
//                }
//                composable("login") {
//                    LoginScreen(
//                        signIn = { email, password -> signIn(email, password) },
//                        navController = navController
//                    )
//                }
//            }
        }
    }

    @Composable
    fun PermissionRequestButton(permission: String, label: String) {
        val context = LocalContext.current
        val permissionLauncher =
            rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    Toast.makeText(context, "$label Granted", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(
                        context,
                        "$label Denied. Please enable it from settings.",
                        Toast.LENGTH_SHORT
                    ).show()
//                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
//                    data = Uri.fromParts("package", context.packageName, null)
//                }
//                context.startActivity(intent)
                }
            }

        Button(onClick = { permissionLauncher.launch(permission) }) {
            Text(text = label)
        }
    }


    private val auth = FirebaseAuth.getInstance()
    val firebaseDB = Firebase.firestore
    var storedVerificationId: String? = null
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken

    //SIgnup
    private fun signUp(email: String, password: String, navController: NavController) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    println("User created: ${auth.currentUser}")
                    navController.navigate("login")
                } else {
                    println("User couldn't be created: ${task.exception?.message}")
                }
            }
    }

    //for signin
    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    println("User Logged In: ${auth.currentUser?.email}")
                } else {
                    println("User couldn't be logged in: ${task.exception?.message}")
                }
            }
    }

    //for adduser
    fun addUserToFirebaseDB(context: Context,name: String, age: Int) {
        val Adult = age >= 18
        val firebaseUser = FirebaseUser(name, age, Adult)


        firebaseDB.collection("users")
            .add(firebaseUser)
            .addOnSuccessListener { dRef ->
                // Successfully added user to Firestore
                Toast.makeText(context, "User added successfully with ID: ${dRef.id}", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Document added with ID: ${dRef.id}")
            }
            .addOnFailureListener { e ->
                // Failed to add user to Firestore
                Toast.makeText(context, "User could not be added: $e", Toast.LENGTH_SHORT).show()
                Log.w(TAG, "Document Could Not be added: $e")
            }
    }

    //for fetch User---> means Read
    fun fetchFirebaseUser(onResult: (List<FirebaseUser>) -> Unit) {
        firebaseDB.collection("users")
            .get()
            .addOnSuccessListener { result ->
                val usersList = result.map { document ->
                    document.toObject(FirebaseUser::class.java)
                }
                onResult(usersList)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error Getting Data", e)
            }
    }

    //for image store in the firebasestorage
    val storage = Firebase.storage
    val storageRef = storage.reference
    fun uploadImage(uri: Uri, context: Context) {
        val fileName = "images/${UUID.randomUUID()}.jpg"
        val imageRef = storageRef.child(fileName)

        imageRef.putFile(uri)
            .addOnCompleteListener { takeSnapShot ->
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    Toast.makeText(
                        context,
                        "Image Uploaded Successfully:${uri}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    context,
                    "Image upload Failed:${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }

    }


    //....................................................................

    val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            Log.d(TAG, "onVerificationCompleted:$credential")
            signINWithPhoneCred(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            Log.w(TAG, "onVerificationFailed", e)

            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
            } else if (e is FirebaseAuthMissingActivityForRecaptchaException) {
                // reCAPTCHA verification attempted with null Activity
            }

            // Show a message and update the UI
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken,
        ) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            Log.d(TAG, "onCodeSent:$verificationId")

            // Save verification ID and resending token so we can use them later
            storedVerificationId = verificationId
            resendToken = token
        }
    }

    fun startPhoneNumberVerification(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun verifyPhoneWithCode(code: String) {
        val credential = PhoneAuthProvider.getCredential(storedVerificationId!!, code)
        signINWithPhoneCred(credential)

    }

    fun signINWithPhoneCred(cred: PhoneAuthCredential) {
        auth.signInWithCredential(cred)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")

                    val user = task.result?.user
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
                }
            }
    }

    @Composable
    fun OTPScreen() {
        val phoneNumber = remember { mutableStateOf("") }
        val otpCode = remember { mutableStateOf("") }
        val imageRes =
            painterResource(id = R.drawable.otplogo) // Replace with your actual image resource name

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            )
            {
                Box(
                    modifier = Modifier
                        .padding(20.dp)
                        .background(
                            color = Color(0xFFE4DDDD),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(25.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "OTP Verification",
                            fontSize = 30.sp,
                            color = Color.Black,

                            modifier = Modifier.padding(8.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Image(
                            painter = imageRes,
                            contentDescription = "OTP Verification",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                        )

                        Text(
                            text = "Enter your phone number to receive a one-time password",
                            fontSize = 16.sp,
                            color = Color.Gray,
                            fontStyle = FontStyle.Italic,
                            modifier = Modifier.padding(8.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        TextField(
                            value = phoneNumber.value,
                            onValueChange = { phoneNumber.value = it },
                            label = { Text(text = "Phone Number") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White, shape = RoundedCornerShape(8.dp)),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { startPhoneNumberVerification(phoneNumber.value) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDAB7AC))
                        ) {
                            Text(text = "Send OTP")
                        }
                        Spacer(modifier = Modifier.height(32.dp))
                        TextField(
                            value = otpCode.value,
                            onValueChange = { otpCode.value = it },
                            label = { Text(text = "Enter OTP") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White, shape = RoundedCornerShape(8.dp)),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { verifyPhoneWithCode(otpCode.value) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDAB7AC))
                        ) {
                            Text(text = "Verify OTP")
                        }
                    }
                }
            }
        }
    }

    //model
    data class FirebaseUser(
        val name: String = "",
        val age: Int = 0,
        val Adult: Boolean = false

    )

    @Composable
    fun AddUserScreen(onBackClick: () -> Unit) {
        val context = LocalContext.current
        val name = remember { mutableStateOf("") }
        val age = remember { mutableStateOf("") }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .padding(20.dp)
                        .background(
                            color = Color(0xFFDFE8EB),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(25.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "ADD User Detail",
                            fontSize = 30.sp,
                            color = Color.Black,
                            modifier = Modifier.padding(8.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        TextField(
                            value = name.value,
                            onValueChange = { name.value = it },
                            label = {
                                Text(
                                    text = "Enter Name", color = Color.Black
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White, shape = RoundedCornerShape(8.dp)),
//                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        TextField(
                            value = age.value,
                            onValueChange = { age.value = it },
                            label = {
                                Text(
                                    text = "Enter Age",
                                    color = Color.Black
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Gray, shape = RoundedCornerShape(8.dp)),
                            //keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                addUserToFirebaseDB(
                                    context,
                                    name.value,
                                    age.value.toInt()
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF324FEC))
                        ) {
                            Text(
                                text = "Add User",
                                color = Color.White,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun fetchUserDisplay(onBackClick: () -> Unit) {
        val usersList = remember {
            mutableStateOf<List<FirebaseUser>>(emptyList())
        }
        //all the api
        LaunchedEffect(Unit) {
            fetchFirebaseUser { users ->
                usersList.value = users
            }

        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Fetch The Existing Users",
                fontSize = 26.sp,
                color = Color.White,
                modifier = Modifier.padding(top = 8.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                items(usersList.value) { user ->
                    val backgroundColor =
                        if (usersList.value.indexOf(user) % 2 == 0) Color(0xFFB8C0D8) else Color(
                            0xFFF5ECF3
                        )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(backgroundColor)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "Name: ${user.name}, Age: ${user.age}",
                            fontSize = 18.sp,
                            color = Color.Black,
                            modifier = Modifier.padding(8.dp)
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Delete Button
                            Button(
                                onClick = {
                                    // Implement delete operation here
//                                    deleteUser(user.id)
                                },
//                               colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
                                modifier = Modifier.padding(horizontal = 8.dp)
                            ) {
                                Text(text = "Delete", color = Color.White)
                            }

                            // Update Button
                            Button(
                                onClick = {
                                    // Implement update operation here
                                    // updateUserInfo(user.id)
                                },
//                               colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF8A2BE2)),
                                modifier = Modifier.padding(horizontal = 8.dp)
                            ) {
                                Text(text = "Update", color = Color.White)
                            }
                        }
                    }
                    Divider()
                }
            }
        }
    }



    // Function to delete a user from Firestore
    fun deleteUser(userId: String) {
        val firebaseDB = Firebase.firestore
        firebaseDB.collection("users")
            .document(userId)
            .delete()
            .addOnSuccessListener {
                Log.d(TAG, "User deleted successfully")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error deleting user", e)
            }
    }
//

    @Composable
    fun ImageUploadScreen() {
        val context = LocalContext.current
        val imageUri = remember {
            mutableStateOf<Uri?>(null)
        }
        val launcher =
            rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                imageUri.value = uri
                uri.let { uploadImage(it!!, context) }

            }
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = { launcher.launch("image/*") }) {
                Text(text = "Select Image")
            }
            Spacer(modifier = Modifier.height(20.dp))
            imageUri.value.let { uri ->
                Image(
                    painter = rememberAsyncImagePainter(uri), contentDescription = "Upload Image",
                    modifier = Modifier.size(250.dp)
                )
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun HomePage() {
        var currentScreen by remember { mutableStateOf("home") }
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Dashboard",
                            fontSize = 35.sp,
                            textAlign = TextAlign.Center
                        )
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color(0xFF1E21E5)
                    )
                )
            },
            content = { padding ->
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    when (currentScreen) {
                        "home" -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                DashboardCard(iconId = R.drawable.img, label = "Insert User") {
                                    currentScreen = "addUser"
                                }
                                DashboardCard(iconId = R.drawable.img_1, label = "Fetch User") {
                                    currentScreen = "fetchUser"
                                }
                            }
                        }
                        "addUser" -> {
                            AddUserScreen(onBackClick = { currentScreen = "home" })
                        }
                        "fetchUser" -> {
                           fetchUserDisplay(onBackClick = { currentScreen = "home" })
                        }
                    }
                }
            }
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun DashboardCard(iconId: Int, label: String, onClick: () -> Unit) {

        Card(
            onClick = onClick,
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .width(150.dp)
                .height(150.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(id = iconId),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = label,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
    }

}