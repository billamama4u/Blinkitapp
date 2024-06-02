package com.tarun.blinkit.roomdatabase

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface DAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCartProducts(products: CartProducts)

    @Update
    fun updateCartProducts(products: CartProducts)

    @Query("DELETE FROM cartProducts where id= :id")
    fun deleteProductfromCart(id:String)

    @Query("SELECT * FROM cartProducts")
    fun getAllCartProducts(): LiveData<List<CartProducts>>

    @Query("DELETE FROM cartProducts")
    suspend fun deleteallproducts()
}