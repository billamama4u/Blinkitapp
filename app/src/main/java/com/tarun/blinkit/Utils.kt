package com.tarun.blinkit

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.tarun.blinkit.databinding.ProgressBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object Utils {

    private var dialogue: AlertDialog?= null

    fun showDialogue(context: Context,message: String){
        var progress= ProgressBinding.inflate(LayoutInflater.from(context))
        progress.loading.text=message
        dialogue= AlertDialog.Builder(context).setView(progress.root).setCancelable(false).create()
        dialogue!!.show()
    }

    fun hideDialogue(){
        dialogue?.dismiss()
    }

    private var auth:FirebaseAuth? =null
    fun getauthinstance(): FirebaseAuth{
        if(auth==null){
            auth= FirebaseAuth.getInstance()
        }
        return auth!!
    }

    fun showToast(context:Context,message:String){
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show()
    }

    fun getuid(): String? {
        return FirebaseAuth.getInstance().currentUser?.uid
    }

    fun getRandomId():String{
        return (1..16).map { ('A'..'Z') + ('a'..'z') + ('0'..'9') }.flatten().random().toString()

    }

    fun getCurrentDate(): String {
        val date=LocalDate.now()
        val formatter=DateTimeFormatter.ofPattern("dd-MM-yyyy")
        return date.format(formatter)
    }
}