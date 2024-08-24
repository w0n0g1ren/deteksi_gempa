package com.dicoding.bmkgproject.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.bmkgproject.data.Gempa
import com.dicoding.bmkgproject.data.GempaItem
import com.dicoding.bmkgproject.databinding.RvItemBinding

class GempaAdapter(private val context: Context): ListAdapter<GempaItem,
        GempaAdapter.ViewHolder>(DIFF_CALLBACK) {

    class ViewHolder(private val binding: RvItemBinding, private val context: Context) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GempaItem){
            binding.tanggal.text = item.tanggal
            binding.jam.text = item.jam
            binding.magnitudo.text = "${item.magnitude} R"
            binding.pusatGempa.text = item.wilayah
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<GempaItem>() {
            override fun areItemsTheSame(oldItem: GempaItem,
                                         newItem: GempaItem): Boolean {
                return oldItem == newItem
            }
            override fun areContentsTheSame(oldItem: GempaItem, newItem:
            GempaItem): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GempaAdapter.ViewHolder {
        val binding = RvItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,false)
        val context = context
        return ViewHolder(binding,context)
    }

    override fun onBindViewHolder(holder: GempaAdapter.ViewHolder, position: Int) {
        val bind = getItem(position)
        holder.bind(bind)
    }
}