package com.example.mvvm_pattern_2021.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mvvm_pattern_2021.R
import com.example.mvvm_pattern_2021.beans.Fruit

class FruitAdapter(private val fruits: List<Fruit>) : RecyclerView.Adapter<FruitAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.fruit_item, parent, false)

        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(fruit = fruits[position])
    }

    override fun getItemCount(): Int {
        return fruits.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var tvFruitName: TextView = itemView.findViewById(R.id.tv_fruit_name)

        fun bind(fruit: Fruit){
            tvFruitName.text = fruit.name
        }
    }

}