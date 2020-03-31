package com.hoony.rssnewsreader.list

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.hoony.rssnewsreader.data.RssItem
import org.jsoup.Jsoup
import java.util.concurrent.Callable
import java.util.concurrent.Executor
import java.util.concurrent.Executors

// xml parser example : https://developer.android.com/training/basics/network-ops/xml#kotlin
class NewsDataLoaderTask(
    private val position: Int,
    private val rssItem: RssItem,
    private val callback: NewsDataLoaderCallback
) : Callable<RssItem> {

    private val executor: Executor = Executors.newSingleThreadExecutor()
    private val handler = Handler(Looper.getMainLooper())

    override fun call(): RssItem {
        Log.d("NewsDataLoadTask", rssItem.title!!)
        Log.d("NewsDataLoadTask", rssItem.link!!)
        val doc = Jsoup.connect(rssItem.link).get()
        val e = doc.select("meta[property=og:image]")
        val f = e.first()
        val attr = f?.attr("content")
        rssItem.imageUri = attr

        return rssItem
    }

    fun loadNewsData() {
        executor.execute {
            val result: RssItem
            try {
                result = call()
                handler.post {
                    callback.newsDataLoaderSuccess(position, result)
                }
            } catch (e: Exception) {
                handler.post {
                    callback.newsDataLoaderFail(e)
                }
            }
        }
    }

    interface NewsDataLoaderCallback {
        fun newsDataLoaderSuccess(position: Int, rssItem: RssItem)
        fun newsDataLoaderFail(e: Exception)
    }
}