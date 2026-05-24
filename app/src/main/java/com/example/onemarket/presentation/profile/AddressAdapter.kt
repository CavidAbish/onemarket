package com.example.onemarket.presentation.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.onemarket.data.local.Address
import com.example.onemarket.databinding.ItemAddressBinding

class AddressAdapter(
    private val onItemClick: (Address) -> Unit
) : ListAdapter<Address, AddressAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val binding: ItemAddressBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(address: Address) {
            binding.tvAddressName.text = address.name
            binding.tvAddressFull.text = address.fullAddress
            binding.tvDefaultBadge.visibility = if (address.isDefault) View.VISIBLE else View.GONE
            binding.root.setOnClickListener { onItemClick(address) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(ItemAddressBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))

    class DiffCallback : DiffUtil.ItemCallback<Address>() {
        override fun areItemsTheSame(oldItem: Address, newItem: Address) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Address, newItem: Address) = oldItem == newItem
    }
}
