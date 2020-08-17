package com.kou.seekmake.screens.common

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.kou.seekmake.R
import kotlinx.android.synthetic.main.dialog_password.*

class PasswordDialog : DialogFragment() {
    private lateinit var mListener: Listener

    interface Listener {
        fun onPasswordConfirm(password: String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = context as Listener

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = requireActivity().layoutInflater.inflate(R.layout.dialog_password, null)
        return AlertDialog.Builder(requireContext())
                .setView(view)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    mListener.onPasswordConfirm(password_register.text.toString())
                }
                .setNegativeButton(android.R.string.cancel) { _, _ ->
                    // do nothing
                }
                .setTitle(R.string.please_enter_password)
                .create()
    }
}