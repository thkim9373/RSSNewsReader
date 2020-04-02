package com.hoony.rssnewsreader.data

data class News(
    val title: String,
    val link: String,
    var imageUri: String?,
    var description: String?,
    var keyWordList: List<String>?
)