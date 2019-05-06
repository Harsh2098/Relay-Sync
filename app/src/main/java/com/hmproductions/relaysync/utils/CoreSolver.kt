package com.hmproductions.relaysync.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.hmproductions.relaysync.data.Bus
import com.hmproductions.relaysync.data.Relay

fun computeRelayParameters(
    buses: List<Bus>,
    alpha: Double = 0.14,
    beta: Double = 0.02,
    circuitBreakerTime: Double = 0.5
): List<Relay> {
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

        relays[i].psm =
            ((globalMaxFaultCurrent / (currentTransformetRatio.toDouble() * currentSetting)).toInt() / 5 * 5).toDouble()
        relays[i].ct = currentTransformetRatio
        relays[i].tms = relays[i].top * (Math.pow(relays[i].psm, beta) - 1.toDouble()) / alpha
    }

    return relays
}

fun computeRelayType(k1: Int, k2: Int, k3: Int): String {
    if (k1 == 0)
        return "Under Current Relay"

    if (k1 == 2 && k2 == 1 && k3 == 1)
        return "Over Current Relay"

    if (k1 == 2 && k2 == 0 && k3 == 1)
        return "Impedance Relay"

    if (k1 == 1 && k2 == 2)
        return "Over Voltage Relay"

    if (k1 == 2 && k2 == 1 && k3 == 0)
        return "Power restrained"

    if (k1 == 1 && k2 == 0 && k3 == 2)
        return "Mho relay"

    return "Unclassified Relay"
}

fun computeEarthingResistance(
    powerRating: Double,
    voltage: Double,
    percentProtection: Int,
    currentSetting: Double
): Double {
    val voltagePerPhase = voltage / Math.pow(3.0, 0.5) * (1 - percentProtection.toDouble() / 100.0)
    val current = powerRating * Math.pow(10.0, 6.0) * currentSetting / (Math.pow(3.0, 0.5) * voltage)
    return voltagePerPhase / current
}

fun computePercentageProtection(
    powerRating: Double,
    voltage: Double,
    earthingResistance: Double,
    currentSetting: Double
): Int {
    val current = powerRating * Math.pow(10.0, 6.0) * currentSetting / (Math.pow(3.0, 0.5) * voltage)
    return 100 - (current * earthingResistance * 100.0 * Math.pow(3.0, 0.5) / voltage).toInt()
}

fun getNearestCTRatio(current: Double): Int {
    val currentTransformerRatios = listOf(100, 200, 400)
    for (i in 0 until currentTransformerRatios.size) {
        if (current < currentTransformerRatios[i]) {
            return if (i != 0) currentTransformerRatios[i - 1] else currentTransformerRatios[0]
        }
    }

    return currentTransformerRatios[currentTransformerRatios.size - 1]
}

fun getTotalFaultCurrentUptoBus(buses: List<Bus>, index: Int): Int {
    var answer = 0
    for (i in buses.size - 1 - index until buses.size) {
        answer += buses[i].loadCurrent
    }
    return answer
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}