package com.hoony.rssnewsreader.list

import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.hoony.rssnewsreader.databinding.ItemNewsBinding

class NewsItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private var binding: ItemNewsBinding? = null

    init {
        binding = DataBindingUtil.bind(itemView)
    }

    fun getBinding(): ItemNewsBinding? {
        return binding
    }
}