package com.example.onemarket.presentation.city

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.onemarket.data.local.CityManager
import com.example.onemarket.databinding.FragmentCityBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CityFragment : Fragment() {

    private var _binding: FragmentCityBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var cityManager: CityManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentCity = cityManager.getCity()

        val adapter = CityAdapter(selectedCity = currentCity) { city ->
            cityManager.saveCity(city)
            findNavController().popBackStack()
        }

        binding.recyclerViewCities.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewCities.adapter = adapter
        adapter.submitList(CityData.cities)

        // Seçili şəhərə scroll et
        val idx = CityData.cities.indexOf(currentCity)
        if (idx >= 0) {
            binding.recyclerViewCities.scrollToPosition(idx)
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}