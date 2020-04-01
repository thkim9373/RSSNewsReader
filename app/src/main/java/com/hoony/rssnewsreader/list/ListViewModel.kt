package com.hoony.rssnewsreader.list

import android.os.Build
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hoony.rssnewsreader.data.RssItem
import kotlinx.coroutines.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.util.*

class ListViewModel : ViewModel(), RssLoadTask.RssLoadingCallback {

    var list: MutableLiveData<MutableList<RssItem>> = MutableLiveData()

    init {
        loadRssData()
    }

    fun loadRssData() {
        val rssLoadTask = RssLoadTask(this)
        rssLoadTask.loadDocument()
    }

    override fun rssLoadingSuccess(rssItemList: MutableList<RssItem>) {
        runBlocking {
            setList(rssItemList)
        }
    }

    private suspend fun setList(rssItemList: MutableList<RssItem>) {
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
                item.await()
            }
        }

        job.join()

        rssItemList.removeAll(exceptionItemList)

        list.value = rssItemList
    }

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
}
