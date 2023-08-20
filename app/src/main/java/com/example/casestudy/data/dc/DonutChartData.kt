package com.example.casestudy.data.dc

data class DonutChartData(
    override val type: String,
    val data: List<DonutChartDetailData>
) : ChartData

data class DonutChartDetailData(
    val label: String,
    val percentage: String,
    val data: List<TransactionData>
)

data class TransactionData(
    val trx_date: String,
    val nominal: Int
)