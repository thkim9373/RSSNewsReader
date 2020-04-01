package com.hoony.rssnewsreader.data

data class RssItem(
    val title: String?,
    val link: String?,
    var imageUri: String?,
    var description: String?,
    var keyWordList: List<String>?
)