package com.example.blusq.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.blusq.R
import com.example.blusq.data.local.entity.EventEntity
import com.example.blusq.databinding.ItemAllFragmentBinding

class EventAdapter(private val onItemClick: (EventEntity) -> Unit) : ListAdapter<EventEntity, EventAdapter.ListViewHolder>(DIFF_CALLBACK) {

    class ListViewHolder(private val binding: ItemAllFragmentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: EventEntity, onItemClick: (EventEntity) -> Unit) {
            binding.tvTitle.text = event.name
            binding.tvSubtitle.text = event.summary
            Glide.with(binding.root.context)
                .load(event.imageLogo)
                .placeholder(R.drawable.ic_loading)
                .error(R.drawable.ic_error)
                .into(binding.ivGambar)
            
            binding.root.setOnClickListener {
                onItemClick(event)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemAllFragmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val event = getItem(position)
        holder.bind(event, onItemClick)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<EventEntity>() {
            override fun areItemsTheSame(oldItem: EventEntity, newItem: EventEntity): Boolean {
                return oldItem.id == newItem.id
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: EventEntity, newItem: EventEntity): Boolean {
                return oldItem == newItem
            }
        }
    }
}