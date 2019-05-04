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
import com.hmproductions.relaysync.data.Bus
import com.hmproductions.relaysync.data.RelayViewModel
import com.hmproductions.relaysync.utils.RelayItemTouchHelper
import com.hmproductions.relaysync.utils.RelayRecyclerAdapter
import com.hmproductions.relaysync.utils.computeRelayParameters
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

        populateSampleData()
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
            if (model.busList.size == 0) emptyListLayout.visibility = View.GONE
            model.busList = relayAdapter?.updatedList as MutableList<Bus>
            model.insertBus()
            relayAdapter?.insertAtLast(model.busList)
        }
    }

    override fun onUpButtonClick(position: Int) {
        model.busList = relayAdapter?.updatedList as MutableList<Bus>
        val moved = model.moveUp(position)
        if (moved) relayAdapter?.itemsChanged(model.busList, position - 1, 2)
    }

    override fun onDownButtonClick(position: Int) {
        model.busList = relayAdapter?.updatedList as MutableList<Bus>
        val moved = model.moveDown(position)
        if (moved) relayAdapter?.itemsChanged(model.busList, position, 2)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int, position: Int) {
        model.deleteBus(position)
        if (model.busList.size == 0) emptyListLayout.visibility = View.VISIBLE
        relayAdapter?.deleteRelay(model.busList, position)
    }

    override fun onRefresh() {
        toast("Relays updated")
        relaySwipeRefreshLayout.isRefreshing = false
    }

    private fun populateSampleData() {
        emptyListLayout.visibility = View.GONE
        model.insertBus(115, 1500, 6000)
        relayAdapter?.insertAtLast(model.busList)
        model.insertBus(80, 1000, 5000)
        relayAdapter?.insertAtLast(model.busList)
        model.insertBus(100, 780, 3000)
        relayAdapter?.insertAtLast(model.busList)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item?.itemId) {
            R.id.calculate_action -> {
                model.busList = relayAdapter?.updatedList as MutableList<Bus>
                val relays = computeRelayParameters(model.busList)
            }
        }

        return super.onOptionsItemSelected(item)
    }
}
