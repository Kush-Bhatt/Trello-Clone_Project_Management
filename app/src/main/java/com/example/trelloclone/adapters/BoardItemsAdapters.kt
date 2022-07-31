package com.example.trelloclone.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.trelloclone.R
import com.example.trelloclone.models.Board
import kotlinx.android.synthetic.main.item_board.view.*

open class BoardItemsAdapters(private val context: Context, private var list: ArrayList<Board>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_board, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val curr = list[position]

        if (holder is MyViewHolder) {
            Glide
                .with(context)
                .load(curr.image)
                .centerCrop()
                .placeholder(R.drawable.ic_board_place_holder)
                .into(holder.itemView.iv_board_image)

//            holder.itemView.apply {
//                tv_name.text = curr.name
//                tv_created_by.text = "Created by ${curr.createdBy}"
//
//                setOnClickListener {
//                    if (onClickListener != null) {
//                        onClickListener!!.onClick(position, curr)
//                    }
//                }
//            }
            holder.itemView.tv_name.text = curr.name
            holder.itemView.tv_created_by.text = "Created By : ${curr.createdBy}"

            holder.itemView.setOnClickListener {
                Log.i("Holder","Holder")
//                if (onClickListener != null) {
                    onClickListener!!.onClick(position, curr)
//                }
            }

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(position: Int, curr: Board)
    }

    //A ViewHolder describes an item view and metadata about its place within the RecyclerView.
    private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)



}