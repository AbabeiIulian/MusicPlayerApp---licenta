package com.licenta2022.musicplayerApp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
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
    


    lateinit var auth: FirebaseAuth

    override fun onStart() {
        super.onStart()
        checkState()
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
            updateProfile()
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

    }

    private fun checkState(){

        val user = auth.currentUser
        if (NEW_USER == false) {
            if (user?.displayName.toString().isNotEmpty()) {
                username.setText(user?.displayName)
            }
        }else{
            username.setText(auth.currentUser?.email)
            NEW_USER = false
        }

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

}