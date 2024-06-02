package com.tarun.blinkit

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.tarun.blinkit.Adapters.OrderAdapter
import com.tarun.blinkit.Models.ordereditems
import com.tarun.blinkit.databinding.FragmentOrderBinding
import com.tarun.blinkit.view_models.Productviewmodel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.StringBuilder


class OrderFragment : Fragment() {

    lateinit var binding: FragmentOrderBinding
    private val viewmodel: Productviewmodel by viewModels()
    lateinit var adapter:OrderAdapter
     var status:Int?=0



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentOrderBinding.inflate(layoutInflater)
        onBackButtonClicked()
        getOrders()

        return binding.root
    }



    private fun getOrders() {
        viewmodel.getAllOrders().observe(viewLifecycleOwner) { orders ->
            if (orders.isNotEmpty()) {
                lifecycleScope.launch {
                    val orderitemslist = ArrayList<ordereditems>()
                    for (order in orders) {
                        var totalprice = 0
                        val title = StringBuilder()
                        for (product in order.list!!) {
                            val price = product.productPrice?.substring(1)?.toInt()
                            val count = product.productCount
                            totalprice += (price?.times(count!!)!!)
                            title.append("${product.productCategory}, ")
                        }

                        // Fetch the status asynchronously
                        val status = withContext(Dispatchers.IO) {
                            viewmodel.fetchStatus(order.orderId)
                        }

                        val ordered = ordereditems(order.orderId, order.date, status, title.toString(), totalprice)
                        orderitemslist.add(ordered)
                    }
                    binding.shimmer.visibility = View.INVISIBLE
                    adapter = OrderAdapter(requireContext(), ::onStatusbuttonclicked)
                    binding.rvorders.adapter = adapter
                    adapter.differasync.submitList(orderitemslist)
                }
            }
        }
    }

    private fun onBackButtonClicked() {
        binding.orderappbrr.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_orderFragment_to_peofileFragment)
        }
    }


    private fun onStatusbuttonclicked(ordereditems: ordereditems){
        var bundle=Bundle()
        ordereditems.itemStatus.let { bundle.putInt("Status", it!!) }
        bundle.putString("orderId",ordereditems.orderId)
        findNavController().navigate(R.id.action_orderFragment_to_orderStatusFragment,bundle)
    }


}