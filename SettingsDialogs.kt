package com.mayad.instagram.android.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mayad.instagram.R
import com.mayad.instagram.android.base.IBaseDialog
import com.mayad.instagram.databinding.DialogLanguageBinding
import com.mayad.instagram.databinding.DialogLogoutBinding
import com.mayad.instagram.databinding.DialogPrivacyPolicyBinding

object SettingsDialogs : IBaseDialog {
    override var dialog: Dialog? = null

    override fun dismiss() {
        dialog?.dismiss()
    }

    fun showPrivacyPolicyDialog(context: Context) {
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(context)
        dismiss()
        DialogPrivacyPolicyBinding.inflate(LayoutInflater.from(context)).apply {
            (root as ViewGroup).removeView(root)
            dialogBuilder.setView(root)
        }

        dialogBuilder.create().apply {
            setCancelable(true)
            window?.apply {
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                attributes.windowAnimations = R.style.DialogAnimation
            }
            show()
        }.also {
            dialog = it
        }
    }

    fun showLogoutDialog(context: Context, onClick: () -> Unit) {
        dismiss()
        val dialog = BottomSheetDialog(context)
        val binding =
            DialogLogoutBinding.inflate(LayoutInflater.from(context), null, false)

        dialog.setContentView(binding.root)

        binding.btnLogout.setOnClickListener {
            onClick.invoke()
            dialog.hide()
            dismiss()
        }
        binding.btnCancel.setOnClickListener { dismiss() }

        val behavior = BottomSheetBehavior.from(binding.root.parent as View)
        behavior.state = BottomSheetBehavior.STATE_COLLAPSED

        dialog.show()
        this.dialog = dialog
    }

    fun showLanguageDialog(context: Context, onClick: (String) -> Unit) {
        dismiss()
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(context)
        DialogLanguageBinding.inflate(LayoutInflater.from(context)).apply {
            (root as ViewGroup).removeView(root)
            dialogBuilder.setView(root)
            btnSave.setOnClickListener {
                onClick.invoke(if (rbEnglish.isChecked) "en" else "ar")
                dialog?.hide()
                dismiss()
            }
            if (language == "en") rbEnglish.isChecked = true else rbArabic.isChecked = true
        }

        dialogBuilder.create().apply {
            window?.apply {
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                attributes.windowAnimations = R.style.DialogAnimation
            }
            show()
        }.also {
            dialog = it
        }
    }

    private var language = "en"

    fun setCurrentLanguage(language: String) {
        language.also { this.language = it }
    }

    fun getLanguage() = language
}