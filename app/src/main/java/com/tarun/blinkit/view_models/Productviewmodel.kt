package com.tarun.blinkit.view_models

import android.app.Application
import android.content.ComponentCallbacks
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.RemoteMessage
import com.tarun.blinkit.Constant

import com.tarun.blinkit.Models.Product
import com.tarun.blinkit.Models.Users
import com.tarun.blinkit.Models.bestseller
import com.tarun.blinkit.Models.order
import com.tarun.blinkit.Models.ordereditems
import com.tarun.blinkit.Utils
import com.tarun.blinkit.apiInterface
import com.tarun.blinkit.roomdatabase.CartProducts
import com.tarun.blinkit.roomdatabase.DAO
import com.tarun.blinkit.roomdatabase.cartDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class Productviewmodel(application: Application):AndroidViewModel(application) {

     val sharedPreferences:SharedPreferences=application.getSharedPreferences("cart_items",MODE_PRIVATE)
    private val cartdao:DAO=cartDatabase.getDatabaseInstance(application).cartproductdao()

    private val _paymentStatus= MutableStateFlow(false)
    val paymentStatus=_paymentStatus


    //room db
    suspend fun insertCartProducts(product: CartProducts){
        cartdao.insertCartProducts(product)
    }

    suspend fun updateCartProducts(product: CartProducts){
        cartdao.updateCartProducts(product)
    }

    suspend fun deleteProductfromCart(id:String){
        cartdao.deleteProductfromCart(id)
    }


    suspend fun deletecartproduct(){
        cartdao.deleteallproducts()
    }

    suspend fun getAll():LiveData<List<CartProducts>>{
        return cartdao.getAllCartProducts()
    }

//    fun saveorder(orderinfo: order){
//        val db=FirebaseDatabase.getInstance().getReference("AllUsers").child("Orders").child(orderinfo.orderId).setValue(orderinfo)
//
//    }

    fun saveorder(orderInfo: order) {
        val db = FirebaseDatabase.getInstance().getReference("AllUsers").child("Orders").child(orderInfo.orderId)
        db.setValue(orderInfo).addOnCompleteListener {
            if (it.isSuccessful) {
                // Write to notification path
                val notificationDb = FirebaseDatabase.getInstance().getReference("Notifications").push()
                val notification = mapOf(
                    "title" to "New Order",
                    "message" to "A new order has been placed.",
                    "timestamp" to System.currentTimeMillis()
                )
                notificationDb.setValue(notification)
            }
        }
    }


    fun getaddress(callback:(String?)->Unit){
        val db=FirebaseDatabase.getInstance().getReference("AllUsers").child("Users").child(Utils.getuid().toString()).child("userAddress").addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val address=snapshot.value.toString()
                    callback(address)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    fun saveUserAddressNew(address:String){
        val db=FirebaseDatabase.getInstance().getReference("AllUsers")
            .child("Users").child(Utils.getuid().toString()).child("userAddress").setValue(address)
    }



    fun afterproductordered(stock:Int,product:CartProducts){

        FirebaseDatabase.getInstance().getReference("AllAdmin").child("AllProducts").child(product.id).child("itemCount").setValue(0)

        FirebaseDatabase.getInstance().getReference("AllAdmin").child("ProductCategory/${product.productCategory}/${product.id}").child("itemCount").setValue(0)

        //FirebaseDatabase.getInstance().getReference("AllAdmin").child("ProductType/${product.productType}/${product.id}").child("itemCount").setValue(0)

        FirebaseDatabase.getInstance().getReference("AllAdmin").child("AllProducts").child(product.id).child("productStock").setValue(stock)

        FirebaseDatabase.getInstance().getReference("AllAdmin").child("ProductCategory/${product.productCategory}/${product.id}").child("productStock").setValue(stock)

        //FirebaseDatabase.getInstance().getReference("AllAdmin").child("ProductType/${product.productType}/${product.id}").child("productStock").setValue(stock)
    }


    fun getAllProducts(): LiveData<List<Product>> {
        val productsLiveData = MutableLiveData<List<Product>>()
        val db = FirebaseDatabase.getInstance().getReference("AllAdmin").child("AllProducts")

        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val products = mutableListOf<Product>()
                for (productSnapshot in snapshot.children) {
                    val prod = productSnapshot.getValue(Product::class.java)
                    prod?.let { products.add(it) }
                }
                productsLiveData.value = products
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })

        return productsLiveData
    }

    fun adduseraddresss(user:Users){
        user.uid?.let {
            FirebaseDatabase.getInstance().getReference("AllUsers").child("Users").child(
                it
            ).child("userAddress").setValue(user.userAddress)
        }
    }

    fun getCategoryProduct(category: String):LiveData<List<Product>>{
        val productsLiveData = MutableLiveData<List<Product>>()
        val db = FirebaseDatabase.getInstance()
            .getReference("AllAdmin").child("ProductCategory").child(category)

        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val products = mutableListOf<Product>()
                for (productSnapshot in snapshot.children) {
                    val prod = productSnapshot.getValue(Product::class.java)
                    prod?.let { products.add(it) }
                }
                productsLiveData.value = products
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })

        return productsLiveData
    }

    fun getOrderProduct(orderId: String): LiveData<List<CartProducts>> {
        val productsLiveData = MutableLiveData<List<CartProducts>>()
        val db = FirebaseDatabase.getInstance()
            .getReference("AllUsers").child("Orders").child(orderId).child("list")

        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val products = mutableListOf<CartProducts>()
                for (productSnapshot in snapshot.children) {
                    try {
                        val prod = productSnapshot.getValue(CartProducts::class.java)
                        prod?.let { products.add(it) }
                    } catch (e: Exception) {
                        // Log the problematic snapshot for debugging
                        Log.e("FirebaseDataError", "Error converting product snapshot: ${productSnapshot.value}", e)
                    }
                }
                productsLiveData.value = products
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
                Log.e("FirebaseError", "Error fetching data: ${error.message}")
            }
        })

        return productsLiveData
    }



    fun savecartitemcount(itemcount:Int){
        sharedPreferences.edit().putInt("itemCount",itemcount).apply()
    }

    fun fetchitemCount():MutableLiveData<Int> {
        val itemcount = MutableLiveData<Int>()
        itemcount.value=sharedPreferences.getInt("itemCount",0)
        return itemcount
    }

    fun updateItemCount(product: Product,itemcount: Int){

        FirebaseDatabase.getInstance().getReference("AllAdmin").child("AllProducts").child(product.id.toString()).child("itemCount").setValue(itemcount)

        FirebaseDatabase.getInstance().getReference("AllAdmin").child("ProductCategory/${product.productCategory}/${product.id}").child("itemCount").setValue(itemcount)

        FirebaseDatabase.getInstance().getReference("AllAdmin").child("ProductType/${product.productType}/${product.id}").child("itemCount").setValue(itemcount)


    }

    fun userAddressUpdate():MutableLiveData<Boolean>{
        val status = MutableLiveData<Boolean>()
        status.value=sharedPreferences.getBoolean("userAddress",false)
        return status
    }

    fun saveUserAddress(){
        sharedPreferences.edit().putBoolean("userAddress",true).apply()
    }

    fun getAllOrders(): LiveData<List<order>> {
        val ordersLiveData = MutableLiveData<List<order>>()
        val db = FirebaseDatabase.getInstance().getReference("AllUsers").child("Orders").orderByChild("itemStatus")

        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val ordersList = mutableListOf<order>()
                for (productSnapshot in snapshot.children) {
                    val singleOrder = productSnapshot.getValue(order::class.java)
                    if(singleOrder?.userid == Utils.getuid()) {
                        singleOrder?.let { ordersList.add(it) }
                    }
                }
                ordersLiveData.value = ordersList
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })

        return ordersLiveData
    }

    fun getAllItems(): LiveData<List<bestseller>> {
        val ordersLiveData = MutableLiveData<List<bestseller>>()
        val db = FirebaseDatabase.getInstance().getReference("AllAdmin").child("ProductType")

        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val ordersList = mutableListOf<bestseller>()
                for (productSnapshot in snapshot.children) {
                    val producttypename= productSnapshot.key
                    val productlist=ArrayList<Product>()

                    for(products in productSnapshot.children){
                        val product=products.getValue(Product::class.java)
                        productlist.add(product!!)

                    }
                    val bestseller=bestseller(producttype=producttypename, productlist = productlist)
                    ordersList.add(bestseller)

                }
                ordersLiveData.value = ordersList
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })

        return ordersLiveData
    }

    suspend fun fetchStatus(orderId: String): Int? {
        return try {
            val snapshot = FirebaseDatabase.getInstance()
                .getReference("AllUsers")
                .child("Orders")
                .child(orderId)
                .child("itemStatus")
                .get()
                .await()
            snapshot.getValue(Int::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }




}
