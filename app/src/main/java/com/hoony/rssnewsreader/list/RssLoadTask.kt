package com.hoony.rssnewsreader.list

import android.os.Handler
import android.os.Looper
import android.util.Xml
import com.hoony.rssnewsreader.data.RssItem
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.net.URL
import java.util.concurrent.Callable
import java.util.concurrent.Executor
import java.util.concurrent.Executors


// xml parser example : https://developer.android.com/training/basics/network-ops/xml#kotlin
class RssLoadTask(private val callback: RssLoadingCallback) : Callable<MutableList<RssItem>> {

    private val executor: Executor = Executors.newSingleThreadExecutor()
    private val handler = Handler(Looper.getMainLooper())

    private val ns: String? = null
    private val url = "https://news.google.com/rss?hl=ko&gl=KR&ceid=KR:ko"

    override fun call(): MutableList<RssItem> {
        val url = URL(this.url)
        val inputStream = url.openStream()
        lateinit var rssItemList: MutableList<RssItem>

        inputStream.use {
            val parser: XmlPullParser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(it, null)
            parser.nextTag()
            rssItemList = readRss(parser)
        }

        return rssItemList
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readRss(parser: XmlPullParser): MutableList<RssItem> {
        val rssItemList = mutableListOf<RssItem>()

        parser.require(XmlPullParser.START_TAG, ns, "rss")
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            // Starts by looking for the entry tag
            if (parser.name == "channel") {
                rssItemList.addAll(readChannel(parser))
            } else {
                skip(parser)
            }
        }
        return rssItemList
    }

    // Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them off
    // to their respective "read" methods for processing. Otherwise, skips the tag.
    @Throws(XmlPullParserException::class, IOException::class)
    private fun readChannel(parser: XmlPullParser): List<RssItem> {
        val rssItemList = mutableListOf<RssItem>()

        parser.require(XmlPullParser.START_TAG, ns, "channel")
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            if (parser.name == "item") {
                rssItemList.add(readItem(parser))
            } else {
                skip(parser)
            }
        }
        return rssItemList
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readItem(parser: XmlPullParser): RssItem {
        parser.require(XmlPullParser.START_TAG, ns, "item")
        var title: String? = null
        var link: String? = null
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            when (parser.name) {
                "title" -> title = readTitle(parser)
                "link" -> link = readLink(parser)
                else -> skip(parser)
            }
        }
        return RssItem(title, link, null)
    }

    // Processes title tags in the feed.
    @Throws(IOException::class, XmlPullParserException::class)
    private fun readTitle(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, ns, "title")
        val title = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, "title")
        return title
    }

    // Processes link tags in the feed.
    @Throws(IOException::class, XmlPullParserException::class)
    private fun readLink(parser: XmlPullParser): String {
//        var link = ""
//        parser.require(XmlPullParser.START_TAG, ns, "link")
//        val tag = parser.name
//        val relType = parser.getAttributeValue(null, "rel")
//        if (tag == "link") {
//            if (relType == "alternate") {
//                link = parser.getAttributeValue(null, "href")
//                parser.nextTag()
//            }
//        }
//        parser.require(XmlPullParser.END_TAG, ns, "link")
        parser.require(XmlPullParser.START_TAG, ns, "link")
        val link = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, "link")
        return link
    }

    // For the tags title and summary, extracts their text values.
    @Throws(IOException::class, XmlPullParserException::class)
    private fun readText(parser: XmlPullParser): String {
        var result = ""
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.text
            parser.nextTag()
        }
        return result
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun skip(parser: XmlPullParser) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            throw IllegalStateException()
        }
        var depth = 1
        while (depth != 0) {
            when (parser.next()) {
                XmlPullParser.END_TAG -> depth--
                XmlPullParser.START_TAG -> depth++
            }
        }
    }

    fun loadDocument() {
        executor.execute {
            val result: List<RssItem>
            try {
                result = call()
                handler.post {
                    callback.rssLoadingSuccess(result)
                }
            } catch (e: Exception) {
                handler.post {
                    callback.rssLoadingFail(e)
                }
            }
        }
    }

    interface RssLoadingCallback {
        fun rssLoadingSuccess(rssItemList: MutableList<RssItem>)
        fun rssLoadingFail(e: Exception)
    }
}