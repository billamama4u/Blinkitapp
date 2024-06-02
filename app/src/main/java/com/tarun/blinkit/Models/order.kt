package com.tarun.blinkit.Models

import com.tarun.blinkit.roomdatabase.CartProducts

data class order(
    val orderId:String="",
    val list:List<CartProducts>? = null,
    val date:String="",
    val useraddress:String="",
    val orderstatus:Int=0,
    val userid:String=""

)
