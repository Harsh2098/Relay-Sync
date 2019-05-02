package com.hmproductions.relaysync

import androidx.lifecycle.ViewModel

class RelayViewModel : ViewModel() {

    var relaysList = mutableListOf<Relay>()
    var size = 0

    fun insertRelay() {
        relaysList.add(Relay(++size, -1, -1, -1))
    }

    fun moveUp(position: Int): Boolean {
        if (position == 0) return false
        swapPositions(position - 1)
        return true
    }

    fun moveDown(position: Int): Boolean {
        if (position >= size - 1) return false
        swapPositions(position)
        return true
    }

    private fun swapPositions(position: Int) {
        val tempRelay = relaysList[position]
        relaysList[position] = relaysList[position + 1]
        relaysList[position + 1] = tempRelay

        relaysList[position].position = position + 1
        relaysList[position + 1].position = position + 2
    }
}