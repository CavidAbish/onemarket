package com.example.onemarket.presentation.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.onemarket.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

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

        val menus = listOf(
            binding.menuSpecial to "Special abunəlik",
            binding.menuOrders to "Mənim sifarişlərim",
            binding.menuCards to "Mənim Kartlarım",
            binding.menuPromo to "Promokodlar",
            binding.menuCredit to "Kredit müraciətləri",
            binding.menuReviews to "Rəylərim",
            binding.menuAddresses to "Sifarişlərin çatdırılması üçün ünvanlarım",
            binding.menuReturns to "Geri qaytarma müraciətləri",
            binding.menuExtra to "Əlavə xidmətlər",
            binding.menuCity to "Şəhər",
            binding.menuLanguage to "Dil",
            binding.menuNotifications to "Bildirişlər",
            binding.menuSupport to "Dəstək xidməti",
            binding.menuDelivery to "Çatdırılma və ödəmə",
            binding.menuPickup to "Təhvil məntəqələri",
            binding.menuService to "Servis mərkəzləri",
            binding.menuFaq to "Ən çox verilən suallar",
            binding.menuLogout to "Çıxış"
        )

        menus.forEach { (view, title) ->
            view.setOnClickListener {
                Toast.makeText(requireContext(), title, Toast.LENGTH_SHORT).show()
            }
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