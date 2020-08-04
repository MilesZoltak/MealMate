## MealMate
# A multi-feature meal planning app by Miles Zoltak

This is my first independent Android App!  The main purpose of it is to allow users to input ingredients they do or don't have, and be presented with ingredeints
that are relevant to those search queries.  I'm learning a lot while I do this, and below you can check out a list of things I have learned to do, or at least 
things I have encountered and tried to learn more about.


P.S.  This is probably gonna look very stream-of-consciousnessy but that's just how I roll.  Hopefully this document sorta conveys my personality as well as useful/interesting information.  Enjoy the little peek into my mind!

## Video Walkthrough
<img src="https://imgur.com/8LXskpB.gif" title="Video Walkthrough" alt="video walkthrough"/>
GIF created with [LiceCap](http://www.cockos.com/licecap/).


## Things I have learned so far
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
* [ ] Add recursive deletion function to Aho-Corasick Trie when user swipes to delete (and rebuild after deletion but that's easy)
* [ ] Build in current ingredient seleciton functionality for tags (i only have it for ingredients right now)
  * [ ] Also build in the exclusion feature which is not implemented yet on either ingredients OR tags
* [ ] fix the dialog recyclerview so that items appear centered and longer words don't get linefed
* [ ] when main RV is empty put something there so it doesn't look like a barren wasteland
  * [ ] also put in some sort of loading message showing that the asynchronous task is working but not completed
## For the database
* [ ] I need to comb over the recipes currently in the database and add in tags for "keto-friendly" or whatever else
 * [ ] recipes usually say "keto" or "paleo" etc. in the title but if I didn't grab it from a collection of keto/paleo/etc recipes then I would have missed that
* [ ] Obviously keep building out the recipe database!!!!
* [ ] Think about making an API for recipes that everyone could use! wouldn't that be interesting...
* [ ] Find out if I can keep pushing the same folder of recipes to Firestore without it duplicating recipes or if I will need a new folder for every push of a bunch of recipes (if that makes sense)
