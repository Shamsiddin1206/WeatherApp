package com.example.weatherapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.weatherapp.Adapters.ForecastDayAdapter
import com.example.weatherapp.Adapters.ForecastTimeAdapter
import com.example.weatherapp.DataClasses.ForecastDayData
import com.example.weatherapp.DataClasses.ForecastTimeData
import com.example.weatherapp.databinding.FragmentHomeBinding
import org.json.JSONObject
import java.util.Calendar
import kotlin.math.roundToInt

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HomeFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    lateinit var binding: FragmentHomeBinding
    lateinit var list: MutableList<ForecastTimeData>
    lateinit var DayList: MutableList<ForecastDayData>
    val url = "https://api.weatherapi.com/v1/forecast.json?key=e0ff523620584e678d873504230810&q=Uzbekistan&days=5&aqi=yes&alerts=no"
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val mainRequest = Volley.newRequestQueue(requireContext())
        list = mutableListOf()
        DayList = mutableListOf()
        val dayRecycler: RecyclerView = binding.nextDaysRecycler
        val timeRecycler: RecyclerView = binding.recyclerfortime
        val mainGradus: TextView = binding.currentGradus
        val mainImage: ImageView = binding.currentImage
        val mainDescription: TextView = binding.gradusDescription
        val wind: TextView = binding.windTezligi
        val temp: TextView = binding.temperatura
        val namlik: TextView = binding.namlik

        val calendar = Calendar.getInstance()
        binding.exactDate.text = calendar.get(Calendar.DAY_OF_MONTH).toString() + " " + calendar.get(Calendar.MONTH).toString() + " " + calendar.get(Calendar.YEAR).toString()
        val request = JsonObjectRequest(url, object : Response.Listener<JSONObject>{
            override fun onResponse(response: JSONObject?) {
                val a = response!!.getJSONObject("forecast").getJSONArray("forecastday").getJSONObject(0).getJSONArray("hour")
                val calendar = Calendar.getInstance()
                val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
                list.clear()
                for (i in 0 until a.length()){
                    val s = a.getJSONObject(i).getString("time")
                    val k: String = s[11].toString() + s[12].toString()
                    if (currentHour<k.toString().toInt()){
                        val degree = a.getJSONObject(i).getDouble("temp_c").roundToInt()
                        val hour = s[11].toString() + s[12].toString() + s[13] + "00"
                        val image = "https:" + a.getJSONObject(i).getJSONObject("condition").getString("icon")

                        val forecastTimeData = ForecastTimeData(hour, image, degree)
                        list.add(forecastTimeData)
                        timeRecycler.adapter = ForecastTimeAdapter(list)
                    }
                    Log.d("TAG", "onResponse: $k")
                }

            }
        }, object : Response.ErrorListener{
            override fun onErrorResponse(error: VolleyError?) {
                Log.d("TAG", "onErrorResponse: $error")
            }

        })
        mainRequest.add(request)

        val request2 = JsonObjectRequest(url, object : Response.Listener<JSONObject>{
            @SuppressLint("SetTextI18n")
            override fun onResponse(response: JSONObject?) {
                mainGradus.text = (response!!.getJSONObject("current").getDouble("temp_c").roundToInt().toString() + "°")
                mainDescription.text = response.getJSONObject("current").getJSONObject("condition").getString("text")
                wind.text = response.getJSONObject("current").getString("wind_kph")
                temp.text = (response.getJSONObject("current").getString("temp_c") + "°")
                namlik.text = (response.getJSONObject("current").getString("humidity") + "%")
                mainImage.load(("https:" + (response.getJSONObject("current").getJSONObject("condition").getString("icon"))))
            }

        }, object : Response.ErrorListener{
            override fun onErrorResponse(error: VolleyError?) {
                Log.d("TAG2", "onErrorResponse: $error")
            }

        })
        mainRequest.add(request2)

        val request3 = JsonObjectRequest(url, object : Response.Listener<JSONObject>{
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(response: JSONObject?) {
                val a = response!!.getJSONObject("forecast").getJSONArray("forecastday")
                for (i in 0 until a.length()){
                    val day = a.getJSONObject(i).getString("date")
                    val conditionText = a.getJSONObject(i).getJSONObject("day").getJSONObject("condition").getString("text")
                    val rasm = "https:" + a.getJSONObject(i).getJSONObject("day").getJSONObject("condition").getString("icon")
                    val gradusi = a.getJSONObject(i).getJSONObject("day").getDouble("avgtemp_c").roundToInt()

                    val forecastDayData = ForecastDayData(rasm, conditionText, day, gradusi)
                    DayList.add(forecastDayData)

                    dayRecycler.adapter = ForecastDayAdapter(DayList, object : ForecastDayAdapter.OnPressed{
                        @SuppressLint("NotifyDataSetChanged")
                        override fun onPressed(forecastDayData: ForecastDayData) {
                            val response4 = JsonObjectRequest(url, object : Response.Listener<JSONObject>{
                                override fun onResponse(response: JSONObject?) {
                                    val b = response!!.getJSONObject("forecast").getJSONArray("forecastday")
                                    for (k in 0 until b.length()){
                                        val f = b.getJSONObject(k).getJSONArray("hour")
                                        if (b.getJSONObject(k).getString("date")==forecastDayData.time){
                                            val hournow = calendar.get(Calendar.HOUR_OF_DAY)
                                            list.clear()
                                            if ((forecastDayData.time[8].toString() + forecastDayData.time[9]) == calendar.get(Calendar.DAY_OF_MONTH).toString()){
                                                binding.TodayData.text = "Today"
                                                for (j in 0 until f.length()){
                                                    if (hournow<(f.getJSONObject(j).getString("time")[11].toString() + f.getJSONObject(j).getString("time")[12].toString()).toInt()){
                                                        val gradysi: Int = f.getJSONObject(j).getDouble("temp_c").roundToInt()
                                                        val soati: String = (f.getJSONObject(j).getString("time")[11]+f.getJSONObject(j).getString("time")[12].toString()+f.getJSONObject(j).getString("time")[13].toString() + "00")
                                                        val rasmi: String = "https:"+f.getJSONObject(j).getJSONObject("condition").getString("icon").toString()

                                                        val forecastTimeData = ForecastTimeData(hour = soati, degree = gradysi, rasm = rasmi)
                                                        list.add(forecastTimeData)
                                                        timeRecycler.adapter = ForecastTimeAdapter(list)
                                                    }
                                                }
                                            }else{
                                                binding.TodayData.text = b.getJSONObject(k).getString("date")
                                                for (j in 0 until f.length()){
                                                    val soati = (f.getJSONObject(j).getString("time")[11]+f.getJSONObject(j).getString("time")[12].toString()+f.getJSONObject(j).getString("time")[13].toString() + "00")
                                                    val rasmi = "https:"+f.getJSONObject(j).getJSONObject("condition").getString("icon").toString()
                                                    val gradysi: Int = f.getJSONObject(j).getDouble("temp_c").roundToInt()

                                                    val forecastTimeData = ForecastTimeData(hour = soati, degree = gradysi, rasm = rasmi)
                                                    list.add(forecastTimeData)
                                                    timeRecycler.adapter = ForecastTimeAdapter(list)
                                                }
                                            }
                                        }
                                    }
                                }

                            }, object : Response.ErrorListener{
                                override fun onErrorResponse(error: VolleyError?) {
                                    Log.d("TAG4", "onErrorResponse: $error")
                                }

                            })
                            mainRequest.add(response4)
                            dayRecycler.adapter!!.notifyDataSetChanged()
                        }

                    })
                    dayRecycler.adapter!!.notifyDataSetChanged()
                }
            }

        }, object : Response.ErrorListener{
            override fun onErrorResponse(error: VolleyError?) {
                Log.d("TAG3", "onErrorResponse: $error")
            }

        })
        mainRequest.add(request3)
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}