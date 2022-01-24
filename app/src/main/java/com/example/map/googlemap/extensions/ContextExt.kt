package com.example.map.googlemap.extensions

import android.content.Context

fun Context.dip(value: Int): Int = (value * resources.displayMetrics.density).toInt()
