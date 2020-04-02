package com.hoony.rssnewsreader.list.loader

import android.os.Build
import android.util.Log
import com.hoony.rssnewsreader.data.News
import com.hoony.rssnewsreader.data.RssItem
import kotlinx.coroutines.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.util.*

class NewsDataLoader {

    @ExperimentalCoroutinesApi
    suspend fun getNewsData(rssItemList: MutableList<RssItem>): MutableList<News> =
        withContext(Dispatchers.IO) {
            val newsList: MutableList<News> = arrayListOf()

            val deferredArray: MutableList<Deferred<News?>> = arrayListOf()
            for (item in rssItemList) {
                deferredArray.add(CoroutineScope(Dispatchers.IO).async {
                    val news: News?
                    news = try {
                        val doc = getDocFromNewsLink(item.link)
                        val imageUri = getImageUri(doc)
                        val description = getDescription(doc)
                        val keyWordList = if (description != null) {
                            getKeywords(description)
                        } else {
                            null
                        }
                        News(item.title, item.link, imageUri, description, keyWordList)
                    } catch (e: Exception) {
                        null
                    }
                    news
                })
            }
            for (item in deferredArray) {
                Log.d("Hoony", "item.await()")
                item.await()
                val news = item.getCompleted()
                if (news != null) {
                    newsList.add(news)
                }
            }

            newsList
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

        //  특수문자 제거
        val re = Regex("[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]")
        val tokenizer = StringTokenizer(re.replace(description, " "))

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
}