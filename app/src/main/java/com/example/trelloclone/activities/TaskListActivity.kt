package com.example.trelloclone.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trelloclone.R
import com.example.trelloclone.adapters.TaskListItemsAdapter
import com.example.trelloclone.firebase.FirestoreClass
import com.example.trelloclone.models.Board
import com.example.trelloclone.models.Card
import com.example.trelloclone.models.Task
import com.example.trelloclone.models.User
import com.example.trelloclone.utils.Constants
import kotlinx.android.synthetic.main.activity_task_list.*

class TaskListActivity : BaseActivity() {

    // A global variable for Board Details.
    private lateinit var boardDetails: Board

    private lateinit var boardDocumentId : String

    // A global variable for Assigned Members List.
    lateinit var assignedMembersDetailList: ArrayList<User>

    companion object {
        //A unique code for starting the activity for result
        const val MEMBERS_REQUEST_CODE: Int = 13

        const val CARD_DETAILS_REQUEST_CODE: Int = 14
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)


        if (intent.hasExtra(Constants.DOCUMENT_ID)) {
            boardDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID)!!
        }

        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getBoardDetails(this@TaskListActivity, boardDocumentId)

    }

    private fun setupActionBar() {

        setSupportActionBar(toolbar_task_list_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = boardDetails.name
        }

        toolbar_task_list_activity.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu to use in the action bar
        menuInflater.inflate(R.menu.menu_members, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        when (item.itemId) {
            R.id.action_members -> {

                val intent = Intent(this@TaskListActivity, MembersActivity::class.java)
                intent.putExtra(Constants.BOARD_DETAIL, boardDetails)
                //for reloading taskActivity if any changes were made
                //lets say users add a member in any particular task and suppose that user remains in that add member
                //activity for some time but the added member made some changes in the taskactivity so to display it we reload the
                //data of taskactivity when any changes made

                startActivityForResult(intent, MEMBERS_REQUEST_CODE)

                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK
            && (requestCode == MEMBERS_REQUEST_CODE
            || requestCode == CARD_DETAILS_REQUEST_CODE)
        ) {
            // Show the progress dialog.
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().getBoardDetails(this@TaskListActivity, boardDocumentId)
        } else {
            Log.e("Cancelled", "Cancelled")
        }
    }

    fun boardDetails(board: Board) {

        boardDetails = board

        hideProgressDialog()

        // Call the function to setup action bar.
        setupActionBar()

//         Setup the task list view using the adapter class and task list of the board.
//         Here we are appending an item view for adding a list task list for the board.
//        val addTaskList = Task(resources.getString(R.string.add_list))  //doubt
//        boardDetails.taskList.add(addTaskList)
//
//        rv_task_list.layoutManager =
//            LinearLayoutManager(this@TaskListActivity, LinearLayoutManager.HORIZONTAL, false)
//        rv_task_list.setHasFixedSize(true)
//
//        // Create an instance of TaskListItemsAdapter and pass the task list to it.
//        val adapter = TaskListItemsAdapter(this@TaskListActivity, board.taskList)
//        rv_task_list.adapter = adapter // Attach the adapter to the recyclerView.

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getAssignedMembersListDetails(this, boardDetails.assignedTo)
    }

    fun createTaskList(taskListName: String) {

        Log.i("Done Img","Done Img")

        // Create and Assign the task details
        val task = Task(taskListName, FirestoreClass().getCurrentUserId())

        boardDetails.taskList.add(0, task) // Add task to the first position of ArrayList
        boardDetails.taskList.removeAt(boardDetails.taskList.size - 1) // Remove the last position as we have added the item manually for adding the TaskList.

        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().addUpdateTaskList(this@TaskListActivity,boardDetails)
    }

    fun addUpdateTaskListSuccess() {

        hideProgressDialog()

        // Here get the updated board details.
        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getBoardDetails(this@TaskListActivity, boardDetails.documentId)
    }

    fun updateTaskList(position: Int, listName: String, model: Task) {

        val task = Task(listName, model.createdBy)

        boardDetails.taskList[position] = task
        boardDetails.taskList.removeAt(boardDetails.taskList.size - 1)

        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this@TaskListActivity, boardDetails)
    }

    fun deleteTaskList(position: Int) {

        boardDetails.taskList.removeAt(position)

        boardDetails.taskList.removeAt(boardDetails.taskList.size - 1)

        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this@TaskListActivity, boardDetails)
    }

    fun addCardToTaskList(position: Int, cardName: String) {

        // Remove the last item
        boardDetails.taskList.removeAt(boardDetails.taskList.size - 1)

        val cardAssignedUsersList: ArrayList<String> = ArrayList()

        //Initially no one is assigned that card task (will later on decide the member os the card)
//        cardAssignedUsersList.add(FirestoreClass().getCurrentUserId())

        val card = Card(cardName, FirestoreClass().getCurrentUserId(), cardAssignedUsersList)

        val cardsList = boardDetails.taskList[position].cards
        cardsList.add(card)

        val task = Task(
            boardDetails.taskList[position].title,
            boardDetails.taskList[position].createdBy,
            cardsList
        )

        boardDetails.taskList[position] = task

        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this@TaskListActivity, boardDetails)
    }

    fun boardMembersDetailList(list: ArrayList<User>) {
        assignedMembersDetailList = list
        hideProgressDialog()

        val addTaskList = Task(resources.getString(R.string.add_list))  //(add list) button
        boardDetails.taskList.add(addTaskList)

        rv_task_list.layoutManager =
            LinearLayoutManager(this@TaskListActivity, LinearLayoutManager.HORIZONTAL, false)
        rv_task_list.setHasFixedSize(true)

        // Create an instance of TaskListItemsAdapter and pass the task list to it.
        val adapter = TaskListItemsAdapter(this@TaskListActivity, boardDetails.taskList)
        rv_task_list.adapter = adapter // Attach the adapter to the recyclerView.
    }


    fun cardDetails(taskListPosition: Int, cardPosition: Int) {
        val intent = Intent(this@TaskListActivity, CardDetailsActivity::class.java)
        intent.putExtra(Constants.BOARD_DETAIL, boardDetails)
        intent.putExtra(Constants.TASK_LIST_ITEM_POSITION, taskListPosition)
        intent.putExtra(Constants.CARD_LIST_ITEM_POSITION, cardPosition)
        intent.putExtra(Constants.BOARD_MEMBERS_LIST, assignedMembersDetailList)
        startActivityForResult(intent, CARD_DETAILS_REQUEST_CODE)
    }

    fun updateCardsInTaskList(taskListPosition: Int, cards: ArrayList<Card>) {

        // Remove the last item
        boardDetails.taskList.removeAt(boardDetails.taskList.size - 1)

        boardDetails.taskList[taskListPosition].cards = cards

        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this@TaskListActivity, boardDetails)
    }
}