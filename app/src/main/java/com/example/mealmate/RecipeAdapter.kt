package com.example.mealmate

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.recipe_item.view.*

private const val TAG = "RecipeAdapter"
class RecipeAdapter(val context: Context, val recipes: List<Recipe>) :
    RecyclerView.Adapter<RecipeAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.recipe_item, parent, false))
    }

    override fun getItemCount() = recipes.size


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.bind(recipe)
    }

    inner class ViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView) {
        fun bind(recipe: Recipe) {
            Log.i(TAG, "binding ${recipe.name}")
            itemView.tvName.text = recipe.name
            itemView.tvSource.text = recipe.source
            Glide.with(context).load(recipe.image).apply(RequestOptions().transform(
                CenterCrop(), RoundedCorners(20)
            )).into(itemView.ivImage)
        }

    }

}
