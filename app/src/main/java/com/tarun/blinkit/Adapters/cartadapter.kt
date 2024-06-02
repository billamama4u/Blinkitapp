package com.tarun.blinkit.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tarun.blinkit.databinding.ItemviewcartproductBinding
import com.tarun.blinkit.roomdatabase.CartProducts

class cartadapter: RecyclerView.Adapter<cartadapter.itemviewholder>() {

    inner class itemviewholder(val binding:ItemviewcartproductBinding) : RecyclerView.ViewHolder(binding.root)

    val diffUtil=object : DiffUtil.ItemCallback<CartProducts>(){
        override fun areItemsTheSame(oldItem: CartProducts, newItem: CartProducts): Boolean {
            return oldItem.id==newItem.id
        }

        override fun areContentsTheSame(oldItem: CartProducts, newItem: CartProducts): Boolean {
            return oldItem==newItem
        }
    }

    val differasync=AsyncListDiffer(this,diffUtil)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): itemviewholder {
            return itemviewholder(ItemviewcartproductBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return differasync.currentList.size
    }

    override fun onBindViewHolder(holder: itemviewholder, position: Int) {
        val product=differasync.currentList[position]

        holder.binding.apply {
            Glide.with(holder.itemView).load(product.productImage).into(ivproductcart)
            tvtitlecart.text=product.productTitle.toString()
            tvpricecart.text=product.productPrice.toString()
            tvquantitycart.text=product.productQuantity
            tvamount.text=product.productCount.toString()
        }
    }


}