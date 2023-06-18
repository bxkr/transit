package org.bxkr.transit.fragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.Calendar

class DatePickerFragment(val fn: (year: Int, month: Int, day: Int) -> Unit) : DialogFragment(), DatePickerDialog.OnDateSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(requireContext(), this, year, month, day)

        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000

        return datePickerDialog

    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        fn(year, month, day)
    }
}