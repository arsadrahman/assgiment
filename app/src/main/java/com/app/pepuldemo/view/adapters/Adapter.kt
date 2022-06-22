package com.app.pepuldemo.view.adapters


import android.content.Context

import androidx.recyclerview.widget.RecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import com.app.pepuldemo.R
import com.app.pepuldemo.model.Data
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ui.PlayerView
import androidx.recyclerview.widget.DiffUtil
import com.app.pepuldemo.utility.DiffUtilCallback


class Adapter(private val context: Context, private val datas: ArrayList<Data>) :
    RecyclerView.Adapter<Adapter.ViewHolder>() {
    var onItemClick: ((Data) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.listitem, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if((datas[position].type as String).contentEquals("0")){
            holder.exoplayerView.visibility = View.GONE
            Glide
                .with(context)
                .load(datas[position].result)
                .centerCrop()
                .into(holder.images);
        }
        else   if((datas[position].type as String).contentEquals("1")){
           //Video Thing here
        }

        holder.deleteButton.setOnClickListener {
            onItemClick?.invoke(datas[position])
        }

    }

    override fun getItemCount(): Int {
        return datas.size
    }

     class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var images: ImageView =  view.findViewById(R.id.image_iv)
        var exoplayerView: PlayerView =  view.findViewById(R.id.exoplayerView)
         var deleteButton: Button = view.findViewById(R.id.delete_btn);

    }

    fun updateListItems(datas: ArrayList<Data>) {
        val diffCallback = DiffUtilCallback(this.datas, datas as ArrayList<Data>)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.datas.clear()
        this.datas.addAll(datas)
        diffResult.dispatchUpdatesTo(this)
    }

}