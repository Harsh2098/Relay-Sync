package com.hmproductions.relaysync

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.hmproductions.relaysync.utils.RelayItemTouchHelper
import com.hmproductions.relaysync.utils.RelayRecyclerAdapter
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity(), RelayRecyclerAdapter.RelayClickListener,
    SwipeRefreshLayout.OnRefreshListener, RelayItemTouchHelper.RelayItemTouchHelperListener {

    private lateinit var model: RelayViewModel
    private var relayAdapter: RelayRecyclerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        model = ViewModelProviders.of(this).get(RelayViewModel::class.java)
        relayAdapter = RelayRecyclerAdapter(null, this, this)

        setupRecyclerView()
        setupFab()
        relaySwipeRefreshLayout.setOnRefreshListener(this)
    }

    private fun setupRecyclerView() {
        with(relaysRecyclerView) {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = relayAdapter
            setHasFixedSize(false)

            val itemTouchHelper = ItemTouchHelper(RelayItemTouchHelper(0, ItemTouchHelper.LEFT, this@MainActivity))
            itemTouchHelper.attachToRecyclerView(this)
        }
    }

    private fun setupFab() {
        addFab.setOnClickListener {
            if (model.relaysList.size == 0) emptyListLayout.visibility = View.GONE
            model.relaysList = relayAdapter?.updatedList as MutableList<Relay>
            model.insertRelay()
            relayAdapter?.insertAtLast(model.relaysList)
        }
    }

    override fun onUpButtonClick(position: Int) {
        model.relaysList = relayAdapter?.updatedList as MutableList<Relay>
        val moved = model.moveUp(position)
        if (moved) relayAdapter?.itemsChanged(model.relaysList, position - 1, 2)
    }

    override fun onDownButtonClick(position: Int) {
        model.relaysList = relayAdapter?.updatedList as MutableList<Relay>
        val moved = model.moveDown(position)
        if (moved) relayAdapter?.itemsChanged(model.relaysList, position, 2)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int, position: Int) {
        model.deleteRelay(position)
        if (model.relaysList.size == 0) emptyListLayout.visibility = View.VISIBLE
        relayAdapter?.deleteRelay(model.relaysList, position)
    }

    override fun onRefresh() {
        toast("Relays updated")
        relaySwipeRefreshLayout.isRefreshing = false
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item?.itemId) {
            R.id.calculate_action -> {
                toast("Todo implement this")
                // TODO: Calculate using relay list from model
            }
        }

        return super.onOptionsItemSelected(item)
    }
}
