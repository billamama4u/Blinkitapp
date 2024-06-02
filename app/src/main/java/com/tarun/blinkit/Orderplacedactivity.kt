package com.tarun.blinkit

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope

import com.phonepe.intent.sdk.api.B2BPGRequest
import com.phonepe.intent.sdk.api.B2BPGRequestBuilder
import com.phonepe.intent.sdk.api.PhonePe
import com.phonepe.intent.sdk.api.PhonePeInitException
import com.phonepe.intent.sdk.api.models.PhonePeEnvironment
import com.tarun.blinkit.Adapters.cartadapter
import com.tarun.blinkit.Models.Users
import com.tarun.blinkit.Models.order
import com.tarun.blinkit.databinding.ActivityOrderplacedactivityBinding
import com.tarun.blinkit.databinding.AddresslayoutBinding
import com.tarun.blinkit.view_models.Productviewmodel
import kotlinx.coroutines.launch

import org.json.JSONObject
import java.lang.ClassCastException
import java.nio.charset.Charset

import java.security.MessageDigest


class Orderplacedactivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderplacedactivityBinding
    private lateinit var adapter: cartadapter
    private lateinit var b2BPGRequest:B2BPGRequest
    private var cartlistner:CartListner?=null
    private val viewmodel: Productviewmodel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityOrderplacedactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        statusbarcolour()
        getAllProducts()
        gotomain()
        onNextButtonClicked()
    }














    private fun gotomain() {
        binding.tborder.setNavigationOnClickListener {
            startActivity(Intent(this,UsersMainActivity::class.java))
            finish()
        }
    }


    private fun statusbarcolour(){
        window?.apply {
            val statusbarcolour = ContextCompat.getColor(this@Orderplacedactivity,R.color.white_yellow)
            statusBarColor= statusbarcolour
            if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
                decorView.systemUiVisibility= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }


    private fun getAllProducts() {
        lifecycleScope.launch {
            viewmodel.getAll().observe(this@Orderplacedactivity) {
                adapter=cartadapter()
                binding.rvproduct.adapter=adapter
                adapter.differasync.submitList(it)

                var totalprice=0
                for(product in it){
                    val price=product.productPrice?.substring(1)?.toInt()
                    val count=product.productCount
                    totalprice +=(price?.times(count!!)!!)
                }

                binding.SubTotal.text= "₹$totalprice"

                if(totalprice<200){
                    binding.DeliverCharge.text="₹15"
                    totalprice+= 15
                }else{
                    binding.DeliverCharge.text="FREE"
                }

                binding.GrandTotal.text="₹$totalprice"
            }
        }


    }

    private fun onNextButtonClicked() {
        binding.llpayout.setOnClickListener{
            viewmodel.userAddressUpdate().observe(this){userAddress->
                if(userAddress){
                    saveOrder()

                }
                else{
                    val address= AddresslayoutBinding.inflate(layoutInflater)

                    val alertDialog= AlertDialog.Builder(this).setView(address.root).create()

                    alertDialog.show()

                    address.Addaddress.setOnClickListener{
                        saveaddress(alertDialog,address)
                    }

                }

            }
        }
    }

    private fun saveOrder() {
        lifecycleScope.launch {
            viewmodel.getAll().observe(this@Orderplacedactivity) { cartlist ->
                if (cartlist.isNotEmpty()) {
                    viewmodel.getaddress { address ->
                        val orderinfo = Utils.getuid()?.let {
                            order(
                                orderId = Utils.getRandomId(),
                                list = cartlist,
                                date = Utils.getCurrentDate(),
                                userid = it
                            )
                        }
                        if (orderinfo != null) {
                            viewmodel.saveorder(orderinfo)
                            Log.d("OrderPlacedActivity", "Order saved: $orderinfo")
                        } else {
                            Log.e("OrderPlacedActivity", "Order info is null")
                        }
                    }

                    for (products in cartlist) {
                        val count = products.productCount
                        val stock = products.productStock - (count ?: 0)
                        if (stock != null) {
                            viewmodel.afterproductordered(stock, products)
                        }
                    }

                    with(viewmodel.sharedPreferences.edit()) {
                        putInt("itemCount", 0)
                        apply()
                    }

                    Utils.showToast(this@Orderplacedactivity, "Product ordered")
                    cartlistner?.hidecart()
                    println("working")
                    deletecartproduct()

                    // Navigate to UsersMainActivity
                    startActivity(Intent(this@Orderplacedactivity, UsersMainActivity::class.java))
                    finish()
                } else {
                    Log.e("OrderPlacedActivity", "Cart list is empty")
                }
            }
        }
    }


    private fun deletecartproduct() {
        lifecycleScope.launch {
            viewmodel.deletecartproduct()
            println("Worked")
        }
    }

    private fun saveaddress(alertDialog: AlertDialog, address: AddresslayoutBinding) {
        Utils.showDialogue(this,"Processing.....")
        val pincode=address.pincode.text.toString()
        val state=address.State.text.toString()
        val district=address.District.text.toString()
        val addressuser=address.useraddress.text.toString()
        val phno=address.Phonno.text.toString()

        val addressfinal="$pincode,$state,$district,$addressuser,$phno"

        val user= Users(uid = Utils.getuid(), userNumber = phno,userAddress = addressfinal)
        lifecycleScope.launch {
            viewmodel.adduseraddresss(user)
            viewmodel.saveUserAddress()
        }
        Utils.showToast(this,"Address saved")
        alertDialog.dismiss()
        Utils.hideDialogue()
    }









}