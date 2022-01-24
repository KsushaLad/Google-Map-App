package com.example.map.googlemap.base.navigator

interface BaseNavigator {

    fun showLoadingPopup() //показ загрузки всплывающего окна

    fun hideLoadingPopup() //скрытие загрузки всплывающего окна

    fun networkError(errorCode: String = "") //сетевая ошибка

    fun showToast(resId: Int, error: Boolean = false) //показ Toast

    fun showToast(msg: String, error: Boolean = false) //показ Toast

    fun errorHandling(errorCode: String = "") //обработка ошибок

    fun hideKeyboard() //закрытие клавиатуры

    fun showKeyboard() //показ клавиатуры
}