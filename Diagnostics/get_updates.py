### Assumption : Confirmed_cases.csv exists in the working directory
from selenium import webdriver
import pandas as pd
import os
import re
from selenium.webdriver import ActionChains
from selenium.common.exceptions import NoSuchElementException  
from selenium.webdriver.common.keys import Keys
import time
import numpy as np
import random
from datetime import datetime
import collections

def init():
    URL = "https://www.arcgis.com/apps/opsdashboard/index.html#/bda7594740fd40299423467b48e9ecf6"

    chrome_options = webdriver.ChromeOptions()
    chrome_options.headless = True
    prefs = {"profile.default_content_setting_values.notifications" : 2}
    chrome_options.add_experimental_option("prefs",prefs)
    br = webdriver.Chrome(executable_path=r"C:\Users/qasim/Downloads/chromedriver_win32_v80/chromedriver.exe",options=chrome_options)
    br.get(URL)
    time.sleep(3)
    return br


def main():
    root = init().find_element_by_id("ember6")

    data = root.text.split("\n")[2:]

    end = data.index("Last Updated at (M/D/YYYY)")

    total_cases = data[1]

    start = 1 + data.index("Confirmed Cases by Country/Region/Sovereignty")

    data = data[start:end]

    total_cases = int(total_cases.replace(",",""))

    TIME = str(datetime.now())

    case = {}

    for x in data:
        x = x.split(" ")
        country = " ".join(x[1:]).replace("*", "")
        cases = int(x[0].replace(",",""))
        case[country] = cases

    case = collections.OrderedDict(sorted(case.items()))
    case["Total Count"] = total_cases
    df = pd.read_csv("C:/Users/qasim/Desktop/Exigence/COVID-19/CoronaBluetooth/Diagnostics/Confirmed_case.csv")

    df_temp = pd.DataFrame.from_dict(case, orient="index", columns=[':'.join(TIME.split(":")[:-1])]).reset_index()

    del df_temp["index"]

    df_cum = [df, df_temp]

    df_cum = pd.concat(df_cum, axis=1)

    df_cum.to_csv("C:/Users/qasim/Desktop/Exigence/COVID-19/CoronaBluetooth/Diagnostics/Confirmed_case.csv", index=False)
    
    return {"CODE" : "200", "Total Cases" : total_cases, "TIME" : TIME}

if __name__ == "__main__":
    status = main()
    print(status)
