package com.example.casestudy.data.dc

data class LineChartData(
    override val type: String,
    val data: LineChartDetailData
) : ChartData

data class LineChartDetailData(
    val month: List<Int>
)