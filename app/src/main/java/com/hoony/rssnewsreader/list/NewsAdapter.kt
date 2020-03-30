package com.hoony.rssnewsreader.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hoony.rssnewsreader.R

class NewsAdapter(private var list: List<String>) : RecyclerView.Adapter<NewsItemHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsItemHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
        return NewsItemHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: NewsItemHolder, position: Int) {
        val binding = holder.getBinding()
        binding?.tvTitle?.text = list[position]
    }
}
