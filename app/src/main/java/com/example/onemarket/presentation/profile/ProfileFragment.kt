package com.example.onemarket.presentation.profile

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.onemarket.MainActivity
import com.example.onemarket.R
import com.example.onemarket.data.local.AppNotificationManager
import com.example.onemarket.data.local.CityManager
import com.example.onemarket.data.local.LanguageManager
import com.example.onemarket.data.local.PersonalInfoManager
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
    @Inject lateinit var appNotificationManager: AppNotificationManager
    @Inject lateinit var personalInfoManager: PersonalInfoManager
    @Inject lateinit var languageManager: LanguageManager

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
        (activity as? MainActivity)?.updateNavBadges()
    }

    private fun updateUI() {
        if (userManager.isLoggedIn()) showLoggedIn()
        else showLoggedOut()

        updateNotifBadge()

        binding.ivNotification.setOnClickListener {
            findNavController().navigate(
                ProfileFragmentDirections.actionProfileFragmentToNotificationsFragment()
            )
        }
    }

    private fun updateNotifBadge() {
        val count = appNotificationManager.getUnreadCount()
        if (count > 0) {
            binding.tvNotifBadgeProfile.visibility = View.VISIBLE
            binding.tvNotifBadgeProfile.text = if (count > 99) "99+" else count.toString()
        } else {
            binding.tvNotifBadgeProfile.visibility = View.GONE
        }
    }

    private fun showLoggedOut() {
        binding.loggedOutLayout.visibility = View.VISIBLE
        binding.loggedInLayout.visibility = View.GONE

        // Yalnız daxil olmuş halda görünən elementlər
        binding.dividerAfterLanguage.visibility = View.GONE
        binding.menuNotifications.visibility = View.GONE
        binding.dividerLogout.visibility = View.GONE
        binding.menuLogout.visibility = View.GONE
        binding.tvDeleteAccount.visibility = View.GONE

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

        // Display full name from personalInfoManager if available, else fallback to userManager
        val fullName = personalInfoManager.getFullName()
        binding.tvUserName.text = if (fullName.isNotEmpty()) fullName else userManager.getUserName().ifEmpty { "İstifadəçi" }
        binding.tvUserPhone.text = userManager.getUserPhone()

        // Tap profile card → open Bir ID screen
        binding.cardBirId.setOnClickListener {
            findNavController().navigate(
                ProfileFragmentDirections.actionProfileFragmentToBirIdProfileFragment()
            )
        }

        // Mənim sifarişlərim — naviqasiya
        binding.menuOrders.setOnClickListener {
            findNavController().navigate(
                ProfileFragmentDirections.actionProfileFragmentToMyOrdersFragment()
            )
        }

        binding.menuCards.setOnClickListener {
            findNavController().navigate(
                ProfileFragmentDirections.actionProfileFragmentToMyCardsFragment()
            )
        }

        binding.menuReturns.setOnClickListener {
            Toast.makeText(requireContext(), "Geri qaytarma müraciətləri", Toast.LENGTH_SHORT).show()
        }

        binding.menuAddresses.setOnClickListener {
            findNavController().navigate(
                ProfileFragmentDirections.actionProfileFragmentToSavedAddressesFragment()
            )
        }

        binding.menuPromo.setOnClickListener {
            findNavController().navigate(
                ProfileFragmentDirections.actionProfileFragmentToPromoCodesFragment()
            )
        }

        binding.menuReviews.setOnClickListener {
            findNavController().navigate(
                ProfileFragmentDirections.actionProfileFragmentToReviewsFragment()
            )
        }

        binding.menuExtra.setOnClickListener {
            findNavController().navigate(
                ProfileFragmentDirections.actionProfileFragmentToExtraServicesFragment()
            )
        }

        binding.menuCredit.setOnClickListener {
            findNavController().navigate(
                ProfileFragmentDirections.actionProfileFragmentToCreditApplicationsFragment()
            )
        }

        // Daxil olmuş vəziyyətdə görünən elementlər
        binding.dividerAfterLanguage.visibility = View.VISIBLE
        binding.menuNotifications.visibility = View.VISIBLE
        binding.dividerLogout.visibility = View.VISIBLE
        binding.menuLogout.visibility = View.VISIBLE
        binding.tvDeleteAccount.visibility = View.VISIBLE

        // Çıxış — ekran qaralsın, home-a keçsin
        binding.menuLogout.setOnClickListener {
            logout()
        }

        binding.tvDeleteAccount.setOnClickListener {
            findNavController().navigate(
                ProfileFragmentDirections.actionProfileFragmentToDeleteAccountFragment()
            )
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
        binding.titleLanguage.text = languageManager.getLanguageLabel()

        binding.menuCity.setOnClickListener {
            findNavController().navigate(
                ProfileFragmentDirections.actionProfileFragmentToCityFragment()
            )
        }

        binding.menuLanguage.setOnClickListener {
            showLanguagePicker()
        }

        binding.menuNotifications.setOnClickListener {
            findNavController().navigate(
                ProfileFragmentDirections.actionProfileFragmentToNotificationsFragment()
            )
        }
        binding.menuSupport.setOnClickListener {
            Toast.makeText(requireContext(), "Dəstək xidməti", Toast.LENGTH_SHORT).show()
        }
        binding.menuServiceCenters.setOnClickListener {
            Toast.makeText(requireContext(), "Servis mərkəzləri", Toast.LENGTH_SHORT).show()
        }
        binding.menuDelivery.setOnClickListener {
            findNavController().navigate(
                ProfileFragmentDirections.actionProfileFragmentToDeliveryPaymentFragment()
            )
        }
        binding.menuPickup.setOnClickListener {
            findNavController().navigate(
                ProfileFragmentDirections.actionProfileFragmentToPickupListFragment()
            )
        }
        binding.menuFaq.setOnClickListener {
            findNavController().navigate(
                ProfileFragmentDirections.actionProfileFragmentToFaqFragment()
            )
        }
    }

    private fun showLanguagePicker() {
        val languages = arrayOf(
            "🇦🇿  Azərbaycan",
            "🇷🇺  Русский",
            "🇬🇧  English",
            "🇹🇷  Türkçe"
        )
        val codes = arrayOf("az", "ru", "en", "tr")
        val currentLang = languageManager.getLanguage()
        val checkedItem = codes.indexOf(currentLang).takeIf { it >= 0 } ?: 0

        AlertDialog.Builder(requireContext())
            .setTitle("Dil seçin")
            .setSingleChoiceItems(languages, checkedItem) { dialog, which ->
                val selected = codes[which]
                languageManager.saveLanguage(selected)
                binding.titleLanguage.text = languageManager.getLanguageLabel()
                dialog.dismiss()
            }
            .setNegativeButton("Ləğv et", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
