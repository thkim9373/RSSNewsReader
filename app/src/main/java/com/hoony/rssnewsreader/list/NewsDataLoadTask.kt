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
class NewsDataLoadTask(
    private val newsList: List<RssItem>,
    private val callback: NewsDataLoadingCallback
) : Callable<List<RssItem>> {

    private val executor: Executor = Executors.newSingleThreadExecutor()
    private val handler = Handler(Looper.getMainLooper())

    override fun call(): List<RssItem> {
        for (rssItem in newsList) {
            Log.d("NewsDataLoadTask", rssItem.title!!)
            Log.d("NewsDataLoadTask", rssItem.link!!)
            val doc = Jsoup.connect(rssItem.link).get()
            val e = doc.select("meta[property=og:image]")
            val f = e.first()
            val attr = f?.attr("content")
            rssItem.imageUri = attr
        }
        return newsList
    }

    fun loadNewsData() {
        executor.execute {
            val result: List<RssItem>
            try {
                result = call()
                handler.post {
                    callback.newsDataLoadingSuccess(result)
                }
            } catch (e: Exception) {
                handler.post {
                    callback.newsDataLoadingFail(e)
                }
            }
        }
    }

    interface NewsDataLoadingCallback {
        fun newsDataLoadingSuccess(rssItemList: List<RssItem>)
        fun newsDataLoadingFail(e: Exception)
    }
}