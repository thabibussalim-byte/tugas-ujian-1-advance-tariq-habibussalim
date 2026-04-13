package com.example.blusq.ui.finish

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContentProviderCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.blusq.R
import com.example.blusq.data.local.Result
import com.example.blusq.databinding.FragmentFinishBinding
import com.example.blusq.ui.EventAdapter
import com.example.blusq.ui.ViewModelFactory
import com.example.blusq.ui.main.MainViewModel

class FinishFragment : Fragment() {

    private var _binding: FragmentFinishBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFinishBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireActivity())
        val viewModel: MainViewModel by viewModels { factory }

        val eventAdapter = EventAdapter { event ->
            val eventId = event.id
            if (eventId != null) {
                val bundle = Bundle().apply {
                    putInt("eventId", eventId)
                }
                findNavController()
                    .navigate(R.id.action_finishFragment_to_detailEventFragment, bundle)
            } else {
                Toast.makeText(requireContext(), "ID Event tidak valid", Toast.LENGTH_SHORT).show()
            }
        }

        binding.rvFinish.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = eventAdapter
        }

        viewModel.getFinishedEvents().observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        eventAdapter.submitList(result.data)
                    }
                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(context, "Error: ${result.error}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}