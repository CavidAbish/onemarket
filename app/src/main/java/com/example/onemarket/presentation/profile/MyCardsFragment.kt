package com.example.onemarket.presentation.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.onemarket.data.local.CardManager
import com.example.onemarket.databinding.FragmentMyCardsBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MyCardsFragment : Fragment() {

    private var _binding: FragmentMyCardsBinding? = null
    private val binding get() = _binding!!

    @Inject lateinit var cardManager: CardManager

    private lateinit var adapter: MyCardsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyCardsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = MyCardsAdapter { card ->
            findNavController().navigate(
                MyCardsFragmentDirections.actionMyCardsFragmentToCardDetailFragment(card.id)
            )
        }

        binding.recyclerCards.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@MyCardsFragment.adapter
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnAddCard.setOnClickListener {
            findNavController().navigate(
                MyCardsFragmentDirections.actionMyCardsFragmentToAddCardFragment()
            )
        }
    }

    override fun onResume() {
        super.onResume()
        refreshCards()
    }

    private fun refreshCards() {
        val cards = cardManager.getCards()
        if (cards.isEmpty()) {
            binding.emptyLayout.visibility = View.VISIBLE
            binding.recyclerCards.visibility = View.GONE
        } else {
            binding.emptyLayout.visibility = View.GONE
            binding.recyclerCards.visibility = View.VISIBLE
            adapter.submitList(cards.toList())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
