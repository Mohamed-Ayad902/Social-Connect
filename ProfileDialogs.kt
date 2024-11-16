package com.mayad.instagram.android.dialog

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.mayad.instagram.R
import com.mayad.instagram.android.base.DialogButtons
import com.mayad.instagram.android.base.IBaseDialog
import com.mayad.instagram.android.extension.loge
import com.mayad.instagram.databinding.DialogCurrentUserBinding
import com.mayad.instagram.databinding.DialogOtherUserBinding
import com.mayad.instagram.databinding.DialogQrCodeBinding


object ProfileDialogs : IBaseDialog {
    override var dialog: Dialog? = null

    override fun dismiss() {
        dialog?.dismiss()
        dialog = null
    }

    fun showCurrentUserDialog(context: Context, onClick: (DialogButtons) -> Unit) {
        dismiss()
        val dialog = BottomSheetDialog(context)
        val binding =
            DialogCurrentUserBinding.inflate(LayoutInflater.from(context), null, false)

        dialog.setContentView(binding.root)

        binding.btnSettings.setOnClickListener { onClick.invoke(CurrentUserButtons.SETTINGS) }
        binding.btnRecentActivity.setOnClickListener { onClick.invoke(CurrentUserButtons.RECENT_ACTIVITY) }
        binding.btnQrCode.setOnClickListener { onClick.invoke(CurrentUserButtons.QR_CODE) }
        binding.btnSaved.setOnClickListener { onClick.invoke(CurrentUserButtons.SAVED) }

        val behavior = BottomSheetBehavior.from(binding.root.parent as View)
        behavior.state = BottomSheetBehavior.STATE_COLLAPSED

        dialog.show()
        ProfileDialogs.dialog = dialog
    }

    fun showOtherUserDialog(context: Context, onClick: (DialogButtons) -> Unit) {
        dismiss()
        val dialog = BottomSheetDialog(context)
        val binding =
            DialogOtherUserBinding.inflate(LayoutInflater.from(context), null, false)

        dialog.setContentView(binding.root)

        binding.btnBlock.setOnClickListener { onClick.invoke(OtherUserButtons.BLOCK) }
        binding.btnMutualFriends.setOnClickListener { onClick.invoke(OtherUserButtons.MUTUAL_FRIENDS) }

        val behavior = BottomSheetBehavior.from(binding.root.parent as View)
        behavior.state = BottomSheetBehavior.STATE_COLLAPSED

        dialog.show()
        ProfileDialogs.dialog = dialog
    }

    fun showQrCodeDialog(context: Context, username: String, onClick: () -> Unit) {
        dismiss()
        val dialog = BottomSheetDialog(context)
        val binding =
            DialogQrCodeBinding.inflate(LayoutInflater.from(context), null, false)

        dialog.setContentView(binding.root)
        binding.tvUsername.text = username
        try {
            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.encodeBitmap(username, BarcodeFormat.QR_CODE, 500, 500)
            binding.imageViewQrCode.setImageBitmap(bitmap)
        } catch (e: Exception) {
            "showQrCodeDialog: ${e.message}".loge("ProfileDialogs")
        }

        val behavior = BottomSheetBehavior.from(binding.root.parent as View)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED

        binding.btnScanUser.setOnClickListener { onClick.invoke(); dismiss() }
        dialog.show()
        ProfileDialogs.dialog = dialog
    }

    fun showStoragePermissionDialog(
        context: Context, onButtonClickedListener: (okClicked: Boolean) -> Unit
    ) {
        MaterialAlertDialogBuilder(context).setTitle(context.getString(R.string.permission_not_allowed))
            .setMessage(context.getString(R.string.accept_permission))
            .setCancelable(false)
            .setPositiveButton(R.string.yes) { dialog, _ ->
                dialog.dismiss()
                onButtonClickedListener(true)
            }.setNegativeButton(R.string.no) { dialog, _ ->
                dialog.dismiss()
                onButtonClickedListener(false)
            }.show()
    }
}

enum class CurrentUserButtons : DialogButtons {
    SETTINGS,
    RECENT_ACTIVITY,
    QR_CODE,
    SAVED
}

enum class OtherUserButtons : DialogButtons {
    BLOCK,
    MUTUAL_FRIENDS
}