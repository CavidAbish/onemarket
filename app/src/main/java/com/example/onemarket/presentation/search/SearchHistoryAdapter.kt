package com.example.onemarket.presentation.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.onemarket.databinding.ItemSearchHistoryBinding

class SearchHistoryAdapter(
    private val onClick: (String) -> Unit
) : ListAdapter<String, SearchHistoryAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val binding: ItemSearchHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(query: String) {
            binding.tvHistoryItem.text = query
            binding.root.setOnClickListener { onClick(query) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSearchHistoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(a: String, b: String) = a == b
        override fun areContentsTheSame(a: String, b: String) = a == b
    }
}