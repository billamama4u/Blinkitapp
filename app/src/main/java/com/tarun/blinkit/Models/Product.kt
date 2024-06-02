package com.tarun.blinkit.Models

data class Product(
    var id:String?= null,
    var productTitle:String?="",
    var productCategory:String?="",
    var productType:String?="",
    var productQuantity:Int=0,
    var productUnit:String?="",
    var productPrice:Int=0,
    var productStock:Int?=0,
    var itemCount:Int? = 0,
    var productimages: ArrayList<String?>?=null,
    var adminUid:String?=null
)
