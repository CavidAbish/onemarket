package com.example.onemarket.presentation.profile

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.onemarket.R
import com.example.onemarket.data.local.CityManager
import com.example.onemarket.data.local.UserManager
import com.example.onemarket.databinding.FragmentProfileBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    @Inject lateinit var userManager: UserManager
    @Inject lateinit var cityManager: CityManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateUI()
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    private fun updateUI() {
        if (userManager.isLoggedIn()) showLoggedIn()
        else showLoggedOut()
    }

    private fun showLoggedOut() {
        binding.loggedOutLayout.visibility = View.VISIBLE
        binding.loggedInLayout.visibility = View.GONE

        binding.btnLogin.setOnClickListener {
            findNavController().navigate(
                ProfileFragmentDirections.actionProfileFragmentToLoginFragment()
            )
        }
        setupCommonMenus()
    }

    private fun showLoggedIn() {
        binding.loggedOutLayout.visibility = View.GONE
        binding.loggedInLayout.visibility = View.VISIBLE

        binding.tvUserName.text = userManager.getUserName()
        binding.tvUserPhone.text = userManager.getUserPhone()

        // Mənim sifarişlərim — naviqasiya
        binding.menuOrders.setOnClickListener {
            findNavController().navigate(
                ProfileFragmentDirections.actionProfileFragmentToMyOrdersFragment()
            )
        }

        val menus = listOf(
            binding.menuSpecial to "Special abunəlik",
            binding.menuCards to "Mənim Kartlarım",
            binding.menuPromo to "Promokodlar",
            binding.menuReviews to "Rəylərim",
            binding.menuAddresses to "Sifarişlərin çatdırılması üçün ünvanlarım",
            binding.menuReturns to "Geri qaytarma müraciətləri",
            binding.menuExtra to "Əlavə xidmətlər"
        )
        menus.forEach { (v, title) ->
            v.setOnClickListener { Toast.makeText(requireContext(), title, Toast.LENGTH_SHORT).show() }
        }

        binding.menuCredit.setOnClickListener {
            findNavController().navigate(
                ProfileFragmentDirections.actionProfileFragmentToCreditApplicationsFragment()
            )
        }

        // Çıxış — ekran qaralsın, home-a keçsin
        binding.menuLogout.setOnClickListener {
            logout()
        }

        binding.tvDeleteAccount.setOnClickListener {
            Toast.makeText(requireContext(), "Hesabı silmək", Toast.LENGTH_SHORT).show()
        }

        setupCommonMenus()
    }

    private fun logout() {
        // Ekran qararma animasiyası
        val rootView = requireActivity().window.decorView
        val fadeOut = ObjectAnimator.ofFloat(rootView, "alpha", 1f, 0f)
        fadeOut.duration = 400

        val animSet = AnimatorSet()
        animSet.play(fadeOut)
        animSet.start()

        animSet.addListener(object : android.animation.AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: android.animation.Animator) {
                userManager.logout()

                // Home-a keç
                requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav)
                    .selectedItemId = R.id.homeFragment

                // Ekranı geri gətir
                val fadeIn = ObjectAnimator.ofFloat(rootView, "alpha", 0f, 1f)
                fadeIn.duration = 400
                fadeIn.start()

                updateUI()
            }
        })
    }

    private fun setupCommonMenus() {
        binding.titleCity.text = cityManager.getCity()

        binding.menuCity.setOnClickListener {
            findNavController().navigate(
                ProfileFragmentDirections.actionProfileFragmentToCityFragment()
            )
        }

        binding.menuLanguage.setOnClickListener {
            Toast.makeText(requireContext(), "Dil", Toast.LENGTH_SHORT).show()
        }
        binding.menuNotifications.setOnClickListener {
            Toast.makeText(requireContext(), "Bildirişlər", Toast.LENGTH_SHORT).show()
        }
        binding.menuSupport.setOnClickListener {
            Toast.makeText(requireContext(), "Dəstək xidməti", Toast.LENGTH_SHORT).show()
        }
        binding.menuDelivery.setOnClickListener {
            Toast.makeText(requireContext(), "Çatdırılma və ödəmə", Toast.LENGTH_SHORT).show()
        }
        binding.menuPickup.setOnClickListener {
            findNavController().navigate(
                ProfileFragmentDirections.actionProfileFragmentToPickupListFragment()
            )
        }
        binding.menuFaq.setOnClickListener {
            Toast.makeText(requireContext(), "Ən çox verilən suallar", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}