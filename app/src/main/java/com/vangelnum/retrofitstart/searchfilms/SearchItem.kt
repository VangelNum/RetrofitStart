package com.vangelnum.retrofitstart.searchfilms

data class SearchItem(
    var results: List<Result>,
    val total: Int,
    val total_pages: Int
)