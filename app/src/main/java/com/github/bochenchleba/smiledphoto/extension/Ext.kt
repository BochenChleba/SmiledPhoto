package com.github.bochenchleba.smiledphoto.extension

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import kotlinx.android.synthetic.main.dialog_gallery.*
import java.util.concurrent.atomic.AtomicInteger

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

fun SeekBar.setProgressChangedListener(block: (Int) -> Unit) {
    setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            block(progress)
        }
        override fun onStartTrackingTouch(seekBar: SeekBar?) {}
        override fun onStopTrackingTouch(seekBar: SeekBar?) {}
    })
}

fun DialogFragment.setMatchParentWidth() {
    val orientation = resources.configuration.orientation
    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    } else {
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}

fun MutableLiveData<AtomicInteger>.postIncrement() {
    value?.incrementAndGet()
    postValue(value)
}

fun Animation.setListeners(
    onStart: (Animation) -> Unit = {},
    onRepeat: (Animation) -> Unit = {},
    onEnd: (Animation) -> Unit = {}
) {
    val listener = object : Animation.AnimationListener {
        override fun onAnimationEnd(animation: Animation) {
            onEnd(animation)
        }
        override fun onAnimationRepeat(animation: Animation) {
            onRepeat(animation)
        }
        override fun onAnimationStart(animation: Animation) {
            onStart(animation)
        }
    }
    setAnimationListener(listener)
}

