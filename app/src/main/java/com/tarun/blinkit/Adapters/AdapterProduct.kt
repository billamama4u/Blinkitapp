package com.tarun.blinkit.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.models.SlideModel
import com.tarun.blinkit.FilteringProducts

import com.tarun.blinkit.Models.Product
import com.tarun.blinkit.databinding.ItemviewproductBinding

class AdapterProduct(
    val onAddbuttonCLicked: (Product, ItemviewproductBinding) -> Unit,
    val incproduct: (Product, ItemviewproductBinding) -> Unit,
    val decproduct: (Product, ItemviewproductBinding) -> Unit
) : RecyclerView.Adapter<AdapterProduct.ProductViewHolder>(),Filterable {

    inner class ProductViewHolder(var binding: ItemviewproductBinding) : RecyclerView.ViewHolder(binding.root)

    private val diffUtil = object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffUtil)
    var originallist = ArrayList<Product>()

    private var filteringProducts: FilteringProducts? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(ItemviewproductBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = differ.currentList[position]

        holder.binding.apply {
            val itemlist = ArrayList<SlideModel>()

            val list = product.productimages
            if (list != null) {
                for (i in 0 until list.size) {
                    itemlist.add(SlideModel(list[i].toString()))
                }
            }
            ivslider.setImageList(itemlist)

            tvproducttitle.text = product.productTitle.toString()
            tvprice.text = "₹${product.productPrice}"
            if(product.itemCount!! >0){
                prdctno.text=product.itemCount.toString()
                editbtn.visibility= View.GONE
                prdctcount.visibility= View.VISIBLE
            }
            tvproductquantity.text = "${product.productQuantity}${product.productUnit}"

            editbtn.setOnClickListener{
                onAddbuttonCLicked(product,this)
            }
            incno.setOnClickListener {
                incproduct(product,this)
            }
            decno.setOnClickListener {
                decproduct(product,this)
            }
        }

    }


    override fun getFilter(): Filter {
        if (filteringProducts == null) {
            filteringProducts = FilteringProducts(this, originallist)
        }
        return filteringProducts as FilteringProducts
    }
}
