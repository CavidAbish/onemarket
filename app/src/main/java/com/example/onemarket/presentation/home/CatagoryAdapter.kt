package com.example.onemarket.presentation.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.onemarket.databinding.ItemCategoryBinding
import com.example.onemarket.domain.model.CategoryModel

class CategoryAdapter(
    private val onCategoryClick: (CategoryModel) -> Unit
) : ListAdapter<CategoryModel, CategoryAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(category: CategoryModel) {
            binding.tvCategoryName.text = category.name

            // Glide ilə şəkil yüklə
            Glide.with(binding.root)
                .load(category.imageUrl)
                .centerCrop()
                .into(binding.ivCategoryImage)

            binding.root.setOnClickListener {
                onCategoryClick(category)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<CategoryModel>() {
        override fun areItemsTheSame(oldItem: CategoryModel, newItem: CategoryModel) =
            oldItem.slug == newItem.slug

        override fun areContentsTheSame(oldItem: CategoryModel, newItem: CategoryModel) =
            oldItem == newItem
    }
}