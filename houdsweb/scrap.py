import urllib2
import re

url = "https://resultats.ffbb.com/championnat/rencontres/b5e6211eda83b5e6212003d94.html"

#f = urllib2.urlopen(url)
f = open("tt.html","r")
lines = f.readlines()
ls = ''.join(lines)
idx = [m.start() for m in re.finditer('<tr', ls)]

f.close()

