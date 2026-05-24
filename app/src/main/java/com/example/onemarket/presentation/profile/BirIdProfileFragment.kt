package com.example.onemarket.presentation.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.onemarket.data.local.PersonalInfoManager
import com.example.onemarket.data.local.UserManager
import com.example.onemarket.databinding.FragmentBirIdProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BirIdProfileFragment : Fragment() {

    private var _binding: FragmentBirIdProfileBinding? = null
    private val binding get() = _binding!!

    @Inject lateinit var userManager: UserManager
    @Inject lateinit var personalInfoManager: PersonalInfoManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBirIdProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.btnClose.setOnClickListener {
            findNavController().popBackStack()
        }

        updateUserInfo()


        parentFragmentManager.setFragmentResultListener(
            "personal_info_saved", viewLifecycleOwner
        ) { _, _ ->
            updateUserInfo()
        }


        binding.menuPersonalInfo.setOnClickListener {
            val sheet = PersonalInfoBottomSheet()
            sheet.show(parentFragmentManager, "PersonalInfo")
        }


        binding.menuPrivacyPolicy.setOnClickListener {
            val sheet = PrivacyPolicyBottomSheet()
            sheet.show(parentFragmentManager, "PrivacyPolicy")
        }
    }

    override fun onResume() {
        super.onResume()
        updateUserInfo()
    }

    private fun updateUserInfo() {
        val fullName = personalInfoManager.getFullName()
        binding.tvUserName.text = fullName.ifEmpty {
            userManager.getUserName().ifEmpty { "İstifadəçi" }
        }
        binding.tvUserPhone.text = userManager.getUserPhone()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
