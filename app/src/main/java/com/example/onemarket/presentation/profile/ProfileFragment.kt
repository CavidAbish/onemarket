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
        if (userManager.isLoggedIn()) showLoggedIn() else showLoggedOut()
        updateNotifBadge()
        binding.ivNotification.setOnClickListener {
            findNavController().navigate(
                ProfileFragmentDirections.actionProfileFragmentToNotificationsFragment()
            )
        }
    }

    private fun updateNotifBadge() {
        val count = appNotificationManager.getUnreadCount()
        binding.tvNotifBadgeProfile.visibility = if (count > 0) View.VISIBLE else View.GONE
        if (count > 0) binding.tvNotifBadgeProfile.text = if (count > 99) "99+" else count.toString()
    }

    private fun showLoggedOut() {
        binding.loggedOutLayout.visibility = View.VISIBLE
        binding.loggedInLayout.visibility = View.GONE
        binding.dividerAfterLanguage.visibility = View.GONE
        binding.menuNotifications.visibility = View.GONE
        binding.dividerLogout.visibility = View.GONE
        binding.menuLogout.visibility = View.GONE
        binding.tvDeleteAccount.visibility = View.GONE
        binding.btnLogin.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToLoginFragment())
        }
        setupCommonMenus()
    }

    private fun showLoggedIn() {
        binding.loggedOutLayout.visibility = View.GONE
        binding.loggedInLayout.visibility = View.VISIBLE

        val fullName = personalInfoManager.getFullName()
        binding.tvUserName.text = fullName.ifEmpty { userManager.getUserName().ifEmpty { "İstifadəçi" } }
        binding.tvUserPhone.text = userManager.getUserPhone()

        binding.cardBirId.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToBirIdProfileFragment())
        }
        binding.menuOrders.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToMyOrdersFragment())
        }
        binding.menuCards.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToMyCardsFragment())
        }
        binding.menuReturns.setOnClickListener {
            Toast.makeText(requireContext(), getString(R.string.profile_returns), Toast.LENGTH_SHORT).show()
        }
        binding.menuAddresses.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToSavedAddressesFragment())
        }
        binding.menuPromo.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToPromoCodesFragment())
        }
        binding.menuReviews.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToReviewsFragment())
        }
        binding.menuExtra.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToExtraServicesFragment())
        }
        binding.menuCredit.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToCreditApplicationsFragment())
        }

        binding.dividerAfterLanguage.visibility = View.VISIBLE
        binding.menuNotifications.visibility = View.VISIBLE
        binding.dividerLogout.visibility = View.VISIBLE
        binding.menuLogout.visibility = View.VISIBLE
        binding.tvDeleteAccount.visibility = View.VISIBLE

        binding.menuLogout.setOnClickListener { logout() }
        binding.tvDeleteAccount.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToDeleteAccountFragment())
        }

        setupCommonMenus()
    }

    private fun logout() {
        val rootView = requireActivity().window.decorView
        val fadeOut = ObjectAnimator.ofFloat(rootView, "alpha", 1f, 0f).apply { duration = 400 }
        AnimatorSet().apply {
            play(fadeOut)
            start()
            addListener(object : android.animation.AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: android.animation.Animator) {
                    userManager.logout()
                    requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav)
                        .selectedItemId = R.id.homeFragment
                    ObjectAnimator.ofFloat(rootView, "alpha", 0f, 1f).apply { duration = 400 }.start()
                    updateUI()
                }
            })
        }
    }

    private fun setupCommonMenus() {
        binding.titleCity.text = cityManager.getCity()
        binding.titleLanguage.text = languageManager.getLanguageLabel()

        binding.menuCity.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToCityFragment())
        }
        binding.menuLanguage.setOnClickListener {
            LanguageBottomSheetFragment()
                .show(parentFragmentManager, LanguageBottomSheetFragment::class.java.simpleName)
        }
        binding.menuNotifications.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToNotificationsFragment())
        }
        binding.menuSupport.setOnClickListener {
            Toast.makeText(requireContext(), getString(R.string.profile_support), Toast.LENGTH_SHORT).show()
        }
        binding.menuServiceCenters.setOnClickListener {
            Toast.makeText(requireContext(), getString(R.string.profile_service_centers), Toast.LENGTH_SHORT).show()
        }
        binding.menuDelivery.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToDeliveryPaymentFragment())
        }
        binding.menuPickup.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToPickupListFragment())
        }
        binding.menuFaq.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToFaqFragment())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
