package com.example.smiledphoto

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

fun AppCompatActivity.showDialog(dialog: DialogFragment, tag: String = "") =
    supportFragmentManager.beginTransaction().apply {
        add(dialog, tag)
        commitAllowingStateLoss()
    }

fun <T: Fragment> T.putExtras(block: Bundle.() -> Unit): T {
    arguments = Bundle().apply { block(this) }
    return this
}
