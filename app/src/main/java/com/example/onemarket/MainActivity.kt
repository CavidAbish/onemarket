package com.example.onemarket

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.onemarket.data.local.CartManager
import com.example.onemarket.data.local.FavoritesManager
import com.example.onemarket.data.local.LanguageManager
import com.example.onemarket.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @Inject lateinit var favoritesManager: FavoritesManager
    @Inject lateinit var cartManager: CartManager

    override fun attachBaseContext(newBase: Context) {
        val lang = LanguageManager.readLanguage(newBase)
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = Configuration(newBase.resources.configuration)
        config.setLocale(locale)
        super.attachBaseContext(newBase.createConfigurationContext(config))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNav.setupWithNavController(navController)

        binding.bottomNav.setOnItemSelectedListener { item ->
            if (item.itemId == R.id.cartFragment) {
                try { navController.popBackStack(R.id.deliveryFragment, true) } catch (_: Exception) {}
            }
            navController.navigate(item.itemId)
            true
        }

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
            R.id.categoryProductsFragment,
            R.id.myOrdersFragment,
            R.id.orderDetailFragment,
            R.id.threeDSecureFragment
        )

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id in hideBottomNavFragments) {
                binding.bottomNav.visibility = View.GONE
            } else {
                binding.bottomNav.visibility = View.VISIBLE
                updateNavBadges()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateNavBadges()
    }

    fun updateNavBadges() {
        val favCount = favoritesManager.getFavorites().size
        val favBadge = binding.bottomNav.getOrCreateBadge(R.id.favoritesFragment)
        favBadge.isVisible = favCount > 0
        if (favCount > 0) favBadge.number = favCount

        val cartCount = cartManager.getCartItems().sumOf { it.quantity }
        val cartBadge = binding.bottomNav.getOrCreateBadge(R.id.cartFragment)
        cartBadge.isVisible = cartCount > 0
        if (cartCount > 0) cartBadge.number = cartCount
    }
}
