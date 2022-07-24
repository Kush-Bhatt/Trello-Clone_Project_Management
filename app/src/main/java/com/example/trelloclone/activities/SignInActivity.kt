package com.example.trelloclone.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import com.example.trelloclone.R
import com.example.trelloclone.databinding.ActivitySignInBinding
import com.example.trelloclone.firebase.FirestoreClass
import com.example.trelloclone.models.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : BaseActivity() {

    private var binding : ActivitySignInBinding? = null
//    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        //fullScreen
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
        )

        setSupportActionBar(binding?.toolbarSignInActivity)
        if(supportActionBar!=null)
        {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
            binding?.toolbarSignInActivity?.setNavigationOnClickListener {
                onBackPressed()
            }
        }

//        auth = Firebase.auth
        btn_sign_in.setOnClickListener {
            signInRegisteredUser()
        }
    }

    fun signInSuccess(user : User){
        hideProgressDialog()
        Log.e("Success","Hii")
        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }

    private fun signInRegisteredUser(){

        val email : String = et_email_sign_in.text.toString().trim{ it <= ' '}
        val password : String = et_password_sign_in.text.toString().trim{ it <= ' '}

        if(validateForm(email,password))
        {
            showProgressDialog(resources.getString(R.string.please_wait))

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
//                    hideProgressDialog()
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("Sign in", "signInWithEmail:success")
//                        val user = auth.currentUser
                        FirestoreClass().loadUserData(this)
                        //FirestoreClass().signInUser(SignInActivity)  It will show error

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("Sign in", "signInWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun validateForm(
        email: String,
        password: String,
    ): Boolean {
        return when {
            TextUtils.isEmpty(email) -> {
                showErrorSnackBar("Please Enter email.")
                false
            }
            TextUtils.isEmpty(password) -> {
                showErrorSnackBar("Please Enter password.")
                false
            }
            else -> {
                true
            }
        }
    }
}