package com.hmproductions.relaysync.data

import androidx.lifecycle.ViewModel

class RelayViewModel : ViewModel() {

    var busList = mutableListOf<Bus>()

    fun insertBus(loadCurrent: Int = -1, minFaultCurrent: Int = -1, maxFaultCurrent: Int = -1) {
        busList.add(Bus(busList.size + 1, loadCurrent, minFaultCurrent, maxFaultCurrent))
    }

    fun moveUp(position: Int): Boolean {
        if (position == 0) return false
        swapPositions(position - 1)
        return true
    }

    fun moveDown(position: Int): Boolean {
        if (position >= busList.size - 1) return false
        swapPositions(position)
        return true
    }

    fun deleteBus(position: Int) {
        busList.removeAt(position)
    }

    private fun swapPositions(position: Int) {
        val tempRelay = busList[position]
        busList[position] = busList[position + 1]
        busList[position + 1] = tempRelay

        busList[position].position = position + 1
        busList[position + 1].position = position + 2
    }
}