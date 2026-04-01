package com.example.onemarket.presentation.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.onemarket.databinding.ItemBannerBinding

class BannerAdapter(
    private val banners: List<Int> // drawable resource id-ləri
) : RecyclerView.Adapter<BannerAdapter.ViewHolder>() {

    // Sonsuz loop üçün çox böyük say
    private val INFINITE_COUNT = Int.MAX_VALUE

    inner class ViewHolder(private val binding: ItemBannerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(drawableRes: Int) {
            binding.ivBanner.setImageResource(drawableRes)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBannerBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // position % banners.size ilə sonsuz loop
        holder.bind(banners[position % banners.size])
    }

    override fun getItemCount(): Int = if (banners.isEmpty()) 0 else INFINITE_COUNT

    // Başlanğıc pozisiyası — ortadan başla ki hər iki tərəfə scroll olsun
    fun getStartPosition(): Int = (INFINITE_COUNT / 2) - ((INFINITE_COUNT / 2) % banners.size)
}