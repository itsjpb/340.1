package com.itsjpb13

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.text.set
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest


class MainActivity : AppCompatActivity() {
    private lateinit var username : String
    private lateinit var email : String
    private lateinit var pass : String




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val viewText = toolbar.setTitle("Jason's App")

        FirebaseApp.initializeApp(this)
        val usernameField = findViewById<EditText>(R.id.loginText)
        val emailField = findViewById<EditText>(R.id.email_field)
        val passwordField = findViewById<EditText>(R.id.password_field)
        val loginButton =   findViewById<Button>(R.id.login_button)
        val buyButton = findViewById<Button>(R.id.buyButton)
        val sellButton = findViewById<Button>(R.id.sellButton)
        val tradeButton = findViewById<Button>(R.id.tradeButton)
        val giveButton = findViewById<Button>(R.id.giveButton)

        val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
        val storedUser = sharedPref.getString("USER", "")
        val storedEmail = sharedPref.getString("EMAIL", "")
        val storedPass = sharedPref.getString("PASS", "")



        if (!storedUser.equals("") && !storedEmail.equals("") && !storedPass.equals("")) {
            usernameField.setText(storedUser)
            emailField.setText(storedEmail)
            passwordField.setText(storedPass)
        }






        loginButton.setOnClickListener{


            username = usernameField.text.toString()
            email = emailField.text.toString()
            pass = passwordField.text.toString()
            signIn()

        }

        buyButton.setOnClickListener {
            val intent = Intent(this, movieActivity::class.java)
            startActivity(intent)
    }

        sellButton.setOnClickListener {
            val intent = Intent(this, Cam_Landing_Activity::class.java)
            startActivity(intent)
        }

        tradeButton.setOnClickListener {
            val intent = Intent(this, CamMap::class.java)
            startActivity(intent)
        }

        giveButton.setOnClickListener {
            Toast.makeText(this, "albums", Toast.LENGTH_LONG).show()
        }


    }

    fun isEmailValid(email: CharSequence?): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    fun validate(): Boolean{
        if (username == "")
            return false
        if (email == "")
            return false
        if (pass
            == "")
            return false
        return true
    }

    private fun signIn() {
        Log.d("FIREBASE", "signIn")
        val mAuth = FirebaseAuth.getInstance();

        if(!validate()){
            Toast.makeText(this, "please complete all fields", Toast.LENGTH_SHORT).show()
        /*    usernameField.text = null
            emailField.text = null
            passwordField.text = null*/
        } else if(!isEmailValid(email)) {
            Toast.makeText(this, "please enter a valid email address",
                Toast.LENGTH_SHORT).show()
           // emailField.text = null
        } else {


            mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(
                    this
                ) { task ->
                    Log.d("FIREBASE", "signIn:onComplete:" + task.isSuccessful)
                    if (task.isSuccessful) {
                        val thesePreferences = this.getPreferences(Context.MODE_PRIVATE)
                        val help = thesePreferences.edit()
                        help.apply {
                            putString("USER", username)
                            putString("EMAIL", email)
                            putString("PASS", pass)
                        }.apply()

                        // update profile
                        val user = FirebaseAuth.getInstance().currentUser
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(username)
                            .build()
                        user!!.updateProfile(profileUpdates)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Log.d("FIREBASE", "User profile updated.")
                                    // Go to FirebaseActivity
                                    ContextCompat.startActivity(this,
                                        Intent(
                                            this,
                                            FirebaseActivity::class.java
                                        ), null
                                    )
                                }
                            }
                    } else {
                        Log.d("FIREBASE", "sign-in failed")
                    }
                }
        }
    }
}


