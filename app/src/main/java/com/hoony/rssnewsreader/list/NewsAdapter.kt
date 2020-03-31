package com.hoony.rssnewsreader.list

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.hoony.rssnewsreader.R
import com.hoony.rssnewsreader.data.RssItem

class NewsAdapter(private var list: List<RssItem>) : RecyclerView.Adapter<NewsItemHolder>() {

    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsItemHolder {
        context = parent.context
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
        return NewsItemHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: NewsItemHolder, position: Int) {
        val binding = holder.getBinding()
        val rssItem = list[position]
        binding?.tvTitle?.text = rssItem.title
        if (rssItem.imageUri != null) {
            Log.d("NewsAdapter", "image Uri : " + rssItem.imageUri!!)
            binding?.ivThumbnail?.visibility = View.VISIBLE
            Glide.with(context)
                .load(Uri.parse(rssItem.imageUri!!))
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(binding?.ivThumbnail!!)
        } else {
            binding?.ivThumbnail?.visibility = View.GONE
        }
    }
}
