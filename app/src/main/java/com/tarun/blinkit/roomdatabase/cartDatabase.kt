package com.tarun.blinkit.roomdatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [CartProducts::class], version = 1, exportSchema = false)
abstract class cartDatabase:RoomDatabase() {

    abstract fun cartproductdao(): DAO

    companion object{
            @Volatile
            var Instance:cartDatabase?=null

        fun getDatabaseInstance(context: Context): cartDatabase{
            val tempinstance= Instance
            if(tempinstance!= null) {
                return tempinstance
            } else{
                synchronized(this){
                    val dbroom=Room.databaseBuilder(context,cartDatabase::class.java,"cartProducts").allowMainThreadQueries().build()
                    Instance=dbroom
                    return dbroom
                }
            }
        }

    }
}