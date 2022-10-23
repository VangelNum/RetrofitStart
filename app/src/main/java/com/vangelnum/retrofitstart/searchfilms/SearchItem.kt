package com.vangelnum.retrofitstart.searchfilms

data class SearchItem(
    val results: List<Result>,
    val total: Int,
    val total_pages: Int
)