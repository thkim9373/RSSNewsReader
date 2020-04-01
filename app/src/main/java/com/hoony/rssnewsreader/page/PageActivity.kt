package com.hoony.rssnewsreader.page

import android.annotation.SuppressLint
import android.os.Bundle
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