package com.hoony.rssnewsreader.list

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.hoony.rssnewsreader.R
import com.hoony.rssnewsreader.data.RssItem
import com.hoony.rssnewsreader.page.PageActivity

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

        binding?.let {
            it.tvTitle.text = rssItem.title
            it.tvDescription.text = rssItem.description
            if (rssItem.imageUri != null) {
                it.ivThumbnail.visibility = View.VISIBLE
                Glide.with(context)
                    .load(Uri.parse(rssItem.imageUri!!))
                    .centerCrop()
                    .thumbnail(0.2f)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(it.ivThumbnail)
            } else {
                it.ivThumbnail.visibility = View.GONE
            }

            val keyWordList = if (rssItem.keyWordList != null) {
                rssItem.keyWordList
            } else {
                arrayListOf()
            }
            val keyWordTextViewArray =
                arrayListOf(it.tvKeyword1, binding.tvKeyword2, binding.tvKeyword3)
            for (i in 0..2) {
                if (i <
                    keyWordList!!.size
                ) {
                    keyWordTextViewArray[i].visibility = View.VISIBLE
                    keyWordTextViewArray[i].text = keyWordList[i]
                } else {
                    keyWordTextViewArray[i].visibility = View.GONE
                }
            }

            it.clContainer.setOnClickListener {
                val intent = Intent(context, PageActivity::class.java)
                intent.putExtra("url", rssItem.link)
                context.startActivity(intent)
            }
        }
    }
}
