package com.example.onemarket.presentation.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.onemarket.R

class FaqAdapter(
    private val items: List<FaqListItem>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_SECTION     = 0
        private const val TYPE_DESCRIPTION = 1
        private const val TYPE_QUESTION    = 2
    }

    override fun getItemViewType(position: Int): Int = when (items[position]) {
        is FaqListItem.SectionHeader -> TYPE_SECTION
        is FaqListItem.Description   -> TYPE_DESCRIPTION
        is FaqListItem.Question      -> TYPE_QUESTION
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_SECTION     -> SectionVH(inflater.inflate(R.layout.item_faq_section, parent, false))
            TYPE_DESCRIPTION -> DescVH(inflater.inflate(R.layout.item_faq_description, parent, false))
            else             -> QuestionVH(inflater.inflate(R.layout.item_faq_question, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is FaqListItem.SectionHeader -> (holder as SectionVH).bind(item)
            is FaqListItem.Description   -> (holder as DescVH).bind(item)
            is FaqListItem.Question      -> (holder as QuestionVH).bind(item) {
                notifyItemChanged(position)
            }
        }
    }

    override fun getItemCount(): Int = items.size



    class SectionVH(view: View) : RecyclerView.ViewHolder(view) {
        private val tv: TextView = view.findViewById(R.id.tvSectionTitle)
        fun bind(item: FaqListItem.SectionHeader) { tv.text = item.title }
    }

    class DescVH(view: View) : RecyclerView.ViewHolder(view) {
        private val tv: TextView = view.findViewById(R.id.tvDescription)
        fun bind(item: FaqListItem.Description) { tv.text = item.text }
    }

    class QuestionVH(view: View) : RecyclerView.ViewHolder(view) {
        private val tvQ: TextView = view.findViewById(R.id.tvQuestion)
        private val tvA: TextView = view.findViewById(R.id.tvAnswer)

        fun bind(item: FaqListItem.Question, onToggle: () -> Unit) {
            tvQ.text = item.question
            tvA.text = item.answer
            tvA.visibility = if (item.isExpanded) View.VISIBLE else View.GONE
            itemView.setOnClickListener {
                item.isExpanded = !item.isExpanded
                onToggle()
            }
        }
    }
}
