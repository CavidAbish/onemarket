package com.example.onemarket.presentation.login

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.onemarket.databinding.ItemCountryBinding

class CountryAdapter(
    private val onClick: (Country) -> Unit
) : ListAdapter<Country, CountryAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val binding: ItemCountryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(country: Country) {
            binding.tvFlag.text = country.flag
            binding.tvCountryName.text = country.name
            binding.tvCountryCode.text = country.code
            binding.root.setOnClickListener { onClick(country) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCountryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<Country>() {
        override fun areItemsTheSame(a: Country, b: Country) = a.code == b.code && a.name == b.name
        override fun areContentsTheSame(a: Country, b: Country) = a == b
    }
}