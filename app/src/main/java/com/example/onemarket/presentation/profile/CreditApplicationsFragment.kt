package com.example.onemarket.presentation.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.onemarket.R
import com.example.onemarket.data.local.CreditApplication
import com.example.onemarket.data.local.CreditManager
import com.example.onemarket.databinding.FragmentCreditApplicationsBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CreditApplicationsFragment : Fragment() {

    private var _binding: FragmentCreditApplicationsBinding? = null
    private val binding get() = _binding!!

    @Inject lateinit var creditManager: CreditManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreditApplicationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        loadApplications()
    }

    override fun onResume() {
        super.onResume()
        loadApplications()
    }

    private fun loadApplications() {
        val apps = creditManager.getApplications()
        if (apps.isEmpty()) {
            binding.emptyState.visibility = View.VISIBLE
            binding.rvCreditApplications.visibility = View.GONE
        } else {
            binding.emptyState.visibility = View.GONE
            binding.rvCreditApplications.visibility = View.VISIBLE
            val adapter = CreditApplicationAdapter(apps)
            binding.rvCreditApplications.layoutManager = LinearLayoutManager(requireContext())
            binding.rvCreditApplications.adapter = adapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class CreditApplicationAdapter(
    private val items: List<CreditApplication>
) : RecyclerView.Adapter<CreditApplicationAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvCreditProductName)
        val tvAmount: TextView = view.findViewById(R.id.tvCreditAmount)
        val tvMonthly: TextView = view.findViewById(R.id.tvCreditMonthly)
        val tvDate: TextView = view.findViewById(R.id.tvCreditDate)
        val tvStatus: TextView = view.findViewById(R.id.tvCreditStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_credit_application, parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.tvName.text = item.productNames
        holder.tvAmount.text = String.format("%.2f ₼", item.totalAmount)
        holder.tvMonthly.text = String.format("Aylıq: %.2f ₼ × %d ay", item.monthlyPayment, item.months)
        holder.tvDate.text = item.date
        holder.tvStatus.text = item.status
    }

    override fun getItemCount() = items.size
}
