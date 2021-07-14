import requests
import json
from bs4 import *
baseurl ="https://gogoanime.pe/"
jsonlist=[]
for i in range(1,3):
    stringi=str(i)
    currenturl=baseurl+"anime-list.html?page="+stringi
    page=requests.get(currenturl)
    soup=BeautifulSoup(page.content,'html.parser')
    lis=soup.find('ul',class_='listing').find_all('li')
    for li in lis:
        x=BeautifulSoup(li.get('title'),'html.parser')
        imagelink=x.find('img').get('src')
        animelink=baseurl+li.find('a').get('href')
        title=x.find_all('div')[1].find('a').text
        #print(title)
        #print(animelink)
        anime={
            "anime" : {
                "Anime name" : title,
                "link" : animelink,
                "imagelink" : imagelink
                }
            }
        jsonlist.append(anime)
with open('animelist.json', 'w') as json_file:  
    json.dump(jsonlist, json_file)
