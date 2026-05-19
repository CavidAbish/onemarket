package com.example.onemarket

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.onemarket.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNav.setupWithNavController(navController)

        // Bottom nav-ı gizlət/göstər
        val hideBottomNavFragments = setOf(
            R.id.deliveryFragment,
            R.id.mapPickerFragment,
            R.id.orderSummaryFragment,
            R.id.paymentFragment,
            R.id.paymentFailedFragment,
            R.id.loginFragment,
            R.id.otpFragment,
            R.id.cityFragment,
            R.id.searchFragment,
            R.id.productDetailFragment,
            R.id.categoryProductsFragment
        )

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id in hideBottomNavFragments) {
                binding.bottomNav.visibility = View.GONE
            } else {
                binding.bottomNav.visibility = View.VISIBLE
            }
        }
    }
}