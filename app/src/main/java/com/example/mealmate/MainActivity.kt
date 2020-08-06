package com.example.mealmate

import android.content.DialogInterface
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_add.view.*
import kotlinx.android.synthetic.main.specification_add.*

private const val TAG = "MainActivity"
private val ingredients = mutableListOf<Specification>()
private val tags = mutableListOf<Specification>()
private val required = mutableListOf<String>()
private val trie_ingredients_include = Trie_AC()
private val trie_ingredients_exclude = Trie_AC()
private val trie_tags_include = Trie_AC()
private val trie_tags_exclude = Trie_AC()

private val recipes = mutableListOf<Recipe>()
private val exclusions = mutableListOf<Recipe>()
class MainActivity : AppCompatActivity() {

    private lateinit var colorDrawableBackGround: ColorDrawable
    private lateinit var deleteIcon: Drawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        debugDB()

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

        //set up specifications recyclerview
        val rvSpecs = dialog.findViewById<RecyclerView>(R.id.rvSpecs)
        val specAdapter = if (specType == "ingredients") SpecsAdapter(this, ingredients) else SpecsAdapter(this, tags)
        rvSpecs?.adapter = specAdapter
        rvSpecs?.layoutManager = LinearLayoutManager(this)

        //handle user input
        dialog.findViewById<Button>(R.id.btnInclude)?.setOnClickListener {
            val spec = dialog.findViewById<EditText>(R.id.etSearch)?.text.toString()
            if (specType == "ingredients") {
                ingredients.add(Specification(spec, true))
                specAdapter.notifyItemInserted(ingredients.size - 1)
                Log.i(TAG, "including $spec in ingredients list and trie")
            } else {
                tags.add(Specification(spec, true))
                specAdapter.notifyItemInserted(tags.size - 1)
                Log.i(TAG, "including $spec in tags list and trie")
            }
            dialog.findViewById<EditText>(R.id.etSearch)?.setText("")
            // insert spec intro the correct trie when user enters ingredient
            if (specType == "ingredients") {
                trie_ingredients_include.insert(spec)
            } else {
                trie_tags_include.insert(spec)
            }

            if (dialog.findViewById<CheckBox>(R.id.cbMustHave)?.isChecked!!) {
                required.add(spec)
            }
        }

        dialog.findViewById<Button>(R.id.btnExclude)?.setOnClickListener {
            val spec = dialog.findViewById<EditText>(R.id.etSearch)?.text.toString()
            if (specType == "ingredients") {
                ingredients.add(Specification(spec, false))

                specAdapter.notifyItemInserted(ingredients.size - 1)
                Log.i(TAG, "excluded $spec from ingredients")
            } else {
                tags.add(Specification(spec, false))
                specAdapter.notifyItemInserted(tags.size - 1)
                Log.i(TAG, "excluded $spec from tags")
            }
            dialog.findViewById<EditText>(R.id.etSearch)?.setText("")

            //insert specs into proper aho corasick trie as they come in
            if (specType == "ingredients") {
                trie_ingredients_exclude.insert(spec)
            } else {
                trie_tags_exclude.insert(spec)
            }

            if (dialog.findViewById<CheckBox>(R.id.cbMustHave)?.isChecked!!) {
                required.add(spec)
            }
        }

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            val toast = Toast.makeText(this, "Fetching recipes...", Toast.LENGTH_LONG)
            toast.setGravity(Gravity.TOP, 0, 200)
            toast.show()

            //build the tries so we can search ingredients/tags accordingly (this may be no work if any of these tries is empty)
            trie_ingredients_include.buildAhoCorasick()
            trie_ingredients_exclude.buildAhoCorasick()
            trie_tags_include.buildAhoCorasick()
            trie_tags_exclude.buildAhoCorasick()

