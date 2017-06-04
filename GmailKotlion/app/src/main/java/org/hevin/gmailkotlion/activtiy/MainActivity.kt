package org.hevin.gmailkotlion.activtiy

import android.content.res.TypedArray
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.view.ActionMode
import android.support.v7.widget.*
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import org.hevin.gmailkotlion.R
import org.hevin.gmailkotlion.adapter.MessageAdapter
import org.hevin.gmailkotlion.helper.DividerItemDecoration
import org.hevin.gmailkotlion.model.Message
import org.hevin.gmailkotlion.network.ApiClient
import org.hevin.gmailkotlion.network.ApiInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener,
        MessageAdapter.MessageAdapterListener {

    private val swipeRefreshLayout: SwipeRefreshLayout? = null

    private val messages: ArrayList<Message> = ArrayList()
    private val mAdapter: MessageAdapter = MessageAdapter(this, messages, this)

    private var actionMode: ActionMode? = null
    private val actionModeCallback: ActionModeCallback = ActionModeCallback()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar) as Toolbar)


        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }

        val swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout) as SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(this)
        swipeRefreshLayout.post {
            Runnable {
                getInbox()
            }
        }

        val recyclerView = findViewById(R.id.recyclerview_main_messages) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
        recyclerView.adapter = mAdapter
    }

    private fun getInbox() {
        swipeRefreshLayout?.isRefreshing = true

        val apiService = ApiClient.instance.create(ApiInterface::class.java)

        val call: Call<List<Message>> = apiService.getInbox()
        call.enqueue(object : Callback<List<Message>> {
            override fun onResponse(call: Call<List<Message>>?, response: Response<List<Message>>?) {
                messages.clear()
                for (msg in response!!.body()) {
                    msg.color = getRandomMaterialColor("400")
                    messages.add(msg)
                }
                mAdapter.notifyDataSetChanged()
                swipeRefreshLayout?.isRefreshing = false
            }

            override fun onFailure(call: Call<List<Message>>?, t: Throwable?) {
                Toast.makeText(applicationContext, "Unable to fetch json", Toast.LENGTH_LONG).show()
                swipeRefreshLayout?.isRefreshing = false
            }
        })
    }

    private fun getRandomMaterialColor(typeColor: String): Int {
        var returnColor = Color.GRAY
        val arrayId = resources.getIdentifier("mdcolor_" + typeColor, "array", packageName)

        if (arrayId == 0) {
            val colors: TypedArray = resources.obtainTypedArray(arrayId)
            val index: Int = (Math.random() * colors.length()).toInt()
            returnColor = colors.getColor(index, Color.GRAY)
            colors.recycle()
        }
        return returnColor
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_search) {
            Toast.makeText(applicationContext, "Search...", Toast.LENGTH_SHORT).show()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRefresh() {
        getInbox()
    }

    override fun onIconClicked(position: Int) {
        if (actionMode == null) {
            actionMode = startSupportActionMode(actionModeCallback)
        }
    }

    override fun onIconImportantClicked(position: Int) {
        val msg = messages[position]
        msg.isImport = !msg.isImport
        messages[position] = msg
        mAdapter.notifyItemChanged(position)
    }

    override fun onMessageRowClicked(position: Int) {
        if (mAdapter.selectedItems.size() > 0) {
            enableActionMode(position)
        } else {
            val msg = messages[position]
            msg.isRead = true
            messages[position] = msg
            mAdapter.notifyItemChanged(position)

            Toast.makeText(applicationContext, "Read: " + msg.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRowLongClicked(position: Int) {
        enableActionMode(position)
    }

    private fun enableActionMode(position: Int) {
        if (actionMode == null) {
            actionMode = startSupportActionMode(actionModeCallback)
        }
        toggleSelection(position)
    }

    private fun toggleSelection(position: Int) {
        mAdapter.toggleSelection(position)
        val count = mAdapter.selectedItems.size()
        if (count == 0) {
            actionMode?.finish()
        } else {
            actionMode?.title = count.toString()
            actionMode?.invalidate()
        }
    }

    inner class ActionModeCallback : ActionMode.Callback {
        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            when (item.itemId) {
                R.id.action_delete -> {
                    deleteMessages()
                    mode.finish()
                    return true
                }
                else -> return false
            }
        }

        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            mode.menuInflater.inflate(R.menu.menu_action_mode, menu)
            swipeRefreshLayout?.isEnabled = false
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return false
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            mAdapter.clearSelections()
        }

        private fun deleteMessages() {
            mAdapter.resetAnimationIndex()
            val selectionItemPositions: List<Int> = mAdapter.getSelectedItems()
            for (i in selectionItemPositions.size - 1 downTo 0) {
                mAdapter.removeData(i)
            }
            mAdapter.notifyDataSetChanged()
        }
    }
}
