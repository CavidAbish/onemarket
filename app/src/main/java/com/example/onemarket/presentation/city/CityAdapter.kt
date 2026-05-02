package com.example.onemarket.presentation.city

import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.onemarket.databinding.ItemCityBinding

class CityAdapter(
    private val selectedCity: String,
    private val onClick: (String) -> Unit
) : ListAdapter<String, CityAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val binding: ItemCityBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(city: String) {
            binding.tvCityName.text = city

            if (city == selectedCity) {
                binding.tvCityName.setTextColor(Color.parseColor("#E91E8C"))
                binding.tvCityName.setTypeface(null, Typeface.BOLD)
                binding.ivCheck.visibility = View.VISIBLE
            } else {
                binding.tvCityName.setTextColor(Color.parseColor("#1A1A1A"))
                binding.tvCityName.setTypeface(null, Typeface.NORMAL)
                binding.ivCheck.visibility = View.GONE
            }

            binding.root.setOnClickListener { onClick(city) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCityBinding.inflate(
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