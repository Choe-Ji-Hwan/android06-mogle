package com.wakeup.presentation.extension

import android.animation.Animator.AnimatorListener
import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController

fun <T> NavController.setNavigationResultToBackStack(key: String, value: T) {
    this.previousBackStackEntry?.savedStateHandle?.set(key, value)
}

fun <T> NavController.getNavigationResultFromTop(key: String): MutableLiveData<T>? =
    this.currentBackStackEntry?.savedStateHandle?.getLiveData(key)

val Int.dp
    get() = Resources.getSystem().displayMetrics?.let { dm ->
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), dm)
    } ?: 0f

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Context.hideKeyboard(view: View) {
    val imm = getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun View.getFadeOutAnimator(animDuration: Long): ObjectAnimator =
    ObjectAnimator.ofFloat(this, "alpha", 1.0f, 0.0f).apply {
        duration = animDuration
    }

fun View.getFadeInAnimator(animDuration: Long): ObjectAnimator =
    ObjectAnimator.ofFloat(this, "alpha", 0.0f, 1.0f).apply {
        duration = animDuration
    }

fun ObjectAnimator.setListener(listener: AnimatorListener): ObjectAnimator {
    this.addListener(listener)
    return this
}

fun getBitMapFromVectorDrawable(context: Context, drawableId: Int): Bitmap {
    var drawable = ContextCompat.getDrawable(context, drawableId)

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
        drawable = DrawableCompat.wrap(drawable!!).mutate()
    }

    val bitmap: Bitmap = Bitmap.createBitmap(drawable!!.intrinsicWidth,
        drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)

    val canvas = Canvas(bitmap)

    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight())
    drawable.draw(canvas)
    return bitmap
}