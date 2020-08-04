package com.example.mealmate

data class Recipe(
    val name: String,
    val servings: String,
    val ingredients: List<String>,
    val directions: List<String>,
    val tags: List<String>,
    val source: String,
    val image: String,
    val url: String
)