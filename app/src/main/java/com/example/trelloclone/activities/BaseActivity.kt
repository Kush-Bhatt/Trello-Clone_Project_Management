package com.example.trelloclone.activities

import android.app.Dialog
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.example.trelloclone.R
import kotlinx.android.synthetic.main.dialog_progress.*
import com.example.trelloclone.databinding.ActivityBaseBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

open class BaseActivity : AppCompatActivity() {

    private var doubleBackToExitPressedOnce = false

//    private var tv_progress_text : TextView? = null
    private lateinit var progressDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
    }

    fun showProgressDialog(text: String) {
        progressDialog = Dialog(this)

        //Set the dialog content from a layout resource.
        progressDialog.setContentView(R.layout.dialog_progress)

//        var tv_progress_text = binding?.tv_progress_text?
        progressDialog.tv_progress_text.text = text

        //Start the dialog and display it on screen.
        progressDialog.show()
    }

    //This function is used to dismiss the progress dialog if it is visible to user.
    fun hideProgressDialog() {
        progressDialog.dismiss()
    }

    fun getCurrentUserID(): String {
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

    //if the user press back twice in 2 secs the application will be close
    fun doubleBackToExit() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(
            this,
            resources.getString(R.string.please_click_back_again_to_exit),
            Toast.LENGTH_SHORT
        ).show()

            // if user does not press back twice in 2 secs then
            //doubleBackToExitPressedOnce will be false (cannot close app)
            //have to press another 2 times in 2 secs to close the app
        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }

    //show snakeBar when the error occurs like Internet Issues
    fun showErrorSnackBar(message: String) {
        val snackBar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view
//        snackBarView.setBackgroundColor(
//            ContextCompat.getColor(
//                this@BaseActivity,
//                R.color.snackbar_error_color
//            )
//        )

//        val textView = snackBarView.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
//        textView.setTextColor(Color.parseColor("#4444DD"))
        snackBar.show()

    }
}