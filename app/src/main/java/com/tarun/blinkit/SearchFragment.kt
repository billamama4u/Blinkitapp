package com.tarun.blinkit

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.tarun.blinkit.databinding.FragmentSearchBinding
import com.tarun.blinkit.databinding.ItemviewproductBinding
import com.tarun.blinkit.roomdatabase.CartProducts
import com.tarun.blinkit.view_models.Productviewmodel
import kotlinx.coroutines.launch
import java.lang.ClassCastException

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding

    private val viewmodel: Productviewmodel by viewModels()
    private lateinit var adapter:AdapterProduct
    private var cartlistner:CartListner?=null

    private var filteringProducts: FilteringProducts? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        goingBack()

        getAllProducts()
        searchproducts()
        statusbarcolour()
        // Inflate the layout for this fragment
        return binding.root
    }

    private fun statusbarcolour() {
        activity?.window?.apply {
            val statusbarcolour = ContextCompat.getColor(requireContext(), R.color.yellow)
            statusBarColor = statusbarcolour
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }

    private fun searchproducts() {
        binding.Searchet.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                filteringProducts?.filter(query)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }


    private fun goingBack() {
        binding.bckbtn.setOnClickListener {
            findNavController().navigate(R.id.action_searchFragment_to_homepage)
        }
    }

    private fun getAllProducts() {
        binding.shimmer.visibility = View.VISIBLE
        viewmodel.getAllProducts().observe(viewLifecycleOwner) { products ->
            if (products.isEmpty()) {
                binding.rvitems.visibility = View.INVISIBLE
                binding.shimmer.visibility = View.INVISIBLE
                binding.tvitem.visibility = View.VISIBLE
            } else {
                binding.rvitems.visibility = View.VISIBLE
                binding.tvitem.visibility = View.INVISIBLE
                binding.shimmer.visibility = View.INVISIBLE
                adapter = AdapterProduct(
                    ::onAddbuttonClicked,
                    ::onIncbuttonClicked,
                    ::onDecbuttonClicked
                )
                adapter.originallist = products as ArrayList<Product>
                binding.rvitems.adapter = adapter
                adapter.differ.submitList(products)

                // Initialize FilteringProducts and set as the adapter's filter
                 filteringProducts = FilteringProducts(adapter, adapter.originallist)
                 searchproducts()
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
//                productType = product.productType

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
