import urllib.parse

def make_data_uri(svg):
    return 'data:image/svg+xml;utf8,' + urllib.parse.quote(svg.strip())

logos = {
    'Air India Express': '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 400 150">
<text x="80" y="50" fill="#D91C36" font-family="Arial, sans-serif" font-size="25" font-style="italic" font-weight="900">AIR INDIA</text>
<text x="20" y="110" fill="#D91C36" font-family="Arial, sans-serif" font-size="75" font-weight="normal">express</text>
</svg>''',

    'Alliance Air': '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 400 150">
<rect width="400" height="150" fill="#001C3A"/>
<text x="30" y="90" fill="white" font-family="Arial, sans-serif" font-size="75" font-style="italic" font-weight="bold">Alliance</text>
<polygon points="320,35 340,35 325,50" fill="#F47B20"/>
<text x="160" y="120" fill="white" font-family="Arial, sans-serif" font-size="25" font-style="italic" font-weight="bold" letter-spacing="5">AIRLINES</text>
</svg>''',

    'Star Air': '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 400 150">
<text x="40" y="100" fill="#EE1B24" font-family="Arial, sans-serif" font-size="75" font-style="italic" font-weight="900">STAR</text>
<text x="240" y="100" fill="#00207F" font-family="Arial, sans-serif" font-size="75" font-style="italic" font-weight="900">air</text>
<polygon points="285,35 292,50 308,50 295,60 300,75 285,65 270,75 275,60 262,50 278,50" fill="#00207F"/>
</svg>''',

    'FlyBig': '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 400 150">
<path d="M80,80 Q100,120 120,120 Q160,120 220,50 Q230,40 210,35 Q120,70 120,90 Q110,100 100,90 Z" fill="#AD1177"/>
<text x="210" y="110" fill="#AD1177" font-family="Arial, sans-serif" font-size="75" font-weight="normal">flybig.</text>
</svg>'''
}

for name, svg in logos.items():
    print(f'String {name.replace(" ", "")}Logo = "{make_data_uri(svg)}";')
