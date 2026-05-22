package com.example.onemarket.presentation.delivery

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.onemarket.data.local.CityManager
import com.example.onemarket.databinding.FragmentMapPickerBinding
import dagger.hilt.android.AndroidEntryPoint
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class MapPickerFragment : Fragment() {

    private var _binding: FragmentMapPickerBinding? = null
    private val binding get() = _binding!!

    @Inject lateinit var cityManager: CityManager

    private lateinit var mapView: MapView
    private var locationOverlay: MyLocationNewOverlay? = null
    private var currentGeoPoint = GeoPoint(40.4093, 49.8671)
    private var selectedAddress = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Configuration.getInstance().userAgentValue = requireContext().packageName
        _binding = FragmentMapPickerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Centre on the currently selected city
        val (lat, lng) = cityManager.getCoordinates()
        currentGeoPoint = GeoPoint(lat, lng)

        setupMap()

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        binding.btnZoomIn.setOnClickListener { mapView.controller.zoomIn() }
        binding.btnZoomOut.setOnClickListener { mapView.controller.zoomOut() }
        binding.btnMyLocation.setOnClickListener {
            locationOverlay?.myLocation?.let { point ->
                mapView.controller.animateTo(point)
                mapView.controller.setZoom(16.0)
            }
        }

        binding.btnConfirm.setOnClickListener {
            val result = Bundle().apply {
                putString("selected_address", selectedAddress)
                putDouble("lat", currentGeoPoint.latitude)
                putDouble("lng", currentGeoPoint.longitude)
            }
            parentFragmentManager.setFragmentResult("map_result", result)
            findNavController().popBackStack()
        }
    }

    private fun setupMap() {
        mapView = binding.mapView
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)
        mapView.controller.setZoom(15.0)
        mapView.controller.setCenter(currentGeoPoint)

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(requireContext()), mapView)
            locationOverlay?.enableMyLocation()
            mapView.overlays.add(locationOverlay)
        } else {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1001)
        }

        mapView.addMapListener(object : org.osmdroid.events.MapListener {
            override fun onScroll(event: org.osmdroid.events.ScrollEvent?): Boolean {
                val center = mapView.mapCenter
                currentGeoPoint = GeoPoint(center.latitude, center.longitude)
                getAddressFromPoint(currentGeoPoint)
                return false
            }
            override fun onZoom(event: org.osmdroid.events.ZoomEvent?) = false
        })
    }

    private fun getAddressFromPoint(point: GeoPoint) {
        try {
            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            val addresses = geocoder.getFromLocation(point.latitude, point.longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                selectedAddress = addresses[0].getAddressLine(0) ?: ""
                binding.tvAddress.text = selectedAddress
                binding.tvCoordinates.text = ""
                binding.tvNotFound.visibility = View.GONE
            } else {
                selectedAddress = String.format("%.6f, %.6f", point.latitude, point.longitude)
                binding.tvAddress.text = "Ünvan müəyyənləşdirilir..."
                binding.tvCoordinates.text = "Dəqiq ünvanı müəyyən etmək mümkün olmadı: $selectedAddress"
                binding.tvNotFound.visibility = View.VISIBLE
            }
        } catch (e: Exception) {
            selectedAddress = String.format("%.6f, %.6f", point.latitude, point.longitude)
            binding.tvCoordinates.text = selectedAddress
        }
    }

    override fun onResume() { super.onResume(); mapView.onResume() }
    override fun onPause() { super.onPause(); mapView.onPause() }
    override fun onDestroyView() { super.onDestroyView(); mapView.onDetach(); _binding = null }
}
