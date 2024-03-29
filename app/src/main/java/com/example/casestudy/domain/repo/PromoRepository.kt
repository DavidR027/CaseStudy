package com.example.casestudy.domain.repo


import com.example.casestudy.data.dc.Promo
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.Request

fun fetchPromos(): List<Promo> {
    val client = OkHttpClient()
    val request = Request.Builder()
        .url("https://content.digi46.id/promos")
        .build()
    val response = client.newCall(request).execute()
    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    val type = Types.newParameterizedType(List::class.java, Promo::class.java)
    val adapter: JsonAdapter<List<Promo>> = moshi.adapter(type)
    return adapter.fromJson(response.body!!.string())!!
}