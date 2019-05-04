package com.hmproductions.relaysync.data

data class Bus(var position: Int, var loadCurrent: Int, var minFaultCurrent: Int, var maxFaultCurrent: Int)

data class Relay(var tms: Double, var top: Double = 0.0, var psm: Double = 0.0, var ct: Int = -1)
