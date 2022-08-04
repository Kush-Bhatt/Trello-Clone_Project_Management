package com.example.trelloclone.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.trelloclone.R
import com.example.trelloclone.adapters.BoardItemsAdapters
import com.example.trelloclone.firebase.FirestoreClass
import com.example.trelloclone.models.Board
import com.example.trelloclone.models.User
import com.example.trelloclone.utils.Constants
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.main_content.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object {
        private const val MY_PROFILE_REQUEST_CODE : Int = 11
        private const val CREATE_BOARD_REQUEST_CODE : Int= 12
    }

    private lateinit var userName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //fullScreen
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
        )

        setupActionBar()

        // Assign the NavigationView.OnNavigationItemSelectedListener to navigation view.
        nav_view.setNavigationItemSelectedListener(this)


        FirestoreClass().loadUserData(this,true)

        //floating action button to navigate to Create Board Activity
        fab_create_board.setOnClickListener {
            var intent = Intent(this, CreateBoardActivity::class.java)
            intent.putExtra(Constants.NAME,userName)
            startActivityForResult(intent, CREATE_BOARD_REQUEST_CODE)
        }
    }

    //    setup ActionBar
    private fun setupActionBar() {
        setSupportActionBar(toolbar_main_activity)

        toolbar_main_activity.setNavigationIcon(R.drawable.ic_action_navigation_menu)

        toolbar_main_activity.setNavigationOnClickListener {
            toggleDrawer()
        }
    }

    //    A function for opening and closing the Navigation Drawer.
    private fun toggleDrawer() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START))
            drawer_layout.closeDrawer(GravityCompat.START)
        else
            drawer_layout.openDrawer(GravityCompat.START)
    }

    //BackButton Pressed
    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            // A double back press function is added in Base Activity.
            doubleBackToExit()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_my_profile -> {
//                Toast.makeText(this,"My Profile",Toast.LENGTH_SHORT).show()
                startActivityForResult(Intent(this,MyProfileActivity::class.java),
                    MY_PROFILE_REQUEST_CODE)
            }
            R.id.nav_sign_out -> {
                // Here sign outs the user from firebase.
                FirebaseAuth.getInstance().signOut()

                // Send the user to the intro screen of the application.
                val intent = Intent(this, IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    fun updateNavigationUserDetails(user: User,readBoardList : Boolean) {
        userName = user.name
        // Load the user image in the ImageView.
        Glide
            .with(this@MainActivity)
            .load(user.image) // URL of the image
            .centerCrop() // Scale type of the image.
            .placeholder(R.drawable.ic_user_place_holder) // A default place holder
            .into(nav_user_image) // the view in which the image will be loaded.

        tv_username.text = user.name

        if(readBoardList)
        {
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().getBoardsList(this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK
            && requestCode == MY_PROFILE_REQUEST_CODE)
        {
            FirestoreClass().loadUserData(this)
        }
        else if(resultCode == Activity.RESULT_OK
            && requestCode == CREATE_BOARD_REQUEST_CODE)
        {
            FirestoreClass().getBoardsList(this)
        }
        else{
            Log.e("Not Updated Profile","Not Updated")
        }
    }

    fun populateBoardsListInUI(boardList: ArrayList<Board>) {
        hideProgressDialog()

        if (boardList.size > 0) {
            Log.i("Size","Greater than zero")
            rv_boards_list.visibility = View.VISIBLE
            tv_no_boards_available.visibility = View.GONE

            rv_boards_list.layoutManager = LinearLayoutManager(this)
            rv_boards_list.setHasFixedSize(true)

            // Create an instance of BoardItemsAdapter and pass the boardList to it.
            val adapter = BoardItemsAdapters(this,boardList)
            rv_boards_list.adapter = adapter //Attach the adapter to the recycle view
//            adapter.notifyDataSetChanged()

            adapter.setOnClickListener(object :
                BoardItemsAdapters.OnClickListener {
                override fun onClick(position: Int, model: Board) {
                    val intent = Intent(this@MainActivity, TaskListActivity::class.java)
                    intent.putExtra(Constants.DOCUMENT_ID, model.documentId)
                    startActivity(intent)
                }
            })

        } else {
            Log.i("Size","Less than zero")
            rv_boards_list.visibility = View.GONE
            tv_no_boards_available.visibility = View.VISIBLE
        }
    }
}

// by default project management text inside toolbar app_bar_main   doubt