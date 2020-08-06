# MealMate
## A multi-feature meal planning app by Miles Zoltak

This is my first independent Android App!  The main purpose of it is to allow users to input ingredients they do or don't have, and be presented with ingredeints
that are relevant to those search queries.  I'm learning a lot while I do this, and below you can check out a list of things I have learned to do, or at least 
things I have encountered and tried to learn more about.


P.S.  This is probably gonna look very stream-of-consciousnessy but that's just how I roll.  Hopefully this document sorta conveys my personality as well as useful/interesting information.  Enjoy the little peek into my mind!

# Table of Contents
* [Video Walkthrough](#Walkthrough)
* [How it works](#Functionalities)

## Walkthrough
<img src="https://imgur.com/gKgfgXZ.gif" title="Video Walkthrough" alt="video walkthrough"/>
GIF created with [LiceCap](http://www.cockos.com/licecap/).

# Functionalities
## Search Recipes by Ingredient
**Imagine:** You are trying to figure out what to make for dinner and you want to use the items in your pantry.  With MealMate you can input ingredients you would like to use (if you can find a recipe to use them), as well as ingredients you ABSOLUTELY HAVE TO USE.  To sweeten the deal we'll also add in various tags like "dinner", "easy", "weeknight", "keto" etc. (and we can specify between "Would-Like" and "Must-Have" just like we did for ingredients.

Aha!  These recipes look great!  Except... why would I have coconut flour (or whatever) just laying around???  Fortunately for us, we can simply *exclude* ingredients or tags we don't want to see.  That way, we can get closer to only seeing relevant recipes.

## Search Recipes (not yet implemented, but this is like 1-2 day's work)
Sometimes you have a specific hankering for lasagna or beef & broccoli stir fry or any other delicious food!  Nevermind the ingredients you have, you want **this** and you'll stop at nothing to get it.  All you need is a recipe.  You can search for recipes with ease using MealMate!

## Meal Planning (not yet implemented)
You're a millenial!  You love meal planning!  How else would you survive out there in the big bad world!  MealMate has plenty of options for you:
* Select recipes and fill out your own meal plan
* Take the hands-off approach and have MealMate populate a meal plan for you
* Work together with MealMate by giving some specifications (Mac & Cheese on Wednesday, vegetarian this week, etc.) and leaving the rest to be filled in automatically
Want to consolidate all of those ingredients into a shopping list?  Leave it to MealMate!  Start with everything you'll need for the week, cut out what you already have, and add in any extra goodies you might want to pick up at the store!

## Optimize Your Meal Planning
Who doesn't love saving money?  The way we shop for food can make or break the bank.  MealMate helps us shop in ways that maximize your savings and minimize stress and confusion about grocery shopping.

**Imagine:**  You're at the store and you're passing by the produce.  "$2.00 per pound of Daikon Radishes?!?!" you say, "That's such a great deal!"  Or is it?? I don't know, probably nobody knows.  But MealMate does.  By checking average prices for various goods like meats, produce, dairy, and whatever other goods have fluctuating prices, MealMate can tell you when foods are at the usual price, or if they are more or less pricey than usual.  Now we can take advantage of that deal on Sockeye Salmon and know we are getting the most bang for our buck.

**Imagine:**  You usually go to the grocery store down the street and the prices are fine.  But you're across town today and need some stuff at the store so you stop by.  "WHAT??? I've been getting ripped off at the store for 40 long years?!"  Yeah, we've all been there.  But with MealMate we don't have to be.  MealMate can check prices for your grocery list or whatever items at several local grocery stores so that you are going to the store that's going to save you the most money.


# Things I have learned so far
* Firebase and Cloud Firestore specifically, I'll have to figure out user authentification later but that shouldn't be crazy hard
* Implementing Aho-Corasick Tries, this was quite the trip ~~and I'm still in it but I think I've got the hang of it now~~ I DID IT!
* Writing Python scripts to do webscraping, and then saving those results into JSON files
* Adapting code in languages I'm less familiar with (like Node.js) to write console-based scripts for uploading files to Cloud Firestore
* Using open-source GithHub repos in my own projects
* Endless struggles (and successes) with Recycler Views
* Navigating the Android Studio ecosystem
* And probably a lot more that I am forgetting

# Things I still have to do (not exhaustive, I'm SURE other stuff will come up):
## For the actual app
* [x] Add recursive deletion function to Aho-Corasick Trie when user swipes to delete (and rebuild after deletion but that's easy)
* [x] Build in current ingredient seleciton functionality for tags (i only have it for ingredients right now)
  * [x] Also build in the exclusion feature which is not implemented yet on either ingredients OR tags
* [ ] fix the dialog recyclerview so that items appear centered and longer words don't get linefed
* [ ] when main RV is empty put something there so it doesn't look like a barren wasteland
  * [x] also put in some sort of loading message showing that the asynchronous task is working but not completed
* [ ] Consider combining the ingredients dialog and tag dialog into one dialog navigated with tab layout.  makes navigation easier/faster
* [ ] Append matched ingredients to Recipe data class for use later
  * This may help with potentially sorting recyclerview items by best to worst match
* [ ] Allow user to click recipe and view in new activity!
## For the database
* [x] I need to comb over the recipes currently in the database and add in tags for "keto-friendly" or whatever else
 * [x] recipes usually say "keto" or "paleo" etc. in the title but if I didn't grab it from a collection of keto/paleo/etc recipes then I would have missed that
* [ ] Obviously keep building out the recipe database!!!!
* [ ] Think about making an API for recipes that everyone could use! wouldn't that be interesting...
* [x] Find out if I can keep pushing the same folder of recipes to Firestore without it duplicating recipes or if I will need a new folder for every push of a bunch of recipes (if that makes sense)
