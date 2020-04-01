package com.hoony.rssnewsreader.list

import android.os.Build
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.hoony.rssnewsreader.data.RssItem
import kotlinx.coroutines.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.util.*
import kotlin.collections.set

class ListViewModel : ViewModel(), RssLoadTask.RssLoadingCallback {

    //        var list: LiveData<MutableList<RssItem>> = MutableLiveData()
    var list = liveData {
        Log.d("Hoony", "withContext")
        withContext(viewModelScope.coroutineContext + Dispatchers.IO) {
            Log.d("Hoony", "Create loader")
            val loader = RssLoader()
            Log.d("Hoony", "Get rss item list")
            val rssItemList = loader.getNewsListFromRss()
            Log.d("Hoony", "emit")
            emit(getNewsData(rssItemList))
        }
    }


    init {
        loadRssData()
    }

    fun loadRssData() {
//        val rssLoadTask = RssLoadTask(this)
//        rssLoadTask.loadDocument()
        Log.d("Hoony", "loadRssData")

    }

    override fun rssLoadingSuccess(rssItemList: MutableList<RssItem>) {
        Log.d("Hoony", "rssLoadingSuccess")
        list = liveData {
            Log.d("Hoony", "withContext")
            withContext(viewModelScope.coroutineContext + Dispatchers.IO) {
                Log.d("Hoony", "emit")
                emit(getNewsData(rssItemList))
            }
        }
//        runBlocking {
//            setList(rssItemList)
//        }
    }

    private suspend fun getNewsData(rssItemList: MutableList<RssItem>): MutableList<RssItem> {
        val exceptionItemList: MutableList<RssItem> = arrayListOf()

        val deferredArray: MutableList<Deferred<Any>> = arrayListOf()

        for (item in rssItemList) {
            deferredArray.add(CoroutineScope(Dispatchers.IO).async {
                try {
                    val doc = getDocFromNewsLink(item.link!!)
                    item.imageUri = getImageUri(doc)
                    item.description = getDescription(doc)
                    item.keyWordList = getKeywords(item.description!!)
                } catch (e: Exception) {
                    exceptionItemList.add(item)
                }
            })
        }
        for (item in deferredArray) {
            Log.d("Hoony", "item.await()")
            item.await()
        }
        rssItemList.removeAll(exceptionItemList)

        return rssItemList
    }

    private suspend fun getList(rssItemList: MutableList<RssItem>) {
        val exceptionItemList: MutableList<RssItem> = arrayListOf()

        val job = CoroutineScope(Dispatchers.IO).launch {

            val deferredArray: MutableList<Deferred<Any>> = arrayListOf()

            for (item in rssItemList) {
                deferredArray.add(async {
                    try {
                        val doc = getDocFromNewsLink(item.link!!)
                        item.imageUri = getImageUri(doc)
                        item.description = getDescription(doc)
                        item.keyWordList = getKeywords(item.description!!)
                    } catch (e: Exception) {
                        exceptionItemList.add(item)
                    }
                })
            }
            for (item in deferredArray) {
                Log.d("Hoony", "item.await()")
                item.await()
            }
            Log.d("Hoony", "item.await() all finish")
        }

//        Log.d("Hoony", "job.join")
//        job.join()

        rssItemList.removeAll(exceptionItemList)
    }

//    private suspend fun setList(rssItemList: MutableList<RssItem>) {
//        val exceptionItemList: MutableList<RssItem> = arrayListOf()
//
//        val job = CoroutineScope(Dispatchers.IO).launch {
//
//            val deferredArray: MutableList<Deferred<Any>> = arrayListOf()
//
//            for (item in rssItemList) {
//                deferredArray.add(async {
//                    try {
//                        val doc = getDocFromNewsLink(item.link!!)
//                        item.imageUri = getImageUri(doc)
//                        item.description = getDescription(doc)
//                        item.keyWordList = getKeywords(item.description!!)
//                    } catch (e: Exception) {
//                        exceptionItemList.add(item)
//                    }
//                })
//            }
//            for (item in deferredArray) {
//                Log.d("Hoony", "item.await()")
//                item.await()
//            }
//            Log.d("Hoony", "item.await() all finish")
//        }
//
////        Log.d("Hoony", "job.join")
////        job.join()
//
//        rssItemList.removeAll(exceptionItemList)
//
//        Log.d("Hoony", "set value")
//        list.value = rssItemList
//    }

    private fun getDocFromNewsLink(newsLink: String): Document {
        return Jsoup.connect(newsLink).get()
    }

    private fun getImageUri(doc: Document): String? {
        return doc.select("meta[property=og:image]").first()?.attr("content")
    }

    private fun getDescription(doc: Document): String? {
        return doc.select("meta[property=og:description]").first()?.attr("content")
    }

    private fun getKeywords(description: String): List<String> {
        val keyWordList = arrayListOf<String>()

        val hashMap = hashMapOf<String, Int>()

        val tokenizer = StringTokenizer(description)

        while (tokenizer.hasMoreTokens()) {
            val token = tokenizer.nextToken()
            if (token.length >= 2) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    hashMap[token] = hashMap.getOrDefault(token, 0) + 1
                } else {
                    val count: Int = if (hashMap.containsKey(token)) {
                        hashMap[token]!! + 1
                    } else {
                        1
                    }
                    hashMap[token] = count
                }
            }
        }

        val sortedList = hashMap.toList().sortedWith(kotlin.Comparator { o1, o2 ->
            if (o1.second != o2.second) {
                o2.second - o1.second
            } else {
                o1.first.compareTo(o2.first)
            }
        })

        val range: Int = if (sortedList.size >= 3) {
            2
        } else {
            sortedList.size
        }

        for (i in 0..range) {
            keyWordList.add(sortedList[i].first)
        }

        return keyWordList
    }

    override fun rssLoadingFail(e: Exception) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}
