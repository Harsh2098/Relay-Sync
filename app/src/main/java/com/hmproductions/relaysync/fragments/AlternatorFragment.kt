package com.hmproductions.relaysync.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.hmproductions.relaysync.R
import com.hmproductions.relaysync.data.RelayViewModel
import com.hmproductions.relaysync.utils.computeEarthingResistance
import com.hmproductions.relaysync.utils.computePercentageProtection
import com.hmproductions.relaysync.utils.computeRelayType
import kotlinx.android.synthetic.main.alternator_fragment.*
import org.jetbrains.anko.toast
import java.text.DecimalFormat

class AlternatorFragment : Fragment() {

    private lateinit var model: RelayViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model = activity?.run { ViewModelProviders.of(this).get(RelayViewModel::class.java) }
            ?: throw Exception("Invalid activity")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.alternator_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(context?.resources?.getStringArray(R.array.magnitudes)) {
            k1Picker.displayedValues = this
            k2Picker.displayedValues = this
            k3Picker.displayedValues = this

            k1Picker.minValue = 0
            k2Picker.minValue = 0
            k3Picker.minValue = 0

            k1Picker.maxValue = this?.size?.dec() ?: 0
            k2Picker.maxValue = this?.size?.dec() ?: 0
            k3Picker.maxValue = this?.size?.dec() ?: 0
        }

        with(object : NumberPicker.OnValueChangeListener {
            override fun onValueChange(picker: NumberPicker?, oldVal: Int, newVal: Int) {
                relayTypeTextView.text = computeRelayType(k1Picker.value, k2Picker.value, k3Picker.value)
            }
        }) {
            k1Picker.setOnValueChangedListener(this)
            k2Picker.setOnValueChangedListener(this)
            k3Picker.setOnValueChangedListener(this)
        }

        k1Picker.value = 2
        k2Picker.value = 1
        k3Picker.value = 1
        relayTypeTextView.text = computeRelayType(k1Picker.value, k2Picker.value, k3Picker.value)
    }

    fun calculateAlternatorProtection() {
        if (powerRatingEditText.text.toString().isEmpty() || voltageEditText.text.toString().isEmpty() || currentSettingEditText.text.toString().isEmpty()) {
            context?.toast("Parameters missing")
            return
        }

        if (earthingResistanceEditText.text.toString().isEmpty() && percentageProtectionEditText.text.toString().isEmpty()) {
            context?.toast("Parameters missing")
            return
        }

        if (earthingResistanceEditText.text.toString().isEmpty()) {
            val earthingResistance = computeEarthingResistance(
                powerRatingEditText.text.toString().toDouble(),
                voltageEditText.text.toString().toDouble(),
                percentageProtectionEditText.text.toString().toInt(),
                currentSettingEditText.text.toString().toDouble() / 100
            )

            earthingResistanceEditText.setText(DecimalFormat("#.###").format(earthingResistance))
            context?.toast("Earthing resistance calculated")

        } else if (percentageProtectionEditText.text.toString().isEmpty()) {
            val percentageProtection = computePercentageProtection(
                powerRatingEditText.text.toString().toDouble(),
                voltageEditText.text.toString().toDouble(),
                earthingResistanceEditText.text.toString().toDouble(),
                currentSettingEditText.text.toString().toDouble() / 100
            )

            percentageProtectionEditText.setText(DecimalFormat("#.###").format(percentageProtection))
            context?.toast("Percentage protection calculated")
        }
    }
}