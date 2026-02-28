package com.example.onemarket.presentation.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.onemarket.Product
import com.example.onemarket.R
import com.example.onemarket.databinding.FragmentHomeBinding



class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: ProductAdapter



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter= ProductAdapter()
        binding.productRevcyclerView.adapter=adapter
        binding.productRevcyclerView.layoutManager= LinearLayoutManager(requireContext())
        //    ------------------------------------------------------------

        val testProducts = listOf(
            Product(
                id = 1,
                title = "Laptop Acer Swift 3",
                price = 1200.0,
                image = ""
            ),
            Product(
                id = 2,
                title = "Wireless Mouse Logitech",
                price = 35.0,
                image = ""
            ),
            Product(
                id = 3,
                title = "Mechanical Keyboard",
                price = 60.0,
                image = ""
            ),
            Product(
                id = 4,
                title = "27\" Monitor Samsung",
                price = 250.0,
                image = ""
            ),
            Product(
                id = 5,
                title = "USB-C Hub 7-in-1",
                price = 45.0,
                image = ""
            )
        )
        adapter.submitList(testProducts)
//-----------------------------------------------

    }

}