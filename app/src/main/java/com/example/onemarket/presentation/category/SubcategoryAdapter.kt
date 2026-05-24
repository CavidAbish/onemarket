package com.example.onemarket.presentation.category

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.onemarket.databinding.ItemSubcategoryChipBinding
import com.example.onemarket.domain.model.CategoryModel

class SubcategoryAdapter(
    private val onItemClick: (CategoryModel) -> Unit
) : RecyclerView.Adapter<SubcategoryAdapter.VH>() {

    private var items: List<CategoryModel> = emptyList()

    fun submitList(list: List<CategoryModel>) {
        items = list
        notifyDataSetChanged()
    }

    inner class VH(private val binding: ItemSubcategoryChipBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CategoryModel) {
            binding.tvSubName.text = item.name
            if (item.imageUrl.isNotEmpty()) {
                Glide.with(binding.ivSubIcon.context)
                    .load(item.imageUrl)
                    .centerCrop()
                    .into(binding.ivSubIcon)
            }
            binding.root.setOnClickListener { onItemClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(ItemSubcategoryChipBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(items[position])

    override fun getItemCount() = items.size
}
