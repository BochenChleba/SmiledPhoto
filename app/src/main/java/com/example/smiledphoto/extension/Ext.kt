package com.example.smiledphoto.extension

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment

fun AppCompatActivity.showDialog(dialog: DialogFragment, tag: String = "") =
    supportFragmentManager.beginTransaction().apply {
        add(dialog, tag)
        commitAllowingStateLoss()
    }

fun <T: Fragment> T.putExtras(block: Bundle.() -> Unit): T {
    arguments = Bundle().apply { block(this) }
    return this
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}
