package com.drugstore.app.core.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import com.drugstore.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kasprov.android.core.fragment.navigateUp

class PickItemDialogFragment : DialogFragment() {

    private val args: PickItemDialogFragmentArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val title = if (args.titleResId != 0) getString(args.titleResId) else args.title
        val checkedPosition = args.ids.indexOf(args.checkedItemId)

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setSingleChoiceItems(args.labels, checkedPosition) { _, position -> onItemPicked(args.ids[position]) }
            .setPositiveButton(android.R.string.ok) { _, _ -> onItemPicked(args.checkedItemId) }
            .setNegativeButton(R.string.all_cancel, null)
            .create()
    }

    internal fun onItemPicked(id: Int) {
        setFragmentResult(args.requestKey, bundleOf(KEY_PICKED_ITEM_ID to id))
        navigateUp()
    }

    companion object {
        private const val KEY_PICKED_ITEM_ID = "KEY_PICKED_ITEM_ID"

        fun unpackPickedItemId(result: Bundle) = result.getInt(KEY_PICKED_ITEM_ID)
    }
}