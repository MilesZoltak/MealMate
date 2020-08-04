package com.example.mealmate

import android.content.Context
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_add.view.*

private const val TAG = "SpecsAdapter"
class SpecsAdapter(val context: Context, val specs: List<Specification>) :
RecyclerView.Adapter<SpecsAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_add, parent, false))
    }

    override fun getItemCount() = specs.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val spec = specs[position]
        holder.bind(spec)
    }

    inner class ViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView) {
        fun bind(spec: Specification) {
            Log.i(TAG, "binding ${spec.name}")
            itemView.tvSpec.text = "    ${spec.name}    "
            if (!spec.included) {
                itemView.tvSpec.apply {
                    paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                }
            }
        }
    }
}