            searchRecipes(toast)
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
                    if (spec2Delete.included) {
                        trie_ingredients_include.remove(spec2Delete.name)
                        trie_ingredients_include.buildAhoCorasick()
                    } else {
                        trie_ingredients_exclude.remove(spec2Delete.name)
                        trie_ingredients_exclude.buildAhoCorasick()
                        viewHolder.itemView.tvSpec.apply {
                            paintFlags = Paint.ANTI_ALIAS_FLAG
                        }
                    }
                    required.removeAll(listOf(spec2Delete.name))
                    ingredients.removeAt(viewHolder.adapterPosition)
                } else {
                    val spec2Delete = tags[viewHolder.adapterPosition]
                    if (spec2Delete.included) {
                        trie_tags_include.remove(spec2Delete.name)
                        trie_tags_include.buildAhoCorasick()
                    } else {
                        trie_tags_exclude.remove(spec2Delete.name)
                        trie_tags_exclude.buildAhoCorasick()
                        viewHolder.itemView.tvSpec.apply {
                            paintFlags = Paint.ANTI_ALIAS_FLAG
                        }
                    }
                    required.removeAll(listOf(spec2Delete.name))
                    tags.removeAt(viewHolder.adapterPosition)
                }
                specAdapter.notifyItemRemoved(viewHolder.adapterPosition)
                //TODO: if swipe to delete but dont press search then u keep the recipes with that ingredient but it is lost from the RV... fix it!
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

    private fun debugDB() {
        val db = FirebaseFirestore.getInstance()
        db.collection("recipes")
            .whereEqualTo("name", "Keto Cranberry Sauce")
            .get()
            .addOnSuccessListener { documents ->
                Log.i(TAG, "${documents.size()} items returned by query")
                for (document in documents) {
                    Log.i(TAG, "${document["tags"]}")
                }
            }
    }

    private fun searchRecipes(toast: Toast) {
        //clear out results in case it had something in there before
        recipes.clear()
        exclusions.clear()

        val db = FirebaseFirestore.getInstance()
        db.collection("recipes")
            .get()
            .addOnSuccessListener { documents ->
                //looping over all recipes in db (ugh... so expensive :( !!!)
                for (document in documents) {
                    val haystack_ingredients = document["ingredients"].toString().decapitalize()
                    val haystack_tags = document["tags"].toString().decapitalize()
                    val ingredients_matched = trie_ingredients_include.searchAC(haystack_ingredients)
                    val tags_matched = trie_tags_include.searchAC(haystack_tags)
                    if ((required - ingredients_matched - tags_matched).isEmpty() and (ingredients_matched.isNotEmpty() or tags_matched.isNotEmpty())) {
                        Log.i(TAG, "adding ${document["name"]}")
                        recipes.add(Recipe(
                            document["name"] as String,
                            document["servings"] as String,
                            document["ingredients"] as List<String>,
                            document["directions"] as List<String>,
                            document["tags"] as List<String>,
                            document["source"] as String,
                            document["image"] as String,
                            document["url"] as String
                        ))
                    }
                }
                //looping over all returned recipes looking for recipes to exclude (less expensive!!!)
                toast.cancel()
                for (recipe in recipes) {
                    val haystack_ingredients = recipe.ingredients.toString()
                    val haystack_tags = recipe.tags.toString()
                    if (trie_ingredients_exclude.searchAC(haystack_ingredients).isNotEmpty() or trie_tags_exclude.searchAC(haystack_tags).isNotEmpty()) {
                        exclusions.add(recipe)
                        Log.i(TAG, "excluding ${recipe.name}")
                    }
                }
                Log.i(TAG, "recipes has ${recipes.size} recipes")
                Log.i(TAG, "exclusions has ${exclusions.size} recipes")
                recipes.removeAll(exclusions)
                Log.i(TAG, "recipes now has ${recipes.size} recipes")
                //set up recipe recyclerview
                val rvRecipes = findViewById<RecyclerView>(R.id.rvRecipes)
                val recipeAdapter = RecipeAdapter(this, recipes)
                rvRecipes?.adapter = recipeAdapter
                rvRecipes.layoutManager = LinearLayoutManager(this)
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error getting documents: ", exception)
            }


    }
}