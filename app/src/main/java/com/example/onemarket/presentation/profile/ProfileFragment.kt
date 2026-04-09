package com.example.onemarket.presentation.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.onemarket.data.local.UserManager
import com.example.onemarket.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var userManager: UserManager

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
        if (userManager.isLoggedIn()) {
            showLoggedIn()
        } else {
            showLoggedOut()
        }
    }

    private fun showLoggedOut() {
        binding.loggedOutLayout.visibility = View.VISIBLE
        binding.loggedInLayout.visibility = View.GONE

        binding.btnLogin.setOnClickListener {
            findNavController().navigate(
                ProfileFragmentDirections.actionProfileFragmentToLoginFragment()
            )
        }
    }

    private fun showLoggedIn() {
        binding.loggedOutLayout.visibility = View.GONE
        binding.loggedInLayout.visibility = View.VISIBLE

        binding.tvUserName.text = userManager.getUserName()
        binding.tvUserPhone.text = userManager.getUserPhone()

        // Menyu click-ləri
        val menus = listOf(
            binding.menuSpecial to "Special abunəlik",
            binding.menuOrders to "Mənim sifarişlərim",
            binding.menuCards to "Mənim Kartlarım",
            binding.menuPromo to "Promokodlar",
            binding.menuCredit to "Kredit müraciətləri",
            binding.menuReviews to "Rəylərim",
            binding.menuAddresses to "Sifarişlərin çatdırılması üçün ünvanlarım",
            binding.menuReturns to "Geri qaytarma müraciətləri",
            binding.menuExtra to "Əlavə xidmətlər"
        )
        menus.forEach { (v, title) ->
            v.setOnClickListener { Toast.makeText(requireContext(), title, Toast.LENGTH_SHORT).show() }
        }

        // Çıxış
        binding.menuLogout.setOnClickListener {
            userManager.logout()
            updateUI()
            Toast.makeText(requireContext(), "Çıxış edildi", Toast.LENGTH_SHORT).show()
        }

        binding.tvDeleteAccount.setOnClickListener {
            Toast.makeText(requireContext(), "Hesabı silmək", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}