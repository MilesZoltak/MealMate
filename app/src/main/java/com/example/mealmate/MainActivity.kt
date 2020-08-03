package com.example.mealmate

import android.content.DialogInterface
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.reflect.typeOf

private const val TAG = "MainActivity"
private val ingredients = mutableListOf<Specification>()
private val tags = mutableListOf<Specification>()
private val trie_ingredients_include = Trie_AC()
private val trie_ingredients_exclude = Trie_AC()
private val trie_tags_include = Trie_AC()
private val trie_tags_exclude = Trie_AC()
class MainActivity : AppCompatActivity() {

    private lateinit var colorDrawableBackGround: ColorDrawable
    private lateinit var deleteIcon: Drawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //set up fab speed dial
        fabSD.setMenuListener(object : SimpleMenuListenerAdapter() {
            override fun onMenuItemSelected(menuItem: MenuItem?): Boolean {
                if (menuItem != null) {
                    if (menuItem.itemId == R.id.ingredient_add) {
                        Log.i(TAG, "selected $menuItem")
                        showSpecificationDialog("ingredients")
                    }

                    if (menuItem.itemId == R.id.tag_add) {
                        Log.i(TAG, "selected $menuItem")
                        showSpecificationDialog("tags")
                    }
                }
                return super.onMenuItemSelected(menuItem)
            }
        })
    }

    fun showSpecificationDialog(specType: String) {
        //setup dialog
        val searchFormView = LayoutInflater.from(this).inflate(R.layout.specification_add, null)
        val dialog = AlertDialog.Builder(this)
            .setTitle("Include/Exclude")
            .setView(searchFormView)
            .setNeutralButton("Close", null)
            .setPositiveButton("Search", null)
            .show()
        dialog.findViewById<EditText>(R.id.etSearch)?.hint = "Enter $specType"

        //set up recyclerview
        val rvSpecs = dialog.findViewById<RecyclerView>(R.id.rvSpecs)
        val adapter = if (specType == "ingredients") SpecsAdapter(this, ingredients) else SpecsAdapter(this, tags)
        rvSpecs?.adapter = adapter
        rvSpecs?.layoutManager = LinearLayoutManager(this)

        //handle user input
        dialog.findViewById<Button>(R.id.btnInclude)?.setOnClickListener {
            val spec = dialog.findViewById<EditText>(R.id.etSearch)?.text.toString()
            if (specType == "ingredients") {
                ingredients.add(Specification(spec, true))
                adapter.notifyItemInserted(ingredients.size - 1)
//                trie_ingredients_include.insert(spec)
                Log.i(TAG, "including $spec in ingredients list and trie")
            } else {
                tags.add(Specification(spec, true))
                adapter.notifyItemInserted(tags.size - 1)
                trie_tags_include.insert(spec)
                Log.i(TAG, "including $spec in tags list and trie")
            }
            dialog.findViewById<EditText>(R.id.etSearch)?.setText("")
            // insert spec intro the correct trie when user enters ingredient
            if (specType == "ingredients") {
                trie_ingredients_include.insert(spec)
            } else {
                trie_tags_include.insert(spec)
            }
        }

        dialog.findViewById<Button>(R.id.btnExclude)?.setOnClickListener {
            val spec = dialog.findViewById<EditText>(R.id.etSearch)?.text.toString()
            if (specType == "ingredients") {
                ingredients.add(Specification(spec, false))
                adapter.notifyItemInserted(ingredients.size - 1)
                trie_ingredients_exclude.insert(spec)
                Log.i(TAG, "excluded $spec from ingredients")
            } else {
                tags.add(Specification(spec, false))
                adapter.notifyItemInserted(tags.size - 1)
                trie_tags_exclude.insert(spec)
                Log.i(TAG, "excluded $spec from tags")
            }
            dialog.findViewById<EditText>(R.id.etSearch)?.setText("")

            //insert specs into proper aho corasick trie as they come in
            if (specType == "ingredients") {
                trie_ingredients_exclude.insert(spec)
            } else {
                trie_tags_exclude.insert(spec)
            }
        }

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {

            //build the tries so we can search ingredients/tags accordingly (this may be no work if any of these tries is empty)
            trie_ingredients_include.buildAhoCorasick()
            trie_ingredients_exclude.buildAhoCorasick()
            trie_tags_include.buildAhoCorasick()
            trie_tags_exclude.buildAhoCorasick()

            searchRecipes(ingredients, tags)
            dialog.dismiss()
        }
        dialog.getButton(DialogInterface.BUTTON_NEUTRAL).setOnClickListener {
            dialog.dismiss()
        }

        //delete icon
        colorDrawableBackGround = ColorDrawable(Color.parseColor("#ff0000"))
        deleteIcon = ContextCompat.getDrawable(this, R.drawable.ic_delete_24px)!!
        //swipe to delete
        val itemTouchHelperCallback = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                if (specType == "ingredients") {
                    val spec2Delete = ingredients[viewHolder.adapterPosition]
                    ingredients.removeAt(viewHolder.adapterPosition)
                } else {
                    val spec2Delete = tags[viewHolder.adapterPosition]
                    tags.removeAt(viewHolder.adapterPosition)
                }
                adapter.notifyItemRemoved(viewHolder.adapterPosition)
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView
                val iconMarginVertical =
                    (viewHolder.itemView.height - deleteIcon.intrinsicHeight) / 2

                if (dX > 0) {
                    colorDrawableBackGround.setBounds(
                        itemView.left,
                        itemView.top,
                        dX.toInt(),
                        itemView.bottom
                    )
                    deleteIcon.setBounds(
                        itemView.left + iconMarginVertical,
                        itemView.top + iconMarginVertical,
                        itemView.left + iconMarginVertical + deleteIcon.intrinsicWidth,
                        itemView.bottom - iconMarginVertical
                    )
                } else {
                    colorDrawableBackGround.setBounds(
                        itemView.right + dX.toInt(),
                        itemView.top,
                        itemView.right,
                        itemView.bottom
                    )
                    deleteIcon.setBounds(
                        itemView.right - iconMarginVertical - deleteIcon.intrinsicWidth,
                        itemView.top + iconMarginVertical,
                        itemView.right - iconMarginVertical,
                        itemView.bottom - iconMarginVertical
                    )
                    deleteIcon.level = 0
                }
                colorDrawableBackGround.draw(c)
                c.save()

                if (dX > 0) {
                    c.clipRect(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
                    deleteIcon.draw(c)
                } else {
                    c.clipRect(
                        itemView.right + dX.toInt(),
                        itemView.top,
                        itemView.right,
                        itemView.bottom
                    )
                    deleteIcon.draw(c)
                    c.restore()
                }
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(rvSpecs)
    }

    private fun searchRecipes(ingredients: MutableList<Specification>, tags: MutableList<Specification>) {
        val db = FirebaseFirestore.getInstance()

        db.collection("recipes")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val haystack = document["ingredients"].toString().decapitalize()
                    val results = trie_ingredients_include.searchAC(haystack)
                    if (results.isNotEmpty()) {
                        Log.i(TAG, "${document["name"]} includes $results")
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error getting documents: ", exception)
            }
    }
}