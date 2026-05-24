package com.example.onemarket.presentation.delivery

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.onemarket.R
import com.example.onemarket.data.local.CityManager
import com.example.onemarket.data.local.PickupHistoryManager
import com.example.onemarket.data.local.PickupPoint
import com.example.onemarket.data.local.PickupPointsData
import com.example.onemarket.databinding.FragmentPickupMapBinding
import dagger.hilt.android.AndroidEntryPoint
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import javax.inject.Inject

@AndroidEntryPoint
class PickupMapFragment : Fragment() {

    private var _binding: FragmentPickupMapBinding? = null
    private val binding get() = _binding!!
    private lateinit var mapView: MapView
    private val args: PickupMapFragmentArgs by navArgs()

    @Inject lateinit var cityManager: CityManager
    @Inject lateinit var historyManager: PickupHistoryManager

    private var allPoints = PickupPointsData.points
    private var filteredPoints = allPoints.toMutableList()
    private lateinit var adapter: PickupPointsAdapter


    private var infoCardPoint: PickupPoint? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Configuration.getInstance().userAgentValue = requireContext().packageName
        _binding = FragmentPickupMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            binding.cardPointInfo.visibility = View.GONE
            findNavController().popBackStack()
        }

        setupMap()
        setupList()
        setupSearch()
        setupInfoCard()
    }

    private fun setupMap() {
        mapView = binding.mapView
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)

        val singlePointId = args.pointId
        if (singlePointId >= 0) {

            val point = allPoints.find { it.id == singlePointId }
            if (point != null) {
                mapView.controller.setZoom(16.0)
                mapView.controller.setCenter(GeoPoint(point.lat, point.lng))
                addMarker(point)
            }
        } else {

            val (lat, lng) = cityManager.getCoordinates()
            mapView.controller.setZoom(12.0)
            mapView.controller.setCenter(GeoPoint(lat, lng))
            allPoints.forEach { addMarker(it) }
        }
    }

    private fun addMarker(point: PickupPoint) {
        val marker = Marker(mapView)
        marker.position = GeoPoint(point.lat, point.lng)
        marker.title = point.name
        marker.snippet = point.address
        marker.icon = createBirmarketIcon()
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

        marker.setOnMarkerClickListener { _, _ ->
            showInfoCard(point)
            true
        }

        mapView.overlays.add(marker)
    }

    private fun showInfoCard(point: PickupPoint) {
        infoCardPoint = point
        binding.tvInfoPointName.text = point.name
        binding.tvInfoMaxWeight.text = point.maxWeight
        binding.tvInfoAddress.text = point.address
        binding.tvInfoHours.text = point.hours
        binding.cardPointInfo.visibility = View.VISIBLE
    }

    private fun setupInfoCard() {
        binding.btnInfoSelect.setOnClickListener {
            val point = infoCardPoint ?: return@setOnClickListener
            selectPoint(point)
        }

        // Tap on map area hides the info card
        mapView.setOnTouchListener { _, _ ->
            if (binding.cardPointInfo.visibility == View.VISIBLE) {
                binding.cardPointInfo.visibility = View.GONE
            }
            false
        }
    }

    private fun selectPoint(point: PickupPoint) {
        // Save to history (only in delivery/selection mode)
        if (args.pointId < 0) {
            historyManager.addPickupPoint(point)
        }
        val bundle = Bundle().apply {
            putString("pickup_address", point.address)
            putString("pickup_name", point.name)
            putInt("pickup_id", point.id)
        }
        parentFragmentManager.setFragmentResult("pickup_result", bundle)
        findNavController().popBackStack()
    }

    private fun createBirmarketIcon(): android.graphics.drawable.Drawable {
        val size = 80
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)


        paint.color = Color.parseColor("#E91E8C")
        val path = Path()
        path.moveTo(size / 2f, 4f)
        path.lineTo(size - 4f, size / 2f)
        path.lineTo(size / 2f, size - 4f)
        path.lineTo(4f, size / 2f)
        path.close()
        canvas.drawPath(path, paint)


        paint.color = Color.WHITE
        val inner = Path()
        val offset = 14f
        inner.moveTo(size / 2f, offset + 4f)
        inner.lineTo(size - offset - 4f, size / 2f)
        inner.lineTo(size / 2f, size - offset - 4f)
        inner.lineTo(offset + 4f, size / 2f)
        inner.close()
        canvas.drawPath(inner, paint)

        return android.graphics.drawable.BitmapDrawable(resources, bitmap)
    }

    private fun setupList() {
        adapter = PickupPointsAdapter(filteredPoints) { point ->
            selectPoint(point)
        }
        binding.rvPickupPoints.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPickupPoints.adapter = adapter
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().lowercase()
                filteredPoints.clear()
                filteredPoints.addAll(
                    allPoints.filter {
                        it.name.lowercase().contains(query) || it.address.lowercase().contains(query)
                    }
                )
                adapter.notifyDataSetChanged()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun onResume() { super.onResume(); mapView.onResume() }
    override fun onPause() { super.onPause(); mapView.onPause() }
    override fun onDestroyView() {
        super.onDestroyView()
        mapView.onDetach()
        _binding = null
    }
}

class PickupPointsAdapter(
    private val items: List<PickupPoint>,
    private val onSelect: (PickupPoint) -> Unit
) : RecyclerView.Adapter<PickupPointsAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvPointName)
        val tvAddress: TextView = view.findViewById(R.id.tvPointAddress)
        val tvHours: TextView = view.findViewById(R.id.tvPointHours)
        val tvWeight: TextView = view.findViewById(R.id.tvMaxWeight)
        val btnSelect: Button = view.findViewById(R.id.btnSelect)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_pickup_point, parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.tvName.text = item.name
        holder.tvAddress.text = item.address
        holder.tvHours.text = item.hours
        holder.tvWeight.text = item.maxWeight
        holder.btnSelect.setOnClickListener { onSelect(item) }
    }

    override fun getItemCount() = items.size
}
