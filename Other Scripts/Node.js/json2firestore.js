const firebase = require("firebase");
// Required for side-effects
require("firebase/firestore");

//do file stuff
const path = require("path");
const fs = require("fs");
const { exit } = require("process");
const directoryPath = path.join(__dirname, "../Python Scripts/json_recipes");

// Initialize Cloud Firestore through Firebase
firebase.initializeApp({
    apiKey: "[CONFIDENTIAL]",
    authDomain: "[CONFIDENTIAL]",
    projectId: "[CONFIDENTIAL]"
  });
  
//i will be writing files to this db
var db = firebase.firestore();

fs.readdir(directoryPath, function (err, files) {
    //handling error
    if (err) {
        return console.log("unable to scan directory: " + err);
    }
    //listing all files using forEach
    files.forEach(function(file) {
        recipe = require("../Python Scripts/json_recipes/" + file);
        db.collection("recipes").add({
            name: recipe.name,
            servings: recipe.servings,
            ingredients: recipe.ingredients,
            directions: recipe.directions,
            tags: recipe.tags,
            source: recipe.source,
            image: recipe.image,
            url: recipe.url
        }).then(function(docRef) {
            console.log("Document written with ID: " + docRef.id);
        })
        .catch(function(error) {
            console.error("Error adding document: " + error);
        });
    });
});
