package com.example.onemarket.presentation.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.onemarket.R
import com.example.onemarket.databinding.FragmentReviewsBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class ReviewsFragment : Fragment() {

    private var _binding: FragmentReviewsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReviewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        // Tab switching
        binding.tabWriteReview.setOnClickListener {
            selectTab(writeReview = true)
        }
        binding.tabMyReviews.setOnClickListener {
            selectTab(writeReview = false)
        }

        // "Alış-verişə başla" — switch bottom nav to Catalog
        binding.btnStartShopping.setOnClickListener {
            requireActivity()
                .findViewById<BottomNavigationView>(R.id.bottom_nav)
                ?.selectedItemId = R.id.catalogFragment
        }

        // "Rəy yazmaq" from 'Mənim rəylərim' tab — switch to first tab
        binding.btnWriteReview.setOnClickListener {
            selectTab(writeReview = true)
        }

        // Default: show first tab
        selectTab(writeReview = true)
    }

    private fun selectTab(writeReview: Boolean) {
        if (writeReview) {
            // Activate "Rəy yazmaq" tab
            binding.tabWriteReview.setBackgroundResource(R.drawable.bg_chip_selected)
            binding.tabWriteReview.setTextColor(android.graphics.Color.parseColor("#1A237E"))
            binding.tabMyReviews.setBackgroundColor(android.graphics.Color.TRANSPARENT)
            binding.tabMyReviews.setTextColor(android.graphics.Color.parseColor("#9E9E9E"))
            binding.contentWriteReview.visibility = View.VISIBLE
            binding.contentMyReviews.visibility = View.GONE
        } else {
            // Activate "Mənim rəylərim" tab
            binding.tabMyReviews.setBackgroundResource(R.drawable.bg_chip_selected)
            binding.tabMyReviews.setTextColor(android.graphics.Color.parseColor("#1A237E"))
            binding.tabWriteReview.setBackgroundColor(android.graphics.Color.TRANSPARENT)
            binding.tabWriteReview.setTextColor(android.graphics.Color.parseColor("#9E9E9E"))
            binding.contentMyReviews.visibility = View.VISIBLE
            binding.contentWriteReview.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
