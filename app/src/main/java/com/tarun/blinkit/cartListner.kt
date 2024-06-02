package com.tarun.blinkit

interface CartListner {

    fun showCart(itemcount: Int)

    fun savedata(itemcount: Int)

    fun hidecart()
}