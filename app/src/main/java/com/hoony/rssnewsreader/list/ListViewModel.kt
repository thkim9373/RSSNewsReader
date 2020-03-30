package com.hoony.rssnewsreader.list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hoony.rssnewsreader.data.RssItem

class ListViewModel : ViewModel(), RssLoadTask.RssLoadingCallback {

    var list: MutableLiveData<List<String>> = MutableLiveData()

    init {
        loadRssData()
    }

    fun loadRssData() {
        val rssLoadTask = RssLoadTask(this)
        rssLoadTask.loadDocument()
    }

    override fun rssLoadingSuccess(rssItemList: List<RssItem>) {
        val titleList = ArrayList<String>()
        for (news in rssItemList) {
            titleList.add(news.title!!)
        }
        this.list.value = titleList
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
}
