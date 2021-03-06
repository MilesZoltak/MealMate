from selenium import webdriver
from selenium.webdriver.common.keys import Keys
import json
import os.path

# driver = webdriver.Chrome()
directory = "C:\\Users\\miles\\Desktop\\Meal Planning App Stuff\\Python Scripts\\json_recipes"

for filename in os.listdir(directory):
    if (filename.lower().find("keto") + 1):
        with open(directory + "\\" + filename) as f:
            recipe = json.load(f)
        if ("keto" in recipe["tags"]):
            print("keto already tagged on: " + filename)
        else:
            print("updating " + filename)
            recipe["tags"].append("keto")
            with open(directory + "\\" + filename, "w") as f:
                json.dump(recipe, f)
    if (filename.lower().find("paleo") + 1):
        with open(directory + "\\" + filename) as f:
            recipe = json.load(f)
        if ("paleo" in recipe["tags"]):
            print("paleo already tagged on: " + filename)
        else:
            print("updating " + filename)
            recipe["tags"].append("paleo")
            with open(directory + "\\" + filename, "w") as f:
                json.dump(recipe, f)
    '''
    search = "https://www.google.com/search?q=" + "+".join(filename.replace("&", "")[:-5].split(" ")) + "+delish"
    driver.get(search)
    id = "rimg_"
    x = 0
    while (True):
        try:
            img = driver.find_element_by_id(id + str(x))
            src = img.get_attribute("src")
            break
        except:
            x += 1
        
        if (x >= 100):
            src = ""
            print("ERROR: could not find image source for " + filename)
            break

    try:
        link = driver.find_element_by_class_name("a-no-hover-decoration").get_attribute("href")

        with open(directory + "\\" + filename) as f:
            recipe = json.load(f)
        recipe["image"] = src
        recipe["url"] = link
        with open(directory + "\\" + filename, "w") as f:
            json.dump(recipe, f)
    except:
        print("aw they got us on " + filename)
        driver.close()
    '''
