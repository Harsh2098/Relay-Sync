package com.hmproductions.relaysync

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity(), RelayRecyclerAdapter.RelayClickListener,
    SwipeRefreshLayout.OnRefreshListener {

    private lateinit var model: RelayViewModel
    private var relayAdapter: RelayRecyclerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        model = ViewModelProviders.of(this).get(RelayViewModel::class.java)
        relayAdapter = RelayRecyclerAdapter(null, this, this)

        with(relaysRecyclerView) {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = relayAdapter
            setHasFixedSize(false)
        }

        setupFab()
        relaySwipeRefreshLayout.setOnRefreshListener(this)
    }

    private fun setupFab() {
        addFab.setOnClickListener {
            if (model.size == 0) emptyListLayout.visibility = View.GONE
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

    override fun onRefresh() {
        toast("Relays updated")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item?.itemId) {
            R.id.calculate_action -> {
                // TODO: Calculate using relay list from model
            }
        }

        return super.onOptionsItemSelected(item)
    }
}
