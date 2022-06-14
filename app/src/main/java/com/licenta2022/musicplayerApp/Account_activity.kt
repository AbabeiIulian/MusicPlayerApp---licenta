package com.licenta2022.musicplayerApp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.licenta2022.musicplayerApp.other.Constants
import com.licenta2022.musicplayerApp.other.Constants.ANDROID_TAG
import com.licenta2022.musicplayerApp.other.Constants.NEW_USER
import com.licenta2022.musicplayerApp.ui.MainActivity
import kotlinx.android.synthetic.main.activity_account.*
import kotlinx.android.synthetic.main.activity_loggin.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class Account_activity : AppCompatActivity() {

    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection(Constants.DB_USERS)

    lateinit var auth: FirebaseAuth

    override fun onStart() {
        super.onStart()
        checkState()
    }


    override fun onBackPressed() {
        super.onBackPressed()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        auth = FirebaseAuth.getInstance()


        userEmail.setText(auth.currentUser?.email)

        imageButton.setOnClickListener{
            editUsername.isVisible = true
            imageButtonDone.isVisible = true
        }

        imageButtonDone.setOnClickListener{
            if (editUsername.text.toString().isNotEmpty()) {
                updateProfile()
            }
            editUsername.isVisible = false
            imageButtonDone.isVisible = false
            editUsername.text = null
        }

        btnHome.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        btnLogout.setOnClickListener{
            auth.signOut()
            val intent = Intent(this, Loggin_activity::class.java)
            startActivity(intent)
        }

        playlistButton.setOnClickListener {
            val intent = Intent(this, Playlist_activity::class.java)
            startActivity(intent) }

    }


    private fun checkState(){

        val user = auth.currentUser

            if (user?.displayName.toString().isNotEmpty()) {
                username.setText(user?.displayName)
            } else{
                username.setText(auth.currentUser?.email)
            }

        if (NEW_USER) {
            username.setText(auth.currentUser?.email)
            NEW_USER = false
        }


        isPremium()

    }

    private fun updateProfile(){
        auth.currentUser?.let { user->
            val username = editUsername.text.toString()
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .build()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    user.updateProfile(profileUpdates).await()
                    withContext(Dispatchers.Main){
                        checkState()
                        if (user.displayName!!.isNotEmpty()) {
                            Toast.makeText(
                                this@Account_activity,
                                "Profile updated successfully!",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                }catch (e: Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@Account_activity, e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }

        }
    }


    private fun isPremium(){

        val docRef = usersCollection.document(auth.currentUser?.uid.toString())
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(ANDROID_TAG, "DocumentSnapshot data: ${document.data?.get("premium")}")

                    val premiumCheck = document.data?.get("premium")

                    if (premiumCheck == true){
                        premiumButton.setBackgroundResource(R.drawable.ic_checked)
                        premiumTextcheck.text= "Premium"
                    }else{
                        premiumButton.setBackgroundResource(R.drawable.ic_unchecked)
                        premiumTextcheck.text= "Buy premium"
                    }


                } else {
                    Log.d(ANDROID_TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(ANDROID_TAG, "get failed with ", exception)
            }


    }

}