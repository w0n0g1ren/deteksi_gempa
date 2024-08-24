package com.dicoding.bmkgproject.view.list

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.bmkgproject.adapter.GempaAdapter
import com.dicoding.bmkgproject.data.Gempa
import com.dicoding.bmkgproject.data.ListGempaResponse
import com.dicoding.bmkgproject.databinding.ActivityListBinding
import com.dicoding.bmkgproject.view.ViewModelFactory

class ListActivity : AppCompatActivity() {
    private val viewModel by viewModels<ListViewModel> {
        ViewModelFactory.getInstance()
    }
    private lateinit var binding: ActivityListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.listGempaTerkini()
        viewModel.listGempaTerkiniResponse.observe(this){
            setListGempa(it)
        }
    }

    private fun setListGempa(list: ListGempaResponse) {
        val layoutManager = LinearLayoutManager(this)
        binding.rvGempa.layoutManager = layoutManager
        binding.rvGempa.setHasFixedSize(true)
        val adapter = GempaAdapter(this)
        binding.rvGempa.adapter = adapter
        adapter?.submitList(list.infogempa?.gempa)
    }
}