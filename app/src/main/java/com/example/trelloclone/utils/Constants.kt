package com.example.trelloclone.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import com.example.trelloclone.activities.MyProfileActivity

object Constants {
    const val USERS: String = "users"

    // Firebase database field names
    const val IMAGE: String = "image"
    const val NAME: String = "name"
    const val MOBILE: String = "mobile"
    const val ASSIGNED_TO = "assignedTo"
    const val READ_STORAGE_PERMISSION_CODE = 1
    const val PICK_IMAGE_REQUEST_CODE = 2

    // This  is used for the collection name for USERS.
    const val BOARDS: String = "boards"

    // We transfer this code from MyProfile Activity to Constants is to reduce the duplicate code
    fun showImageChooser(activity: Activity) {
        var galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        activity.startActivityForResult(galleryIntent,PICK_IMAGE_REQUEST_CODE)
        Log.i(
            "Gallery",
            "YES"
        )
    }

    fun getFileExtension(activity: Activity, uri: Uri?): String? {
        /*
        * MimeTypeMap: Two-way map that maps MIME-types to file extensions and vice versa.
        * getSingleton(): Get the singleton instance of MimeTypeMap.
        * getExtensionFromMimeType: Return the registered extension for the given MIME type.
        * contentResolver.getType: Return the MIME type of the given content URL.
        */
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }

}