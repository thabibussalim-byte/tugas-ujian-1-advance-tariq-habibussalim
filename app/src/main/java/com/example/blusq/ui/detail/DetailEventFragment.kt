package com.example.blusq.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.blusq.R
import com.example.blusq.data.local.entity.EventEntity
import com.example.blusq.databinding.FragmentDetailEventBinding
import com.example.blusq.ui.ViewModelFactory
import com.example.blusq.ui.main.MainViewModel

class DetailEventFragment : Fragment() {

    private var _binding: FragmentDetailEventBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = ViewModelFactory.getInstance(requireActivity())
        val viewModel: MainViewModel by viewModels { factory }

        val eventId = arguments?.getInt("eventId") ?: -1


        viewModel.getEventById(eventId).observe(viewLifecycleOwner) { event ->
            if (event != null) {
                displayEventDetail(event)
                setupFavoriteButton(event, viewModel)
            }
        }
    }

    private fun displayEventDetail(event: EventEntity) {
        with(binding) {
            tvName.text = event.name
            tvSummary.text = event.summary
            tvOwner.text = event.ownerName
            tvCity.text = event.cityName
            tvBeginTime.text = event.beginTime
            tvEndTime.text = event.endTime

            val remainingQuota = (event.quota ?: 0) - (event.registrants ?: 0)
            tvQuota.text = "Sisa Kuota: $remainingQuota"


            tvDescription.text = HtmlCompat.fromHtml(
                event.description ?: "",
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )

            Glide.with(requireContext())
                .load(event.mediaCover)
                .placeholder(R.drawable.ic_loading)
                .into(ivCover)

            btnRegister.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(event.link))
                startActivity(intent)
            }
        }
    }

    private fun setupFavoriteButton(event: EventEntity, viewModel: MainViewModel) {
        val ivFavorite = binding.root.findViewById<android.widget.ImageView>(R.id.iv_favorite)
        val isFavorite = event.isFavorite ?: false

        ivFavorite?.setImageResource(
            if (isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border
        )

        ivFavorite?.setOnClickListener {
            viewModel.setFavoriteEvent(event.id, !isFavorite)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}