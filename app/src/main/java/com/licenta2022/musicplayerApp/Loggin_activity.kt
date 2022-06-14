package com.licenta2022.musicplayerApp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.licenta2022.musicplayerApp.ui.MainActivity
import kotlinx.android.synthetic.main.activity_account.*
import kotlinx.android.synthetic.main.activity_loggin.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class Loggin_activity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loggin)
        auth = FirebaseAuth.getInstance()

        btnHome1.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnLogin.setOnClickListener{
            loginUser()
        }

        btnRegister1.setOnClickListener{
            val intent = Intent(this, Register_activity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        checkLoggedInState()
    }

    private fun loginUser() {
        val email = etEmailLogin.text.toString()
        val password = etPasswordLogin.text.toString()
        if(email.isNotEmpty() && password.isNotEmpty()){
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.signInWithEmailAndPassword(email,password).await()
                    withContext(Dispatchers.Main){
                        checkLoggedInState()
                    }
                }catch (e: Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@Loggin_activity, e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun checkLoggedInState(){
        if (auth.currentUser == null){
            tvLoggedIn.text = "If you don't have an account yet, please press the Register button!"
        }else{
            tvLoggedIn.text = "You are logged in"
            //btnRegister2.setOnClickListener(){
            val intent = Intent(this, Account_activity::class.java)
            startActivity(intent)
            finish()
            //}
        }
    }


}