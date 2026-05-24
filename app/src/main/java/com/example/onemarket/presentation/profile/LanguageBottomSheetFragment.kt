package com.example.onemarket.presentation.profile

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.onemarket.MainActivity
import com.example.onemarket.R
import com.example.onemarket.data.local.LanguageManager
import com.example.onemarket.databinding.BottomSheetLanguageBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LanguageBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: BottomSheetLanguageBinding? = null
    private val binding get() = _binding!!

    @Inject lateinit var languageManager: LanguageManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetLanguageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val current = languageManager.getLanguage()
        applySelection(current, animate = false)

        binding.btnClose.setOnClickListener { dismiss() }

        binding.rowAz.setOnClickListener { selectLanguage("az") }
        binding.rowEn.setOnClickListener { selectLanguage("en") }
    }

    private fun applySelection(lang: String, animate: Boolean = true) {
        val pink = requireContext().getColor(R.color.pink_main)
        val dark = android.graphics.Color.parseColor("#1A1A1A")

        binding.tvLangAz.setTextColor(if (lang == "az") pink else dark)
        binding.ivCheckAz.visibility = if (lang == "az") View.VISIBLE else View.GONE

        binding.tvLangEn.setTextColor(if (lang == "en") pink else dark)
        binding.ivCheckEn.visibility = if (lang == "en") View.VISIBLE else View.GONE
    }

    private fun selectLanguage(lang: String) {
        if (lang == languageManager.getLanguage()) {
            dismiss()
            return
        }

        languageManager.saveLanguage(lang)
        dismiss()

        val activity = requireActivity()
        val decorView = activity.window.decorView

        val fadeOut = ObjectAnimator.ofFloat(decorView, "alpha", 1f, 0f)
        fadeOut.duration = 600
        fadeOut.start()

        fadeOut.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                Handler(Looper.getMainLooper()).postDelayed({
                    val intent = Intent(activity, MainActivity::class.java)
                    intent.addFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
                    )
                    activity.startActivity(intent)
                    activity.finish()
                    activity.overridePendingTransition(android.R.anim.fade_in, 0)
                }, 400)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
