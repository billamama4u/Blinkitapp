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
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.tarun.blinkit.Adapters.AdapterProduct
import com.tarun.blinkit.Adapters.CategoryAdapter
import com.tarun.blinkit.Adapters.bestselleradapter
import com.tarun.blinkit.Models.Category
import com.tarun.blinkit.Models.Product
import com.tarun.blinkit.Models.bestseller
import com.tarun.blinkit.databinding.FragmentHomepageBinding
import com.tarun.blinkit.databinding.ItemviewproductBinding
import com.tarun.blinkit.databinding.ScrollmenuBinding
import com.tarun.blinkit.roomdatabase.CartProducts
import com.tarun.blinkit.view_models.Productviewmodel
import kotlinx.coroutines.launch
import java.lang.ClassCastException


class homepage : Fragment() {

    private val viewmodel: Productviewmodel by viewModels()
    lateinit var binding: FragmentHomepageBinding
    private var cartlistner:CartListner?=null
    lateinit var adapter:bestselleradapter
    lateinit var adapterxx:AdapterProduct
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentHomepageBinding.inflate(layoutInflater)
        statusbarcolour()
        setcategory()
        goingToSearch()
        onProfileClicked()
        getBestSeller()
        get()
        // Inflate the layout for this fragment
        return (binding.root)
    }

    private fun getBestSeller() {
        viewmodel.getAllItems().observe(viewLifecycleOwner){items ->
            adapter= bestselleradapter(::onSeeallclicked)
            binding.betsellersinfo.adapter=adapter
            adapter.differ.submitList(items)
        }
    }

    private fun onSeeallclicked(tem: bestseller){

        val bsbinding=ScrollmenuBinding.inflate(LayoutInflater.from(requireContext()))
        val bs=BottomSheetDialog(requireContext())
        bs.setContentView(bsbinding.root)

        adapterxx= AdapterProduct(::onAddbuttonClicked,::onIncbuttonClicked,::onDecbuttonClicked)
        bsbinding.rvitemproduct.adapter=adapterxx
        adapterxx.differ.submitList(tem.productlist)

    }

    private fun onAddbuttonClicked(product: Product, itemview: ItemviewproductBinding){
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


    private fun onIncbuttonClicked(product: Product, itemview: ItemviewproductBinding) {

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

    private fun onDecbuttonClicked(product: Product, itemview: ItemviewproductBinding) {
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


    private fun onProfileClicked() {
        binding.profile.setOnClickListener {
            findNavController().navigate(R.id.action_homepage_to_peofileFragment)
        }
    }


    private fun goingToSearch() {
        binding.searchbar.setOnClickListener {
            findNavController().navigate(R.id.action_homepage_to_searchFragment)
        }
    }



    private fun setcategory() {
        var categorylist=ArrayList<Category>()

        for (i in 0 until Constant.allproductcategory.size) {
            categorylist.add(
                Category(
                    Constant.allproductcategory[i],
                    Constant.producticon[i]
                )
            )
        }

        binding.rvcategories.adapter=CategoryAdapter(categorylist,::oncategoryClicked)
    }

    private fun oncategoryClicked(category: Category) {
        val title=category.title
        val bundle= Bundle()
        bundle.putString("category",title)
        findNavController().navigate(R.id.action_homepage_to_categoryFragment,bundle)
    }


    private fun get() {
        lifecycleScope.launch {
            viewmodel.getAll().observe(viewLifecycleOwner) {
                for (i in it) {
                    Log.d("vvv", i.productTitle.toString())
                }
            }
        }
    }



    private fun statusbarcolour(){
        activity?.window?.apply {
            val statusbarcolour = ContextCompat.getColor(requireContext(),R.color.orange)
            statusBarColor= statusbarcolour
            if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
                decorView.systemUiVisibility= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }
}