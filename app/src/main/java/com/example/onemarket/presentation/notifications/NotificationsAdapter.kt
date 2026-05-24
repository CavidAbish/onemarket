package com.example.onemarket.presentation.notifications

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.onemarket.R
import com.example.onemarket.data.local.AppNotification
import com.example.onemarket.databinding.ItemNotificationBinding

sealed class NotifListItem {
    data class DateHeader(val label: String) : NotifListItem()
    data class Item(val notification: AppNotification) : NotifListItem()
}

/** Hər bildiriş tipi üçün vizual stil */
private data class NotifStyle(
    val stripColor: Int,     // Sol şaquli çubuğun rəngi
    val badgeText: String,   // Tip badge mətni
    val badgeBg: Int,        // Badge arxa plan rəngi (açıq ton)
    val badgeText2: Int,     // Badge mətn rəngi (tünd ton)
    val titleColor: Int      // Başlıq rəngi
)

private fun styleFor(type: String): NotifStyle = when (type) {
    "paid" -> NotifStyle(
        stripColor  = Color.parseColor("#4CAF50"),
        badgeText   = "Ödənilib",
        badgeBg     = Color.parseColor("#E8F5E9"),
        badgeText2  = Color.parseColor("#2E7D32"),
        titleColor  = Color.parseColor("#2E7D32")
    )
    "delivery_pending" -> NotifStyle(
        stripColor  = Color.parseColor("#FF9800"),
        badgeText   = "Gözləyir",
        badgeBg     = Color.parseColor("#FFF3E0"),
        badgeText2  = Color.parseColor("#E65100"),
        titleColor  = Color.parseColor("#E65100")
    )
    "cancelled" -> NotifStyle(
        stripColor  = Color.parseColor("#F44336"),
        badgeText   = "Ləğv edildi",
        badgeBg     = Color.parseColor("#FFEBEE"),
        badgeText2  = Color.parseColor("#C62828"),
        titleColor  = Color.parseColor("#C62828")
    )
    "credit" -> NotifStyle(
        stripColor  = Color.parseColor("#1976D2"),
        badgeText   = "Kredit",
        badgeBg     = Color.parseColor("#E3F2FD"),
        badgeText2  = Color.parseColor("#1565C0"),
        titleColor  = Color.parseColor("#1565C0")
    )
    else -> NotifStyle(
        stripColor  = Color.parseColor("#9E9E9E"),
        badgeText   = "",
        badgeBg     = Color.parseColor("#F5F5F5"),
        badgeText2  = Color.parseColor("#555555"),
        titleColor  = Color.parseColor("#1A237E")
    )
}

class NotificationsAdapter(
    private val items: List<NotifListItem>,
    private val onNotifClick: (AppNotification) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEM   = 1
    }

    override fun getItemViewType(position: Int) =
        if (items[position] is NotifListItem.DateHeader) TYPE_HEADER else TYPE_ITEM

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_HEADER) {
            val view = inflater.inflate(R.layout.item_notification_header, parent, false)
            HeaderViewHolder(view as TextView)
        } else {
            ItemViewHolder(ItemNotificationBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is NotifListItem.DateHeader -> (holder as HeaderViewHolder).bind(item.label)
            is NotifListItem.Item       -> (holder as ItemViewHolder).bind(item.notification)
        }
    }

    override fun getItemCount() = items.size

    // ── ViewHolders ─────────────────────────────────────────────────────────

    class HeaderViewHolder(private val tv: TextView) : RecyclerView.ViewHolder(tv) {
        fun bind(label: String) { tv.text = label }
    }

    inner class ItemViewHolder(private val b: ItemNotificationBinding) :
        RecyclerView.ViewHolder(b.root) {

        fun bind(notif: AppNotification) {
            val style = styleFor(notif.type)

            // Sol şaquli strip rəngi
            b.vTypeStrip.setBackgroundColor(style.stripColor)

            // Başlıq
            b.tvNotifTitle.text = notif.title
            b.tvNotifTitle.setTextColor(style.titleColor)

            // Tip badge — yuvarlaq GradientDrawable arxa plan
            b.tvNotifBadge.text = style.badgeText
            b.tvNotifBadge.setTextColor(style.badgeText2)
            val density = b.root.context.resources.displayMetrics.density
            val bg = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 10f * density
                setColor(style.badgeBg)
            }
            b.tvNotifBadge.background = bg

            // Bildiriş mətni
            b.tvNotifBody.text = notif.body

            // Klik → tip əsasında yönləndir
            b.root.setOnClickListener { onNotifClick(notif) }
        }
    }
}
