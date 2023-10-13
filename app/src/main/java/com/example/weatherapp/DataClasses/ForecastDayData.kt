package com.example.weatherapp.DataClasses

data class ForecastDayData(
    var image: String,
    var condition: String,
    var time: String,
    var gradus: Int,
    var status: Boolean = false
)
