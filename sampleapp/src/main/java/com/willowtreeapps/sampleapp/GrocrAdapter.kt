package com.willowtreeapps.sampleapp

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

class GrocrAdapter(private val items: List<Pair<String, Int>>)
    : RecyclerView.Adapter<GrocrItemViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GrocrItemViewHolder {
        return GrocrItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_gorcr_item, parent, false))
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: GrocrItemViewHolder, position: Int) {
        holder.bind(items[position])
    }
}