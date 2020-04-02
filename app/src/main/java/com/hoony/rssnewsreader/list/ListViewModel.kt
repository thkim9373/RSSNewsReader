package com.hoony.rssnewsreader.list

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hoony.rssnewsreader.data.News
import com.hoony.rssnewsreader.list.loader.NewsDataLoader
import com.hoony.rssnewsreader.list.loader.RssReader
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class ListViewModel(application: Application) : AndroidViewModel(application) {

    private val _isLoading = MutableLiveData<Boolean>()

    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _list: MutableLiveData<MutableList<News>> = MutableLiveData()

    val list: LiveData<MutableList<News>>
        get() = _list

    init {
        loadRssData()
    }

    fun loadRssData() {
        Log.d("Hoony", "loadRssData")
        viewModelScope.launch {
            _isLoading.postValue(true)
            try {
                val rssLoader = RssReader()
                val rssItemList = rssLoader.getNewsListFromRss()

                val newsDataLoader = NewsDataLoader()
                val newsList = newsDataLoader.getNewsData(rssItemList)
                _list.postValue(newsList)
            } catch (e: Exception) {
                _list.postValue(null)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
}
