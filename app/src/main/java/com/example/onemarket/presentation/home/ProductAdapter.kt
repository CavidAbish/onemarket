package com.example.onemarket.presentation.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.onemarket.Product
import com.example.onemarket.databinding.ProductItemBinding

class ProductAdapter(
    private var products: List<Product> = emptyList() // default boş list
): RecyclerView.Adapter<ProductAdapter.ProductViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductViewHolder {
       val binding= ProductItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ProductViewHolder,
        position: Int
    ) {
        val product = products[position]
        holder.binding.productPrice.text = "${product.price}₼"
        holder.binding.productInfo.text=product.title
    }

    override fun getItemCount(): Int {
        return  products.size
    }

    inner class ProductViewHolder(val binding: ProductItemBinding): RecyclerView.ViewHolder(binding.root){

    }

    fun submitList(list: List<Product>) {
        products = list
        notifyDataSetChanged()
    }
}