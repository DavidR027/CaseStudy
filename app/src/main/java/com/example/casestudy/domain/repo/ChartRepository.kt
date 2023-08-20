package com.example.casestudy.domain.repo

import android.content.Context
import com.example.casestudy.data.dc.ChartData
import com.example.casestudy.domain.adapter.ChartDataAdapter
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class ChartRepository(private val context: Context) {
    fun getChartData(): List<ChartData> {
        val inputStream = context.assets.open("chart_data.json")
        val json = inputStream.bufferedReader().use { it.readText() }
        val moshi = Moshi.Builder()
            .add(ChartDataAdapter())
            .addLast(KotlinJsonAdapterFactory())
            .build()
        val type = Types.newParameterizedType(List::class.java, ChartData::class.java)
        val adapter: JsonAdapter<List<ChartData>> = moshi.adapter(type)
        return adapter.fromJson(json)!!
    }
}