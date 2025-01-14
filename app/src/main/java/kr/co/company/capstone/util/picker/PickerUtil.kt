package kr.co.company.capstone.util.picker

import android.os.Build
import android.widget.NumberPicker
import androidx.annotation.RequiresApi

object PickerUtil {
    @RequiresApi(Build.VERSION_CODES.Q)
    fun setupNumberPicker(picker: NumberPicker, values: Array<String>) {
        picker.apply {
            minValue = 0
            maxValue = values.size - 1
            displayedValues = values
            selectionDividerHeight = 0
            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        }
    }
}