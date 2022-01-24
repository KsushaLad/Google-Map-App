package com.example.map.googlemap.extensions

import com.google.gson.Gson

fun Any.toJsonString(): String = Gson().toJson(this)