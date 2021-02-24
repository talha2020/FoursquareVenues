package com.example.foursquarevenues

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

fun View.show(){
    this.visibility = View.VISIBLE
}

fun View.setInvisible(){
    this.visibility = View.INVISIBLE
}

fun View.setGone() {
    this.visibility = View.GONE
}

fun Activity.showError(errorMessage: String) {
    showError(this, errorMessage)
}

fun Fragment.showError(errorMessage: String){
    context?.let { showError(it,errorMessage) }
}

private fun showError(context: Context, errorMessage: String) {
    with(context as? AppCompatActivity) {
        val viewGroup =
            (this?.findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0) as ViewGroup
        Snackbar.make(viewGroup, errorMessage, Snackbar.LENGTH_LONG).show()
    }
}