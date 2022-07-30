package com.example.trelloclone.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.WindowManager
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.trelloclone.R
import com.example.trelloclone.firebase.FirestoreClass
import com.example.trelloclone.models.User
import com.example.trelloclone.utils.Constants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_my_profile.*
import java.io.IOException

class MyProfileActivity : BaseActivity() {

    // A global variable for URI of a selected image from phone storage.
    private var selectedPhotoURI: Uri? = null

    // A global variable for a user profile image URL
    private var profileImageURI: String = ""

    // A global variable for user details.
    private lateinit var userDetails: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)

        //fullScreen
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
        )

        setupActionBar()
        FirestoreClass().loadUserData(this@MyProfileActivity)

        iv_profile_user_image.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
            ) {
                Constants.showImageChooser(this)
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                   Constants.READ_STORAGE_PERMISSION_CODE
                )
            }
        }

        btn_update.setOnClickListener {
            if (selectedPhotoURI != null) {
                uploadUserImage()
            } else {
                Log.e("button","HII")
                showProgressDialog(resources.getString(R.string.please_wait))
                updateUserProfileData()
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this)
            } else {
                Toast.makeText(
                    this,
                    "You just denied Permission. You can allow permission from the Settings.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK
            && requestCode == Constants.PICK_IMAGE_REQUEST_CODE
            && data!!.data != null
        ) {
            selectedPhotoURI = data.data

            try {
                Glide
                    .with(this@MyProfileActivity)
                    .load(selectedPhotoURI)
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(iv_profile_user_image)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun setupActionBar() {

        setSupportActionBar(toolbar_my_profile_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = resources.getString(R.string.my_profile)
        }

        toolbar_my_profile_activity.setNavigationOnClickListener { onBackPressed() }
    }

    fun setUserDataInUI(user: User) {

        userDetails = user

        Glide
            .with(this@MyProfileActivity)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(iv_profile_user_image)

        et_name.setText(user.name)
        et_email.setText(user.email)
        if (user.mobile != "") {
            et_mobile.setText(user.mobile.toString())
        }
    }

    private fun uploadUserImage() {
        Log.i(
            "Firebase Enter",
            "yes"
        )
        showProgressDialog(resources.getString(R.string.please_wait))

        //getting the storage reference
        val storageRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            "USER_IMAGE" + (System.currentTimeMillis() / 1000) + "."
                    + Constants.getFileExtension(this,selectedPhotoURI)
        )
        //adding the file to the reference
        storageRef.putFile(selectedPhotoURI!!).addOnSuccessListener { taskSnapshot ->
            Log.e(
                "Firebase Image URL",
               taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
            )
            // Get the downloadable url from the task snapshot
            taskSnapshot.metadata!!.reference!!.downloadUrl
                .addOnSuccessListener { uri ->
                    Log.e("Downloadable Image URL", uri.toString())
//                    showProgressDialog("Error")
                    // assign the image url to the variable.
                    profileImageURI = uri.toString()
//                    hideProgressDialog()
                    // Call a function to update user details in the database.
                    updateUserProfileData()
//                    showProgressDialog("Error")
                }
        }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    this@MyProfileActivity,
                    exception.message,
                    Toast.LENGTH_LONG
                ).show()
                hideProgressDialog()
            }
    }

    //update user data if changes are made
    private fun updateUserProfileData() {

        Log.e("Profile Update", "Yes")
        var userHashMap = HashMap<String, Any>()

        var anyChangesMade = false

        if (profileImageURI.isNotEmpty()  && profileImageURI != userDetails.image) {
            userHashMap[Constants.IMAGE] = profileImageURI
            anyChangesMade = true
        }
        //userHashMap["image"]

        if (et_name.text.toString() != userDetails.name) {
            userHashMap[Constants.NAME] = et_name.text.toString()
            anyChangesMade = true
        }

        if (et_mobile.text.toString() != userDetails.mobile.toString()) {
            userHashMap[Constants.MOBILE] = et_mobile.text.toString()
            anyChangesMade = true
        }

        // Update the data in the database.
        if (anyChangesMade)
            FirestoreClass().updateUserProfileData(this, userHashMap)
        else {
            hideProgressDialog()
            Toast.makeText(this, "No changes were made.", Toast.LENGTH_SHORT).show()
        }
    }

    fun profileUpdateSuccess() {
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }
}