package com.example.onemarket.presentation.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.onemarket.data.local.CardModel
import com.example.onemarket.databinding.ItemSavedCardBinding

class MyCardsAdapter(
    private val onCardClick: (CardModel) -> Unit
) : ListAdapter<CardModel, MyCardsAdapter.CardViewHolder>(DIFF_CALLBACK) {

    inner class CardViewHolder(private val binding: ItemSavedCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(card: CardModel) {
            binding.tvCardNumber.text = card.maskedNumber
            binding.tvCardExpiry.text = card.expiry
            binding.tvCardType.text = card.typeLabel
            binding.root.setOnClickListener { onCardClick(card) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val binding = ItemSavedCardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CardModel>() {
            override fun areItemsTheSame(oldItem: CardModel, newItem: CardModel) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: CardModel, newItem: CardModel) =
                oldItem == newItem
        }
    }
}
