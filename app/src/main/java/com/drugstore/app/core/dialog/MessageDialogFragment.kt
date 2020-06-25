package com.drugstore.app.core.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MessageDialogFragment : DialogFragment() {

    private val args: MessageDialogFragmentArgs by navArgs()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        isCancelable = args.cancelable
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val message = if (args.messageResId != 0) getString(args.messageResId) else args.message

        return MaterialAlertDialogBuilder(requireContext())
            .apply { if (args.titleResId != 0) setTitle(args.titleResId) }
            .setMessage(message)
            .setPositiveButton(args.buttonTextResId) { _, _ ->  onDone() }
            .create()
    }

    internal fun onDone() {
        setFragmentResult(args.requestKey, bundleOf(KEY_IS_ACKNOWLEDGED to true))
    }

    companion object {
        private const val KEY_IS_ACKNOWLEDGED = "KEY_IS_ACKNOWLEDGED"
    }
}