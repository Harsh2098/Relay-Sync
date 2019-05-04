package com.hmproductions.relaysync

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.hmproductions.relaysync.data.Bus
import com.hmproductions.relaysync.data.RelayViewModel
import com.hmproductions.relaysync.adapter.BusRecyclerAdapter
import com.hmproductions.relaysync.utils.RelayItemTouchHelper
import com.hmproductions.relaysync.adapter.RelayRecyclerAdapter
import com.hmproductions.relaysync.utils.computeRelayParameters
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity(), BusRecyclerAdapter.RelayClickListener,
    SwipeRefreshLayout.OnRefreshListener, RelayItemTouchHelper.RelayItemTouchHelperListener {

    private lateinit var model: RelayViewModel
    private var busAdapter: BusRecyclerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        model = ViewModelProviders.of(this).get(RelayViewModel::class.java)
        busAdapter = BusRecyclerAdapter(null, this, this)

        setupRecyclerView()
        setupFab()
        relaySwipeRefreshLayout.setOnRefreshListener(this)

        populateSampleData()
    }

    private fun setupRecyclerView() {
        with(busesRecyclerView) {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = busAdapter
            setHasFixedSize(false)

            val itemTouchHelper = ItemTouchHelper(RelayItemTouchHelper(0, ItemTouchHelper.LEFT, this@MainActivity))
            itemTouchHelper.attachToRecyclerView(this)
        }
    }

    private fun setupFab() {
        addFab.setOnClickListener {
            if (model.busList.size == 0) emptyListLayout.visibility = View.GONE
            model.busList = busAdapter?.updatedList as MutableList<Bus>
            model.insertBus()
            busAdapter?.insertAtLast(model.busList)
        }
    }

    override fun onUpButtonClick(position: Int) {
        model.busList = busAdapter?.updatedList as MutableList<Bus>
        val moved = model.moveUp(position)
        if (moved) busAdapter?.itemsChanged(model.busList, position - 1, 2)
    }

    override fun onDownButtonClick(position: Int) {
        model.busList = busAdapter?.updatedList as MutableList<Bus>
        val moved = model.moveDown(position)
        if (moved) busAdapter?.itemsChanged(model.busList, position, 2)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int, position: Int) {
        model.deleteBus(position)
        if (model.busList.size == 0) emptyListLayout.visibility = View.VISIBLE
        busAdapter?.deleteRelay(model.busList, position)
    }

    override fun onRefresh() {
        toast("Relays updated")
        relaySwipeRefreshLayout.isRefreshing = false
    }

    private fun populateSampleData() {
        emptyListLayout.visibility = View.GONE
        model.insertBus(115, 1500, 6000)
        busAdapter?.insertAtLast(model.busList)
        model.insertBus(80, 1000, 5000)
        busAdapter?.insertAtLast(model.busList)
        model.insertBus(100, 780, 3000)
        busAdapter?.insertAtLast(model.busList)
    }

    private fun validateBuses(): Boolean {
        for(bus in model.busList) {
            if(bus.maxFaultCurrent == -1 || bus.minFaultCurrent == -1 || bus.loadCurrent == -1)
                return false
        }

        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item?.itemId) {
            R.id.calculate_action -> {
                model.busList = busAdapter?.updatedList as MutableList<Bus>

                val valid = validateBuses()
                if(!valid) {
                    toast("Invalid bus parameters")
                    return super.onOptionsItemSelected(item)
                }

                val relays = computeRelayParameters(model.busList)

                val hostRoomView = LayoutInflater.from(this).inflate(R.layout.relay_answer_layout, null)

                with(hostRoomView.findViewById(R.id.relaysRecyclerView) as RecyclerView) {
                    layoutManager = LinearLayoutManager(this@MainActivity)
                    adapter = RelayRecyclerAdapter(this@MainActivity, relays)
                    setHasFixedSize(true)
                }

                AlertDialog.Builder(this)
                    .setView(hostRoomView)
                    .setCancelable(true)
                    .show()
            }
        }

        return super.onOptionsItemSelected(item)
    }
}
