package com.example.onemarket.presentation.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.onemarket.data.local.AppNotification
import com.example.onemarket.data.local.AppNotificationManager
import com.example.onemarket.databinding.FragmentNotificationsBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    @Inject lateinit var notificationManager: AppNotificationManager

    /** 0 = Hamısı, 1 = Sifarişlər */
    private var selectedTab = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Bildirişlər açıldıqda hamısı oxunmuş sayılır — badge silinir
        notificationManager.markAllRead()

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

        binding.tabAll.setOnClickListener    { selectTab(0) }
        binding.tabOrders.setOnClickListener { selectTab(1) }

        binding.recyclerViewNotifications.layoutManager = LinearLayoutManager(requireContext())

        loadNotifications()
    }

    private fun selectTab(tab: Int) {
        selectedTab = tab
        val pink  = requireContext().getColor(com.example.onemarket.R.color.pink_main)
        val white = android.graphics.Color.WHITE
        val dark  = android.graphics.Color.parseColor("#555555")

        if (tab == 0) {
            binding.tabAll.setBackgroundResource(com.example.onemarket.R.drawable.bg_chip_selected)
            binding.tabAll.setTextColor(white)
            binding.tabOrders.setBackgroundColor(android.graphics.Color.TRANSPARENT)
            binding.tabOrders.setTextColor(dark)
        } else {
            binding.tabOrders.setBackgroundResource(com.example.onemarket.R.drawable.bg_chip_selected)
            binding.tabOrders.setTextColor(white)
            binding.tabAll.setBackgroundColor(android.graphics.Color.TRANSPARENT)
            binding.tabAll.setTextColor(dark)
        }
        loadNotifications()
    }

    private fun loadNotifications() {
        val all = notificationManager.getNotifications()

        // "Sifarişlər" tabında kredit bildirişləri göstərilmir
        val filtered = if (selectedTab == 1)
            all.filter { it.type != "credit" }
        else
            all

        if (filtered.isEmpty()) {
            binding.emptyLayout.visibility = View.VISIBLE
            binding.recyclerViewNotifications.visibility = View.GONE
            return
        }

        binding.emptyLayout.visibility = View.GONE
        binding.recyclerViewNotifications.visibility = View.VISIBLE

        val listItems = buildListItems(filtered)
        binding.recyclerViewNotifications.adapter = NotificationsAdapter(listItems) { notif ->
            if (notif.type == "credit" && notif.creditApplicationId > 0) {
                findNavController().navigate(
                    NotificationsFragmentDirections
                        .actionNotificationsFragmentToCreditDetailFragment(notif.creditApplicationId)
                )
            } else if (notif.orderId > 0) {
                findNavController().navigate(
                    NotificationsFragmentDirections
                        .actionNotificationsFragmentToOrderDetailFragment(notif.orderId)
                )
            }
        }
    }

    private fun buildListItems(notifications: List<AppNotification>): List<NotifListItem> {
        val result = mutableListOf<NotifListItem>()
        var lastLabel = ""
        for (n in notifications) {
            val label = dateLabel(n.dateTime)
            if (label != lastLabel) {
                result.add(NotifListItem.DateHeader(label))
                lastLabel = label
            }
            result.add(NotifListItem.Item(n))
        }
        return result
    }

    private fun dateLabel(dateTime: String): String {
        // dateTime = "HH:mm dd.MM.yyyy"
        val datePart = dateTime.substringAfter(" ").trim()
        val fmt = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val today = fmt.format(Date())
        val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
        val yesterday = fmt.format(cal.time)
        return when (datePart) {
            today     -> "Bu gün"
            yesterday -> "Dünən"
            else      -> datePart
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
