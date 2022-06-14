package com.licenta2022.musicplayerApp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.licenta2022.musicplayerApp.other.Constants
import com.licenta2022.musicplayerApp.other.Constants.NEW_USER
import kotlinx.android.synthetic.main.activity_loggin.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class Register_activity : AppCompatActivity() {

    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection(Constants.DB_USERS)

    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        auth = FirebaseAuth.getInstance()

        btnRegister2.setOnClickListener{
            registerUser()
        }
    }

    private fun registerUser() {
        val email = etEmailRegister.text.toString()
        val password = etPasswordRegister.text.toString()
        if(email.isNotEmpty() && password.isNotEmpty()){
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.createUserWithEmailAndPassword(email,password).await()
                    withContext(Dispatchers.Main){
                        checkLoggedInState()
                    }
                }catch (e: Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@Register_activity, e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun checkLoggedInState(){
        if (auth.currentUser == null){
            tvRegistered.text = "Something went wrong, please try again!"
        }else{
            tvRegistered.text = "You are logged in"
            //btnRegister2.setOnClickListener(){
            addUserDB(auth.currentUser?.uid.toString())
                NEW_USER = true
                val intent = Intent(this, Account_activity::class.java)
                startActivity(intent)
                finish()
            //}
        }
    }

    private fun addUserDB(userId: String){
        val user = hashMapOf(
            "premium" to false
        )

        usersCollection.document(userId)
            .set(user)
            .addOnSuccessListener { Log.d(Constants.ANDROID_TAG, "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w(Constants.ANDROID_TAG, "Error writing document", e) }
    }

}