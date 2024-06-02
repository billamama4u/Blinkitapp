package com.tarun.blinkit

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.tarun.blinkit.Adapters.AdapterProduct
import com.tarun.blinkit.Models.Product
import com.tarun.blinkit.databinding.FragmentCategoryBinding
import com.tarun.blinkit.databinding.ItemviewproductBinding
import com.tarun.blinkit.roomdatabase.CartProducts
import com.tarun.blinkit.view_models.Productviewmodel
import kotlinx.coroutines.launch
import java.lang.ClassCastException


class CategoryFragment : Fragment() {
    lateinit var binding:FragmentCategoryBinding
    private val viewmodel: Productviewmodel by viewModels()
    private lateinit var adapter:AdapterProduct
    private var cartlistner:CartListner?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentCategoryBinding.inflate(layoutInflater)
        getProductCategory()
        onSearchmenuclicked()
        onNavigationClicked()
        statusbarcolour()
        return binding.root


    }

    private fun statusbarcolour(){
        activity?.window?.apply {
            val statusbarcolour = ContextCompat.getColor(requireContext(),R.color.white)
            statusBarColor= statusbarcolour
            if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
                decorView.systemUiVisibility= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }

    private fun onNavigationClicked() {
        binding.appbrr.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_categoryFragment_to_homepage)
        }
    }

    private fun onSearchmenuclicked() {
        binding.appbrr.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.app_bar_search->{
                    findNavController().navigate(R.id.action_categoryFragment_to_searchFragment)
                    true
                }

                else->{
                    false
                }
            }
        }
    }


    private fun getProductCategory() {

        val bundle=arguments

        val title=bundle?.getString("category").toString()

        binding.appbrr.title=title

        binding.shimmer.visibility = View.VISIBLE
        viewmodel.getCategoryProduct(title).observe(viewLifecycleOwner) { products ->
            if (products.isEmpty()) {
                binding.rvitems.visibility = View.INVISIBLE
                binding.shimmer.visibility = View.INVISIBLE
                binding.tvitem.visibility = View.VISIBLE
            } else {
                binding.rvitems.visibility = View.VISIBLE
                binding.tvitem.visibility = View.INVISIBLE
                binding.shimmer.visibility = View.INVISIBLE
                adapter = AdapterProduct(::onAddbuttonClicked,::onIncbuttonClicked,::onDecbuttonClicked)
                binding.rvitems.adapter = adapter
                adapter.differ.submitList(products)


            }
        }

    }

    private fun onAddbuttonClicked(product: Product,itemview: ItemviewproductBinding){
        itemview.editbtn.visibility=View.GONE
        itemview.prdctcount.visibility=View.VISIBLE

        //step 1-shwing all data

        var itemcount=itemview.prdctno.text.toString().toInt()
        itemcount++
        itemview.prdctno.text=itemcount.toString()
        cartlistner!!.showCart(1)
        product.itemCount=itemcount
        //save data
        product.itemCount=itemcount
        lifecycleScope.launch {
            cartlistner!!.savedata(1)
            saveProductinRoomDB(product)
            viewmodel.updateItemCount(product,itemcount)
        }


    }


    private fun onIncbuttonClicked(product: Product,itemview: ItemviewproductBinding) {

        var inccount = itemview.prdctno.text.toString().toInt()
        inccount++
        product.itemCount = inccount
        lifecycleScope.launch {
            cartlistner!!.savedata(1)
            saveProductinRoomDB(product)
            viewmodel.updateItemCount(product, inccount)
        }
        itemview.prdctno.text = inccount.toString()
        cartlistner!!.showCart(1)


    }

    private fun onDecbuttonClicked(product: Product,itemview: ItemviewproductBinding) {
        var deccount = itemview.prdctno.text.toString().toInt()
        deccount--
        //save data
        product.itemCount=deccount
        lifecycleScope.launch {
            cartlistner!!.savedata(-1)
            saveProductinRoomDB(product)
            viewmodel.updateItemCount(product,deccount)
        }
        if (deccount > 0) {
            itemview.prdctno.text = deccount.toString()
        } else {
            lifecycleScope.launch {
                viewmodel.deleteProductfromCart(product.id.toString())
            }
            itemview.editbtn.visibility=View.VISIBLE
            itemview.prdctcount.visibility=View.GONE
            itemview.prdctno.text="0"
        }
        cartlistner!!.showCart(-1)



    }

    private fun saveProductinRoomDB(product: Product) {
            val cartProducts= product.productStock?.let {
                CartProducts(
                    id=product.id.toString(),
                    productTitle = product.productTitle,
                    productCategory = product.productCategory,
                    productCount = product.itemCount,
                    productPrice = "₹"+"${product.productPrice}",
                    productQuantity = product.productQuantity.toString()+product.productUnit.toString(),
                    productStock = it,
                    productImage = product.productimages?.get(0)!!,
                    adminUid = product.adminUid,
//                    productType = product.productType


                )
            }
        lifecycleScope.launch {
            if (cartProducts != null) {
                viewmodel.insertCartProducts(cartProducts)
            }
        }

    }




    override fun onAttach(context: Context) {
        super.onAttach(context)

        if(context is CartListner){
            cartlistner=context

        }else throw ClassCastException("Please Implement Cart Listner")

    }
}