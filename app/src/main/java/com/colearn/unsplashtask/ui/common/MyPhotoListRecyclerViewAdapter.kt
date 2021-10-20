package com.colearn.unsplashtask.ui.common

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.colearn.unsplashtask.R
import com.colearn.unsplashtask.pojos.Photo

class MyPhotoListRecyclerViewAdapter(private val context: Context,
                                     private val onClickListener: View.OnClickListener,
                                     private var values: ArrayList<Photo>)
    : RecyclerView.Adapter<MyPhotoListRecyclerViewAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.photo_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.idView.tag = position
        holder.idView.setOnClickListener(onClickListener)
        Glide.with(context).load(item.urls?.thumb)
                .placeholder(R.drawable.ic_launcher_foreground).into(holder.idView)
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val idView: ImageView = view.findViewById(R.id.imageView)
    }

    fun setPhotoList(newPhotos: List<Photo>) {
        val lastPosition = values.size - 1
        values.clear()
        values.addAll(newPhotos)
        val newPosition = values.size - 1
        if(lastPosition in 0..newPosition) {
            notifyItemRangeChanged(lastPosition, newPosition)
        } else {
            notifyDataSetChanged()
        }
    }
}