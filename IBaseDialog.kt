package com.mayad.instagram.android.base

import android.app.Dialog

interface DialogButtons

interface IBaseDialog {
    var dialog: Dialog?

    fun dismiss()
}