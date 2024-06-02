package com.tarun.blinkit

import com.tarun.blinkit.Adapters.AdapterProduct
import com.tarun.blinkit.Models.Product
import java.util.Locale


class FilteringProducts(val adapter: AdapterProduct, val filterlist: ArrayList<Product>): android.widget.Filter(){
    override fun performFiltering(constraint: CharSequence?): FilterResults {
        val result= FilterResults()
        if(!constraint.isNullOrEmpty()) {
            val filteredlist = ArrayList<Product>()
            val query = constraint.toString().trim().uppercase(Locale.getDefault()).split(" ")
            for (products in filterlist) {
                if (query.any {
                        products.productCategory?.uppercase(Locale.getDefault())?.contains(it) == true ||
                        products.productTitle?.uppercase(Locale.getDefault())?.contains(it) == true ||
                        products.productType?.uppercase(Locale.getDefault())?.contains(it) == true ||
                        products.productPrice.toString()?.uppercase(Locale.getDefault())?.contains(it) == true

                    }) {
                    filteredlist.add(products)
                }

            }
            result.values=filteredlist
            result.count=filteredlist.size
        }

        else{
            result.values=filterlist
            result.count=filterlist.size
        }
        return result
    }

    override fun publishResults(constraint: CharSequence?, results: FilterResults?) {

        adapter.differ.submitList(results?.values as ArrayList<Product>)
        adapter.notifyDataSetChanged()

    }
}