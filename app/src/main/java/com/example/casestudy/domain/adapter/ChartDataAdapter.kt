package com.example.casestudy.domain.adapter

import com.example.casestudy.data.dc.ChartData
import com.example.casestudy.data.dc.DonutChartData
import com.example.casestudy.data.dc.LineChartData
import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class ChartDataAdapter {
    @FromJson
    fun fromJson(reader: JsonReader): ChartData {
        val map = reader.readJsonValue() as Map<String, Any>
        val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
        return when (map["type"] as String) {
            "donutChart" -> moshi.adapter(DonutChartData::class.java).fromJsonValue(map)!!
            "lineChart" -> moshi.adapter(LineChartData::class.java).fromJsonValue(map)!!
            else -> throw IllegalArgumentException("Invalid type")
        }
    }

    @ToJson
    fun toJson(writer: JsonWriter, value: ChartData?) {
        val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
        when (value) {
            is DonutChartData -> {
                val jsonAdapter = moshi.adapter(DonutChartData::class.java)
                jsonAdapter.toJson(value)
            }
            is LineChartData -> {
                val jsonAdapter = moshi.adapter(LineChartData::class.java)
                jsonAdapter.toJson(value)
            }
        }
    }
}
