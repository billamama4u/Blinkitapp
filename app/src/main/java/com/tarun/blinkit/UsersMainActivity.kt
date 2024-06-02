package com.tarun.blinkit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.tarun.blinkit.Adapters.cartadapter
import com.tarun.blinkit.databinding.ActivityUsersMainBinding
import com.tarun.blinkit.databinding.BottomsheetcartBinding
import com.tarun.blinkit.roomdatabase.CartProducts
import com.tarun.blinkit.view_models.Productviewmodel
import kotlinx.coroutines.launch

class UsersMainActivity : AppCompatActivity(), CartListner {
    private lateinit var binding: ActivityUsersMainBinding
    private lateinit var list: List<CartProducts>
    private lateinit var adapter: cartadapter
    private val viewmodel: Productviewmodel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getCartItems()
        getItemCount()

        // Set the onClick listener for the cart layout
        binding.llcart.setOnClickListener {
            onCartClicked()
        }
    }

    private fun getCartItems() {
        lifecycleScope.launch {
            viewmodel.getAll().observe(this@UsersMainActivity) {
                list = it
            }
        }
    }

    private fun getItemCount() {
        viewmodel.fetchitemCount().observe(this) {
            if (it > 0) {
                binding.llcart.visibility = View.VISIBLE
                binding.noofproduct.text = it.toString()
            } else {
                binding.llcart.visibility = View.GONE
                binding.noofproduct.text = "0"
            }
        }
    }

    override fun showCart(itemCount: Int) {
        val previousCount = binding.noofproduct.text.toString().toInt()
        val updatedCount = previousCount + itemCount

        if (updatedCount > 0) {
            binding.llcart.visibility = View.VISIBLE
            binding.noofproduct.text = updatedCount.toString()
        } else {
            binding.llcart.visibility = View.GONE
            binding.noofproduct.text = "0"
        }
    }

    override fun savedata(itemcount: Int) {
        val currentCount = binding.noofproduct.text.toString().toInt()
        viewmodel.savecartitemcount(currentCount + itemcount)
    }

    override fun hidecart() {
        binding.llcart.visibility = View.GONE
        binding.noofproduct.text = "0"
    }


    private fun onCartClicked() {
        val bsbinding = BottomsheetcartBinding.inflate(LayoutInflater.from(this))
        bsbinding.llnext.setOnClickListener{
            startActivity(Intent(this,Orderplacedactivity::class.java))
        }
        adapter = cartadapter()
        val bs = BottomSheetDialog(this)
        bs.setContentView(bsbinding.root)
        bsbinding.noofproduct.text = binding.noofproduct.text
        bsbinding.rvproductitems.adapter = adapter
        adapter.differasync.submitList(list)
        bs.show()
    }
}