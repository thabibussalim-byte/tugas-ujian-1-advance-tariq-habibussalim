package com.example.blusq.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.blusq.data.local.Result
import com.example.blusq.databinding.FragmentSearchBinding
import com.example.blusq.di.Injection
import com.example.blusq.ui.EventAdapter
import com.example.blusq.ui.SettingPreferences
import com.example.blusq.ui.ViewModelFactory
import com.example.blusq.ui.dataStore

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SearchViewModel
    private lateinit var adapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pref = SettingPreferences.getInstance(requireContext().dataStore)
        val eventRepository = Injection.provideRepository(requireContext())
        val factory = ViewModelFactory(pref, eventRepository)
        viewModel = ViewModelProvider(requireActivity(), factory)[SearchViewModel::class.java]

        setupRecyclerView()
        observeSearchResult()
    }

    private fun setupRecyclerView() {
        adapter = EventAdapter { event ->
            val action = SearchFragmentDirections.actionSearchFragmentToDetailEventFragment(event.id)
            findNavController().navigate(action)
        }
        binding.rvUpcoming.layoutManager = LinearLayoutManager(requireContext())
        binding.rvUpcoming.adapter = adapter
    }

    private fun observeSearchResult() {
        viewModel.searchResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    adapter.submitList(result.data)
                    if (result.data.isEmpty()) {
                        Toast.makeText(requireContext(), "No events found", Toast.LENGTH_SHORT).show()
                    }
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Error: ${result.error}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
