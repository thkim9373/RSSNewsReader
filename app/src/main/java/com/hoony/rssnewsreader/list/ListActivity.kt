package com.hoony.rssnewsreader.list

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.hoony.rssnewsreader.R
import com.hoony.rssnewsreader.databinding.ActivityMainBinding

class ListActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {

    private var binding: ActivityMainBinding? = null
    private var viewModel: ListViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = application!!.let {
            ViewModelProvider(viewModelStore, ViewModelProvider.AndroidViewModelFactory(it)).get(
                ListViewModel::class.java
            )
        }

        setView()
        setObserve()
    }

    private fun setView() {
        binding?.rvNews?.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
    }

    private fun setObserve() {
        viewModel?.let {
            it.list.observe(this,
                Observer {
                    binding?.rvNews?.adapter = NewsAdapter(viewModel?.list?.value!!)
                })
        }
    }

    override fun onRefresh() {
        viewModel?.loadRssData()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding?.rvNews?.adapter = null
    }
}
