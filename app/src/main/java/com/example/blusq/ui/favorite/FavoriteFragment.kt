package com.example.blusq.ui.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.blusq.R
import com.example.blusq.databinding.FragmentFavoriteBinding
import com.example.blusq.ui.EventAdapter
import com.example.blusq.ui.ViewModelFactory
import com.example.blusq.ui.main.MainViewModel

class FavoriteFragment : Fragment() {
    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = ViewModelFactory.getInstance(requireActivity())
        val viewModel: MainViewModel by viewModels { factory }


        val eventAdapter = EventAdapter { event ->
            val bundle = Bundle().apply {
                putInt("eventId", event.id)
            }
            findNavController().navigate(R.id.action_favoriteFragment_to_detailEventFragment, bundle)
        }

        binding.rvFavorite.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = eventAdapter
        }

        binding.progressBar.visibility = View.VISIBLE
        viewModel.getFavoriteEvents().observe(viewLifecycleOwner) { favoriteEvents ->
            binding.progressBar.visibility = View.GONE
            if (favoriteEvents != null) {
                eventAdapter.submitList(favoriteEvents)
                binding.rvFavorite.visibility = if (favoriteEvents.isEmpty()) View.GONE else View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}