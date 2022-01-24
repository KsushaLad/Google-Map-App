package com.example.map.googlemap.extensions

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

inline fun <reified T> String.fromJson() = Gson().fromJson<T>(this, object : TypeToken<T>() {}.type)