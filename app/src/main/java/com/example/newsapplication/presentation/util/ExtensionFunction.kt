package com.example.newsapplication.presentation.util

import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import java.time.Duration

fun Fragment.showSnackbar(
    message: String,
    duration: Int = Snackbar.LENGTH_LONG,
    view: View = requireView()
    ){

    Snackbar.make(view, message, duration).show()
}


inline fun <T: View> T.setInvisibility(isInvisible: Boolean){
    if (isInvisible){
        this.visibility = View.INVISIBLE
    }else{
        this.visibility = View.VISIBLE
    }
}

val <T> T.exhaustive: T
    get() = this