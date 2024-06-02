package com.tarun.blinkit.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.tarun.blinkit.Models.ordereditems
import com.tarun.blinkit.R
import com.tarun.blinkit.databinding.OrdereditemsBinding
import kotlin.reflect.KFunction1

class OrderAdapter(val context: Context, val statusbuttonclicked: KFunction1<ordereditems, Unit>):RecyclerView.Adapter<OrderAdapter.orderviewholder>() {

    inner class orderviewholder(val binding: OrdereditemsBinding) : RecyclerView.ViewHolder(binding.root)

    val diffUtil=object : DiffUtil.ItemCallback<ordereditems>(){
        override fun areItemsTheSame(oldItem: ordereditems, newItem: ordereditems): Boolean {
            return oldItem.orderId==newItem.orderId
        }

        override fun areContentsTheSame(oldItem: ordereditems, newItem: ordereditems): Boolean {
            return oldItem == newItem
        }
    }
    val differasync= AsyncListDiffer(this,diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): orderviewholder {
        return orderviewholder(OrdereditemsBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return differasync.currentList.size
    }

    override fun onBindViewHolder(holder: orderviewholder, position: Int) {
        val order=differasync.currentList[position]
        holder.binding.apply {
            tvorderdate.text=order.itemDate
            tvorderitems.text=order.itemTitle
            price.text="₹"+order.itemPrice.toString()

            when(order.itemStatus){
                0 ->{
                    status.text="Ordered"
                    status.backgroundTintList=ContextCompat.getColorStateList(context, R.color.yellow)
                }
                1 ->{
                    status.text="Recieved"
                    status.backgroundTintList=ContextCompat.getColorStateList(context, R.color.greyblue)
                }
                2 ->{
                    status.text="Dispatched"
                    status.backgroundTintList=ContextCompat.getColorStateList(context, R.color.green)
                }
                3 ->{
                    status.text="Delivered"
                    status.backgroundTintList=ContextCompat.getColorStateList(context, R.color.orange)
                }
            }



            holder.itemView.setOnClickListener{
                statusbuttonclicked(order)
            }
        }
    }
}