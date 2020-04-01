package com.hoony.rssnewsreader.list

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
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

        setListener()
        setView()
        setObserve()
    }

    private fun setListener() {
        binding?.swipeRefreshLayout?.setOnRefreshListener(this)
    }

    private fun setView() {
        binding?.rvNews?.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding?.rvNews?.addItemDecoration(
            DividerItemDecoration(
                this,
                LinearLayoutManager.VERTICAL
            )
        )
    }

    private fun setObserve() {
        viewModel?.let {
            it.list.observe(this,
                Observer {
                    Log.d("Hoony", "observe list")
                    binding?.rvNews?.adapter = NewsAdapter(viewModel?.list?.value!!)
                    binding?.swipeRefreshLayout?.isRefreshing = false
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
