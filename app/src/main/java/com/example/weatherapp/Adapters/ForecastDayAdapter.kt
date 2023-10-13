package com.example.weatherapp.Adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.weatherapp.DataClasses.ForecastDayData
import com.example.weatherapp.R

class ForecastDayAdapter(var list: MutableList<ForecastDayData>, val onPressed: OnPressed): RecyclerView.Adapter<ForecastDayAdapter.MyViewHolder>() {
    class MyViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val data = view.findViewById<TextView>(R.id.dayItemData)
        val info = view.findViewById<TextView>(R.id.dayItemCondition)
        val degree = view.findViewById<TextView>(R.id.dayItemDegree)
        var rasm = view.findViewById<ImageView>(R.id.dayItemImage)
        val loyaut = view.findViewById<RelativeLayout>(R.id.dayRelative)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.day_item, parent, false))
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    override fun onBindViewHolder(holder: ForecastDayAdapter.MyViewHolder, position: Int) {
        val a = list[position]
        holder.data.text = a.time
        holder.degree.text = a.gradus.toString() + "°"
        holder.info.text = a.condition
        holder.rasm.load(a.image)

        var s: Int = 0
        for (i in 0 until list.size){
            if (list[i].status){
                s++
            }
        }
        if (s==0){
            if (position==0){
                holder.loyaut.setBackgroundResource(R.drawable.borderforselectedtime)
            }
        }else{
            if (a.status){
                holder.loyaut.setBackgroundResource(R.drawable.borderforselectedtime)
            }else{
                holder.loyaut.setBackgroundResource(R.drawable.borderfortimecard)
            }
        }

        holder.itemView.setOnClickListener {
            if (!a.status){
                a.status = true
                holder.loyaut.setBackgroundResource(R.drawable.borderforselectedtime)
                for (i in 0 until list.size){
                    if (i!=position){
                        list[i].status = false
                    }
                }
                notifyDataSetChanged()
            }
            onPressed.onPressed(a)

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface OnPressed{
        fun onPressed(forecastDayData: ForecastDayData)
    }
}