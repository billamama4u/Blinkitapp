package com.tarun.blinkit

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tarun.blinkit.Adapters.AdapterProduct
import com.tarun.blinkit.Adapters.cartadapter
import com.tarun.blinkit.databinding.FragmentOrderBinding
import com.tarun.blinkit.databinding.FragmentOrderStatusBinding
import com.tarun.blinkit.view_models.Productviewmodel


class OrderStatusFragment : Fragment() {
   lateinit var binding: FragmentOrderStatusBinding
    private val viewmodel: Productviewmodel by viewModels()
    private lateinit var cartadapter: cartadapter
   private var id:String?=""
    private var status:Int=0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentOrderStatusBinding.inflate(layoutInflater)
        getValues()
        settingStatus()
        getProductCategory()
        onbackbuttonpresed()
        return binding.root
    }

    private fun onbackbuttonpresed() {
        binding.orderappbrr.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_orderStatusFragment_to_orderFragment)
        }
    }

    private fun getValues() {
        val bundle = arguments
        id = bundle?.getString("orderId")
        status = bundle?.getInt("Status")!!

    }

    private fun settingStatus(){
        when(status){
            0 ->{
                binding.iv1.backgroundTintList= ContextCompat.getColorStateList(requireContext(), R.color.greyblue)
            }1 ->{
                binding.iv1.backgroundTintList= ContextCompat.getColorStateList(requireContext(), R.color.greyblue)
                binding.iv2.backgroundTintList= ContextCompat.getColorStateList(requireContext(), R.color.greyblue)
                binding.vw1.backgroundTintList= ContextCompat.getColorStateList(requireContext(), R.color.greyblue)
            }2 ->{
                binding.iv1.backgroundTintList= ContextCompat.getColorStateList(requireContext(), R.color.greyblue)
                binding.iv2.backgroundTintList= ContextCompat.getColorStateList(requireContext(), R.color.greyblue)
                binding.iv3.backgroundTintList= ContextCompat.getColorStateList(requireContext(), R.color.greyblue)
                binding.vw1.backgroundTintList= ContextCompat.getColorStateList(requireContext(), R.color.greyblue)
                binding.vw2.backgroundTintList= ContextCompat.getColorStateList(requireContext(), R.color.greyblue)
            }3 ->{
            binding.iv1.backgroundTintList= ContextCompat.getColorStateList(requireContext(), R.color.greyblue)
            binding.iv2.backgroundTintList= ContextCompat.getColorStateList(requireContext(), R.color.greyblue)
            binding.iv3.backgroundTintList= ContextCompat.getColorStateList(requireContext(), R.color.greyblue)
            binding.iv5.backgroundTintList= ContextCompat.getColorStateList(requireContext(), R.color.greyblue)
            binding.vw1.backgroundTintList= ContextCompat.getColorStateList(requireContext(), R.color.greyblue)
            binding.vw2.backgroundTintList= ContextCompat.getColorStateList(requireContext(), R.color.greyblue)
            binding.vw3.backgroundTintList= ContextCompat.getColorStateList(requireContext(), R.color.greyblue)
            }
        }
    }

    private fun getProductCategory() {


        viewmodel.getOrderProduct(id!!).observe(viewLifecycleOwner) { products ->

                cartadapter= cartadapter()
                binding.rvordersall.adapter = cartadapter
                cartadapter.differasync.submitList(products)


            }
        }

    }


