package com.tarun.blinkit.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tarun.blinkit.Models.Category
import com.tarun.blinkit.databinding.ItemlistBinding

class CategoryAdapter(val categorylist: ArrayList<Category>, val onCategoryClicked: (Category) -> Unit):
    RecyclerView.Adapter<CategoryAdapter.categoryviewholder>() {

    inner class categoryviewholder(var binding: ItemlistBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): categoryviewholder {
        val binding= ItemlistBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return categoryviewholder(binding)
    }

    override fun getItemCount(): Int {
        return categorylist.size
    }

    override fun onBindViewHolder(holder: categoryviewholder, position: Int) {
        val category=categorylist[position]
        holder.binding.apply {
            ivcategory.setImageResource(category.image!!)
            categorytitle.text=category.title
        }

        holder.itemView.setOnClickListener {
            onCategoryClicked(category)
        }
    }
}