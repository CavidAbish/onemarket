package com.example.onemarket.presentation.profile

import android.app.DatePickerDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.onemarket.data.local.PersonalInfoManager
import com.example.onemarket.data.local.UserManager
import com.example.onemarket.databinding.BottomSheetPersonalInfoBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
class PersonalInfoBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetPersonalInfoBinding? = null
    private val binding get() = _binding!!

    @Inject lateinit var personalInfoManager: PersonalInfoManager
    @Inject lateinit var userManager: UserManager


    private var passportPrefix = "AA"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetPersonalInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        (dialog as? BottomSheetDialog)?.behavior?.apply {
            state = BottomSheetBehavior.STATE_EXPANDED
            isDraggable = true
            skipCollapsed = true
        }

        loadData()
        setupListeners()
    }

    private fun loadData() {
        val phone = userManager.getUserPhone()


        binding.tvPhone.text = phone.ifEmpty { "" }


        binding.etFirstName.setText(personalInfoManager.getFirstName())
        binding.etLastName.setText(personalInfoManager.getLastName())
        binding.etFatherName.setText(personalInfoManager.getFatherName())
        binding.etFin.setText(personalInfoManager.getFin())


        val fullPassport = personalInfoManager.getPassport()
        if (fullPassport.length >= 2) {
            val savedPrefix = fullPassport.take(2)
            if (savedPrefix == "AA" || savedPrefix == "AZ") {
                passportPrefix = savedPrefix
                binding.tvPassportPrefix.text = passportPrefix
                binding.etPassportNumber.setText(fullPassport.drop(2))
            } else {
                binding.etPassportNumber.setText(fullPassport)
            }
        }

        // Birth date
        val bd = personalInfoManager.getBirthDate()
        binding.tvBirthDate.text = bd.ifEmpty { "—" }

        // Gender
        val gender = personalInfoManager.getGender()
        binding.tvGender.text = gender.ifEmpty { "—" }

        // Email
        binding.etEmail.setText(personalInfoManager.getEmail())


        val cif = personalInfoManager.getOrGenerateCif(phone)
        binding.tvCif.text = cif
    }

    private fun setupListeners() {
        binding.btnClose.setOnClickListener { dismiss() }

        // ── Birth date → DatePickerDialog ──
        binding.tvBirthDate.setOnClickListener { showDatePicker() }
        binding.layoutGender.setOnClickListener { showGenderPicker() }

        // ── Passport prefix picker (AA / AZ) ──
        binding.layoutPassportPrefix.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Seriya seçin")
                .setItems(arrayOf("AA", "AZ")) { _, which ->
                    passportPrefix = if (which == 0) "AA" else "AZ"
                    binding.tvPassportPrefix.text = passportPrefix
                }
                .show()
        }


        binding.btnCopyFin.setOnClickListener {
            val fin = binding.etFin.text?.toString()?.trim() ?: ""
            if (fin.isNotEmpty()) copyToClipboard("FİN", fin)
        }

        binding.btnCopyPassport.setOnClickListener {
            val num = binding.etPassportNumber.text?.toString()?.trim() ?: ""
            if (num.isNotEmpty()) copyToClipboard("Seriya nömrəsi", passportPrefix + num)
        }


        binding.btnSave.setOnClickListener { validateAndSave() }
    }


    private fun showDatePicker() {
        val cal = Calendar.getInstance()
        // Default start: 18 years ago
        var year = cal.get(Calendar.YEAR) - 18
        var month = cal.get(Calendar.MONTH)
        var day = cal.get(Calendar.DAY_OF_MONTH)

        // Pre-fill if already set
        val existing = binding.tvBirthDate.text.toString()
        if (existing != "—" && existing.contains("-")) {
            val parts = existing.split("-")
            if (parts.size == 3) {
                year = parts[0].toIntOrNull() ?: year
                month = (parts[1].toIntOrNull() ?: (month + 1)) - 1
                day = parts[2].toIntOrNull() ?: day
            }
        }

        DatePickerDialog(requireContext(), { _, y, m, d ->
            val date = "%d-%02d-%02d".format(y, m + 1, d)
            binding.tvBirthDate.text = date
        }, year, month, day).show()
    }


    private fun showGenderPicker() {
        val options = arrayOf("Kişi", "Qadın")
        AlertDialog.Builder(requireContext())
            .setTitle("Cins seçin")
            .setItems(options) { _, which ->
                binding.tvGender.text = options[which]
            }
            .show()
    }


    private fun copyToClipboard(label: String, text: String) {
        val cb = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        cb.setPrimaryClip(ClipData.newPlainText(label, text))
        Toast.makeText(requireContext(), "Kopyalandı", Toast.LENGTH_SHORT).show()
    }


    private fun validateAndSave() {
        val firstName = binding.etFirstName.text?.toString()?.trim() ?: ""
        val lastName  = binding.etLastName.text?.toString()?.trim() ?: ""
        val fatherName = binding.etFatherName.text?.toString()?.trim() ?: ""
        val fin = binding.etFin.text?.toString()?.trim()?.uppercase() ?: ""
        val passportNum = binding.etPassportNumber.text?.toString()?.trim() ?: ""
        val passport = if (passportNum.isNotEmpty()) passportPrefix + passportNum else ""
        val birthDate = binding.tvBirthDate.text?.toString()?.let { if (it == "—") "" else it } ?: ""
        val gender = binding.tvGender.text?.toString()?.let { if (it == "—") "" else it } ?: ""
        val email = binding.etEmail.text?.toString()?.trim() ?: ""
        val cif = binding.tvCif.text?.toString()?.let { if (it == "—") "" else it } ?: ""


        if (fin.isNotEmpty() && fin.length != 7) {
            Toast.makeText(requireContext(), "FİN kodu mütləq 7 simvol olmalıdır", Toast.LENGTH_SHORT).show()
            binding.etFin.requestFocus()
            return
        }


        if (email.isNotEmpty() && !isValidEmail(email)) {
            Toast.makeText(requireContext(), "E-poçt düzgün deyil (@ işarəsi tələb olunur)", Toast.LENGTH_SHORT).show()
            binding.etEmail.requestFocus()
            return
        }


        personalInfoManager.saveAll(
            firstName, lastName, fatherName, fin, passport, birthDate, gender, email, cif
        )


        if (firstName.isNotEmpty() || lastName.isNotEmpty()) {
            val fullName = if (lastName.isNotEmpty()) "$firstName $lastName" else firstName
            userManager.saveUser(fullName, userManager.getUserPhone())
        }

        Toast.makeText(requireContext(), "Məlumatlar yadda saxlanıldı", Toast.LENGTH_SHORT).show()

        parentFragmentManager.setFragmentResult("personal_info_saved", android.os.Bundle())
        dismiss()
    }

    private fun isValidEmail(email: String): Boolean {
        return email.contains("@") && email.contains(".") && email.indexOf("@") < email.lastIndexOf(".")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
