package com.drugstore.app.core.dialog

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import com.drugstore.R
import com.kasprov.android.core.fragment.navigateUp
import org.threeten.bp.LocalDate

class PickDateDialogFragment : DialogFragment() {

    private val args: PickDateDialogFragmentArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return DatePickerDialog(
            ContextThemeWrapper(requireContext(), R.style.ThemeOverlay_Dialog_DatePicker),
            { _, year, month, dayOfMonth -> onDatePicked(LocalDate.of(year, month + 1, dayOfMonth)) },
            args.initialDate.year, args.initialDate.monthValue - 1, args.initialDate.dayOfMonth
        )
    }

    internal fun onDatePicked(date: LocalDate) {
        setFragmentResult(args.requestKey, bundleOf(KEY_PICKED_DATE to date))
        navigateUp()
    }

    companion object {
        private const val KEY_PICKED_DATE = "KEY_PICKED_DATE"

        fun unpackPickedDate(result: Bundle) = result.getSerializable(KEY_PICKED_DATE) as? LocalDate
    }
}