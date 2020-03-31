package com.hoony.rssnewsreader.list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hoony.rssnewsreader.data.RssItem
import kotlinx.coroutines.*

class ListViewModel : ViewModel(), RssLoadTask.RssLoadingCallback,
    NewsDataLoadTask.NewsDataLoadingCallback, NewsDataLoaderTask.NewsDataLoaderCallback {

    var list: MutableLiveData<MutableList<RssItem>> = MutableLiveData()

    init {
        loadRssData()
    }

    fun loadRssData() {
        val rssLoadTask = RssLoadTask(this)
        rssLoadTask.loadDocument()
    }


    private var count: Int = 0

    override fun rssLoadingSuccess(rssItemList: MutableList<RssItem>) {
//        val newsDataLoader = NewsDataLoadTask(rssItemList, this)
//        newsDataLoader.loadNewsData()

        runBlocking {
            val job = List(rssItemList.size) {
                launch {

                }
            }
        }

        this.list.value = rssItemList
        for (i in rssItemList.indices) {
            val newsDataLoaderTask = NewsDataLoaderTask(i, rssItemList[i], this)
            newsDataLoaderTask.loadNewsData()
        }
    }

//    override fun rssLoadingSuccess(document: Document) {
//        val newsList = ArrayList<String>()
//
//        val nodeList = document.getElementsByTagName("item")
//
//        for (i in 0 until nodeList.length) {
//            val node = nodeList.item(i)
//            val element = node as Element
//
//            val titleNodeList = element.getElementsByTagName("title")
//            val title = titleNodeList.item(0).childNodes.item(0).nodeValue
//            newsList.add(title)
//        }
//
//        this.list.value = newsList
//    }

    override fun rssLoadingFail(e: Exception) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun newsDataLoadingSuccess(rssItemList: List<RssItem>) {
//        this.list.value = rssItemList
    }

    override fun newsDataLoadingFail(e: Exception) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun newsDataLoaderSuccess(position: Int, rssItem: RssItem) {
        this.list.value?.set(position, rssItem)
    }

    override fun newsDataLoaderFail(e: Exception) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
