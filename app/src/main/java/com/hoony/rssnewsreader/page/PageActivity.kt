package com.hoony.rssnewsreader.page

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.hoony.rssnewsreader.R
import com.hoony.rssnewsreader.databinding.ActivityPageBinding

class PageActivity : AppCompatActivity() {
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding =
            DataBindingUtil.setContentView<ActivityPageBinding>(this, R.layout.activity_page)

        val title = intent.getStringExtra("title")
        binding.toolbar.title = title
        setSupportActionBar(binding.toolbar)

        if (intent.getStringArrayListExtra("keyword_list") != null) {
            val keyWordList = intent.getStringArrayListExtra("keyword_list")
            val keyWordTextViewArray =
                arrayListOf(binding.tvKeyword1, binding.tvKeyword2, binding.tvKeyword3)
            for (i in 0..2) {
                if (i < keyWordList!!.size) {
                    keyWordTextViewArray[i].visibility = View.VISIBLE
                    keyWordTextViewArray[i].text = keyWordList[i]
                } else {
                    keyWordTextViewArray[i].visibility = View.GONE
                }
            }
        } else {
            binding.clKeywordLayout.visibility = View.GONE
        }

        val url = intent.getStringExtra("url")

        val webView = binding.wbNews

        val webViewSetting = webView.settings
        webViewSetting.javaScriptEnabled = true
        webViewSetting.loadWithOverviewMode = true

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view?.loadUrl(url)
                return true
            }
        }
        webView.loadUrl(url)
    }
}