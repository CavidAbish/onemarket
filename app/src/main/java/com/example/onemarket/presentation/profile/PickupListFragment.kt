package com.example.onemarket.presentation.profile

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.onemarket.R
import com.example.onemarket.data.local.PickupPoint
import com.example.onemarket.data.local.PickupPointsData
import com.example.onemarket.databinding.FragmentPickupListBinding
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PickupListFragment : Fragment() {

    private var _binding: FragmentPickupListBinding? = null
    private val binding get() = _binding!!

    private val allPoints = PickupPointsData.points
    private val filteredPoints = allPoints.toMutableList()
    private lateinit var listAdapter: PickupListAdapter

    private var selectedRegion = "Hamısı"
    private var searchQuery = ""

    // District keywords for filtering
    private val knownRegions = listOf(
        "Nəsimi", "Nərimanov", "Xətai", "Yasamal", "Sabunçu", "Suraxanı", "Binəqədi", "Xəzər"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPickupListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

        listAdapter = PickupListAdapter(filteredPoints) { point ->
            findNavController().navigate(
                PickupListFragmentDirections.actionPickupListFragmentToPickupMapFragment(point.id)
            )
        }
        binding.rvPickupList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPickupList.adapter = listAdapter

        setupSearch()
        setupChips()
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchQuery = s.toString().lowercase()
                applyFilter()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupChips() {
        binding.chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            val chipId = checkedIds.firstOrNull() ?: R.id.chipAll
            val chip = group.findViewById<Chip>(chipId)
            selectedRegion = chip?.text?.toString() ?: "Hamısı"
            applyFilter()
        }
    }

    private fun applyFilter() {
        filteredPoints.clear()
        filteredPoints.addAll(allPoints.filter { point ->
            val matchSearch = searchQuery.isEmpty() ||
                point.name.lowercase().contains(searchQuery) ||
                point.address.lowercase().contains(searchQuery)

            val matchRegion = when (selectedRegion) {
                "Hamısı" -> true
                "Digər" -> knownRegions.none { r ->
                    point.address.contains(r, ignoreCase = true)
                }
                else -> point.address.contains(selectedRegion, ignoreCase = true) ||
                        point.name.contains(selectedRegion, ignoreCase = true)
            }

            matchSearch && matchRegion
        })
        listAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class PickupListAdapter(
    private val items: List<PickupPoint>,
    private val onShowOnMap: (PickupPoint) -> Unit
) : RecyclerView.Adapter<PickupListAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvListPointName)
        val tvAddress: TextView = view.findViewById(R.id.tvListPointAddress)
        val tvHours: TextView = view.findViewById(R.id.tvListPointHours)
        val btnShowOnMap: Button = view.findViewById(R.id.btnShowOnMap)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_pickup_list, parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.tvName.text = item.name
        holder.tvAddress.text = item.address
        holder.tvHours.text = item.hours
        holder.btnShowOnMap.setOnClickListener { onShowOnMap(item) }
    }

    override fun getItemCount() = items.size
}
