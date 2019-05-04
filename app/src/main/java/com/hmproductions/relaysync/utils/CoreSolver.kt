package com.hmproductions.relaysync.utils

import com.hmproductions.relaysync.data.Bus
import com.hmproductions.relaysync.data.Relay

fun computeRelayParameters(buses: List<Bus>, alpha: Double = 0.14, beta: Double = 0.02, circuitBreakerTime: Double = 0.5): List<Relay> {
    val relays = mutableListOf<Relay>()
    val globalMaxFaultCurrent = buses[buses.size - 2].maxFaultCurrent.toDouble()
    relays.add(Relay(0.1))

    val faultCurrent = 1.25 * buses[buses.size - 1].loadCurrent
    val ctRatio = getNearestCTRatio(faultCurrent)
    val currentSetting /* pick-up current */ = faultCurrent / ctRatio.toDouble()

    relays[0].ct = ctRatio
    relays[0].psm = globalMaxFaultCurrent / (ctRatio.toDouble() * currentSetting)
    relays[0].top = (alpha * relays[0].tms) / (Math.pow(relays[0].psm, beta) - 1.toDouble())

    for (i in 1 until buses.size - 1) {
        relays.add(Relay(0.0, (relays[i - 1].top + circuitBreakerTime) * 1.1))

        val currentSummation = 1.25 * getTotalFaultCurrentUptoBus(buses, i)

        val currentTransformetRatio = getNearestCTRatio(currentSummation)
        val currentSetting /* pick-up current */ = currentSummation / currentTransformetRatio.toDouble()

        relays[i].psm = ((globalMaxFaultCurrent / (currentTransformetRatio.toDouble() * currentSetting)).toInt() / 5 * 5).toDouble()
        relays[i].ct = currentTransformetRatio
        relays[i].tms = relays[i].top * (Math.pow(relays[i].psm, beta) - 1.toDouble()) / alpha
    }

    return relays
}

fun getNearestCTRatio(current: Double): Int {
    val currentTransformerRatios = listOf(100, 200, 400)
    return currentTransformerRatios[Math.abs(currentTransformerRatios.binarySearch(current.toInt())) - 2]
}

fun getTotalFaultCurrentUptoBus(buses: List<Bus>, index: Int): Int {
    var answer = 0
    for (i in buses.size - 1 - index until buses.size) {
        answer += buses[i].loadCurrent
    }
    return answer
}