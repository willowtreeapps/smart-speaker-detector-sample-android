package com.carterlabs.sampleapp

import android.support.v7.widget.RecyclerView
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.view_gorcr_item.view.*


class GrocrItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    fun bind(grocerItem: Pair<String, Int>) {
        with(itemView) {
            Glide.with(this).load(grocerItem.second)
                    .apply(RequestOptions().override(1000, 800).centerCrop())
                    .into(grocr_item_image)
            grocr_item_label.text = grocerItem.first
        }
    }

}