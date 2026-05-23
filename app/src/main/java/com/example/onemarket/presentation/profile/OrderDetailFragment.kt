package com.example.onemarket.presentation.profile

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.onemarket.R
import com.example.onemarket.data.local.OrderManager
import com.example.onemarket.data.local.UserManager
import com.example.onemarket.databinding.FragmentOrderDetailBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OrderDetailFragment : Fragment() {

    private var _binding: FragmentOrderDetailBinding? = null
    private val binding get() = _binding!!

    private val args: OrderDetailFragmentArgs by navArgs()

    @Inject lateinit var orderManager: OrderManager
    @Inject lateinit var userManager: UserManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOrderDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

        val order = orderManager.getOrders().find { it.id == args.orderId } ?: return

        // Header — sifariş nömrəsi
        binding.tvOrderNumber.text = "Sifariş №${order.id}"

        // Header — kopyalama
        binding.btnCopyHeader.setOnClickListener {
            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(ClipData.newPlainText("order_id", order.id.toString()))
            Toast.makeText(requireContext(), "Sifariş nömrəsi kopyalandı", Toast.LENGTH_SHORT).show()
        }

        // Sifariş statusu kartı
        if (order.status == "cancelled") {
            binding.tvOrderStatusTitle.text = "Sifariş ləğv edildi"
            binding.tvOrderStatusTitle.setTextColor(android.graphics.Color.parseColor("#F44336"))
            binding.ivStatusIcon.visibility = View.GONE
            binding.ivStatusCancelled.visibility = View.VISIBLE
            binding.btnCancelOrder.visibility = View.GONE
        } else {
            binding.tvOrderStatusTitle.text = "Sifariş"
            binding.tvOrderStatusTitle.setTextColor(android.graphics.Color.parseColor("#1A237E"))
            binding.ivStatusIcon.visibility = View.VISIBLE
            binding.ivStatusCancelled.visibility = View.GONE
            binding.btnCancelOrder.visibility = View.VISIBLE
        }

        // Tarix
        binding.tvOrderDateTime.text =
            order.orderDateTime?.takeIf { it.isNotEmpty() } ?: order.date

        // Çatdırılma ünvanı
        val savedAddress = order.deliveryAddress?.takeIf { it.isNotEmpty() }
        binding.tvDeliveryAddress.text = savedAddress ?: when {
            order.paymentMethod.contains("məntəqə", ignoreCase = true) ||
            order.paymentMethod.contains("Pickup", ignoreCase = true) ->
                "Birmarket məntəqəsi"
            else -> "Kuryerlə çatdırılma"
        }
        binding.tvDeliveryTitle.text = if (
            order.paymentMethod.contains("Təhvil", ignoreCase = true) ||
            order.paymentMethod.contains("məntəqə", ignoreCase = true)
        ) "Təhvil məntəqəsindən təslim alma" else "Kuryerlə çatdırılma"

        // Sifarişin tərkibi
        binding.tvSellerName.text = order.product.brand.ifEmpty { "OneMarket" }
        binding.tvProductTitle.text = order.product.title
        binding.tvProductPrice.text = String.format("%.2f ₼", order.product.price)
        binding.tvProductOldPrice.text = String.format("%.2f ₼", order.product.originalPrice)
        binding.tvProductOldPrice.paintFlags =
            binding.tvProductOldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        binding.tvProductQuantity.text = "x ${order.quantity} ədəd."

        Glide.with(this)
            .load(order.product.thumbnail)
            .centerCrop()
            .into(binding.ivProductImage)

        // Ödəniş məlumatları
        binding.tvBuyerName.text = userManager.getUserName().ifEmpty { "Alıcı" }
        binding.tvBuyerPhone.text = userManager.getUserPhone()
        binding.tvPaymentMethod.text = order.paymentMethod
        binding.tvPaymentMethod.setTextColor(android.graphics.Color.parseColor("#1A1A1A"))

        when {
            order.status == "cancelled" -> {
                binding.tvPaymentStatus.text = "Ləğv edildi"
                binding.tvPaymentStatus.setTextColor(android.graphics.Color.parseColor("#F44336"))
            }
            order.paymentMethod == "Təhvil alarkən bank kartı vasitəsi ilə" -> {
                binding.tvPaymentStatus.text = "Gözləmədədir"
                binding.tvPaymentStatus.setTextColor(android.graphics.Color.parseColor("#FF9800"))
            }
            else -> {
                binding.tvPaymentStatus.text = "Ödənilib"
                binding.tvPaymentStatus.setTextColor(requireContext().getColor(R.color.cart_green))
            }
        }

        // Cəmi
        binding.tvTotalAmount.text = String.format("%.2f ₼", order.totalAmount)

        // Sifarişi ləğv et
        binding.btnCancelOrder.setOnClickListener {
            // Capture context and NavController before entering the dialog callback.
            // Calling requireContext()/findNavController() inside an async lambda
            // can throw IllegalStateException if the fragment is no longer attached.
            val ctx = requireContext()
            val nav = findNavController()
            androidx.appcompat.app.AlertDialog.Builder(ctx)
                .setTitle("Sifarişi ləğv et")
                .setMessage("Sifarişi ləğv etmək istədiyinizdən əminsiniz?")
                .setPositiveButton("Ləğv et") { dialog, _ ->
                    dialog.dismiss()
                    orderManager.cancelOrder(order.id)
                    Toast.makeText(ctx, "Sifariş ləğv olundu", Toast.LENGTH_SHORT).show()
                    nav.popBackStack()
                }
                .setNegativeButton("Xeyr") { dialog, _ -> dialog.dismiss() }
                .show()
        }

        // Ödənişin detallarını göstər
        binding.rowPaymentDetails.setOnClickListener {
            showPaymentDetailsSheet(order)
        }

        // Dəstək xidməti
        binding.btnSupport.setOnClickListener {
            Toast.makeText(requireContext(), "Dəstək xidməti hazırda mövcud deyil", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showPaymentDetailsSheet(order: com.example.onemarket.data.local.Order) {
        val dialog = BottomSheetDialog(requireContext())
        val sheetView = layoutInflater.inflate(R.layout.bottom_sheet_payment_details, null)
        dialog.setContentView(sheetView)

        // Sifarişin məbləği
        val origAmount = order.originalAmountSum
            .takeIf { it > 0 } ?: (order.product.originalPrice * order.quantity)
        sheetView.findViewById<TextView>(R.id.tvDetailOrderLabel).text =
            "Sifarişin məbləği (${order.quantity} məhsul):"
        sheetView.findViewById<TextView>(R.id.tvDetailOrderAmount).text =
            String.format("%.2f ₼", origAmount)

        // Endirim
        val discount = order.discountSum
            .takeIf { it > 0 } ?: ((order.product.originalPrice - order.product.price) * order.quantity)
        val rowDiscount = sheetView.findViewById<LinearLayout>(R.id.rowDetailDiscount)
        val divDiscount = sheetView.findViewById<View>(R.id.divDetailDiscount)
        if (discount > 0) {
            sheetView.findViewById<TextView>(R.id.tvDetailDiscount).text =
                String.format("-%.2f ₼", discount)
            rowDiscount.visibility = View.VISIBLE
            divDiscount.visibility = View.VISIBLE
        }

        // Çatdırılma (deliveryCost=0.0 → null/missing sahə → "Pulsuz" göstər)
        val delivery = order.deliveryCost
        val deliveryTv = sheetView.findViewById<TextView>(R.id.tvDetailDelivery)
        if (delivery <= 0) {
            deliveryTv.text = "Pulsuz"
            deliveryTv.setTextColor(requireContext().getColor(R.color.cart_green))
        } else {
            deliveryTv.text = String.format("%.2f ₼", delivery)
            deliveryTv.setTextColor(requireContext().getColor(R.color.black))
        }

        // Cəmi
        sheetView.findViewById<TextView>(R.id.tvDetailTotal).text =
            String.format("%.2f ₼", order.totalAmount)

        // Bağla
        sheetView.findViewById<ImageButton>(R.id.btnCloseSheet).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
