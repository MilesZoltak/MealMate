from selenium import webdriver
from selenium.webdriver.common.keys import Keys
import json
import os.path
import time

def formatName(name):
    #filename cannot contain 
    name = name.replace("\\", "")
    name = name.replace("/", "-")
    name = name.replace(":", "")
    name = name.replace("*", "")
    name = name.replace("?", "")
    name = name.replace("\"", "")
    name = name.replace("<", "")
    name = name.replace(">", "")
    name = name.replace("|", "")
    return name

def saveRecipe(name, servings, ingredients, directions, tags, source):
    name = formatName(name)
    recipe = {
        "name": name,
        "servings": servings,
        "ingredients": ingredients,
        "directions": directions,
        "tags": tags,
        "source": source
    }

    with open("./json_recipes/" + name + ".json", 'w') as outfile:
        json.dump(recipe, outfile)

def scrape_recipe(link):
    driver = webdriver.Chrome()
    driver.get(link)
    #set name and servings
    name = driver.find_element_by_class_name("recipe-hed").text
    servings = driver.find_element_by_class_name("yields-amount").text
    #iterate over ingredients and save them to list
    ingredients = []
    for ing in driver.find_elements_by_class_name("ingredient-item"):
        ingredients.append(ing.text)
    #due to lack of structure this is a little harder for directions, but we got it
    directions = []
    dir_lists = driver.find_element_by_class_name("direction-lists")
    dir_ol = dir_lists.find_element_by_css_selector("*")
    dirs = dir_ol.find_elements_by_css_selector("*")
    for dir in dirs:
        directions.append(dir.text)
    
    tags = ["side", "side-dish"]   #hard-coded based on the Delish slideshow in use
    source = "Delish.com"
    driver.close()
    saveRecipe(name, servings, ingredients, directions, tags, source)

driver = webdriver.Chrome()
driver.get("https://www.delish.com/cooking/g1970/side-dishes/?slide=1")
slideCount = driver.find_element_by_class_name("slideshow-toolbar-count").text
slideCount = slideCount.split(" ")[2]   #stores the number of recipes in this collection

#iterate over all the slides and grab them recipes!
for i in range(int(slideCount)):
    if (i > 30):
        time.sleep(1)
        try:
            link = driver.find_element_by_link_text("Delish").get_attribute("href")
        except:
            #oops, the period is hyperlinked too!
            link = driver.find_element_by_link_text("Delish.").get_attribute("href")
        scrape_recipe(link)
    driver.find_element_by_class_name("slide-button-next").click()
