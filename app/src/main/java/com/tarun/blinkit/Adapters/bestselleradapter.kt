package com.tarun.blinkit.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tarun.blinkit.Models.bestseller
import com.tarun.blinkit.databinding.BestsellerBinding
import kotlin.reflect.KFunction1

class bestselleradapter(val kFunction0: KFunction1<bestseller, Unit>) : RecyclerView.Adapter<bestselleradapter.bestsellerviewholder>() {

    inner class bestsellerviewholder(var binding: BestsellerBinding) : RecyclerView.ViewHolder(binding.root)

    private val diffUtil = object : DiffUtil.ItemCallback<bestseller>() {
        override fun areItemsTheSame(oldItem: bestseller, newItem: bestseller): Boolean {
            return oldItem.sellid == newItem.sellid
        }

        override fun areContentsTheSame(oldItem: bestseller, newItem: bestseller): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): bestsellerviewholder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = BestsellerBinding.inflate(layoutInflater, parent, false)
        return bestsellerviewholder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: bestsellerviewholder, position: Int) {
        val item = differ.currentList[position]

        holder.binding.apply {
            tvtype.text=item.producttype.toString()
            productsleft.text=item.productlist?.size.toString()+" products"

            val listofiv= listOf( iv1,iv2,iv3)
            val minsize= minOf(item.productlist!!.size,listofiv.size)

            for(i in 0 until minsize ){
                listofiv[i].visibility= View.VISIBLE
                Glide.with(holder.itemView).load(item.productlist[i].productimages!!.get(0)).into(listofiv[i])
            }

            if(item.productlist.size>3){
                iv4.visibility=View.VISIBLE
                iv4.text= "+${(item.productlist.size - 3)}"
            }

            holder.itemView.setOnClickListener{
                  kFunction0(item)
            }

        }

    }




}
