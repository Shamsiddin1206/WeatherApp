package com.example.weatherapp.Adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.weatherapp.DataClasses.ForecastTimeData
import com.example.weatherapp.R

class ForecastTimeAdapter(var list: MutableList<ForecastTimeData>): RecyclerView.Adapter<ForecastTimeAdapter.MyViewHolder>() {
    class MyViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var imag = view.findViewById<ImageView>(R.id.timeItemImage)
        var gradusi = view.findViewById<TextView>(R.id.timeItemGradus)
        var soat = view.findViewById<TextView>(R.id.timeItemTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.time_item, parent, false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val a = list[position]
        holder.gradusi.text = (a.degree.toString()+"°")
        holder.soat.text = a.hour
        holder.imag.load(a.rasm)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}