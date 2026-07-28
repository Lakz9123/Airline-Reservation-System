import urllib.parse

def make_data_uri(svg):
    return 'data:image/svg+xml;utf8,' + urllib.parse.quote(svg.strip())

logos = {
    'IndiGo': '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 400 150">
<rect width="400" height="150" fill="#001B94"/>
<text x="30" y="100" fill="white" font-family="Arial, sans-serif" font-size="80" font-weight="bold">IndiGo</text>
<circle cx="280" cy="80" r="6" fill="white"/><circle cx="300" cy="70" r="6" fill="white"/><circle cx="320" cy="55" r="6" fill="white"/><circle cx="340" cy="40" r="6" fill="white"/>
</svg>''',

    'Air India': '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 400 150">
<rect width="400" height="150" fill="#E31837"/>
<text x="30" y="100" fill="white" font-family="Arial, sans-serif" font-size="60" font-weight="900">AIR INDIA</text>
<path d="M320,40 Q380,40 370,110 Q340,60 320,40" fill="#F3A71C"/>
</svg>''',

    'SpiceJet': '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 400 150">
<rect width="400" height="150" fill="#DA251D"/>
<text x="140" y="100" fill="white" font-family="Arial, sans-serif" font-size="70" font-style="italic" font-weight="bold">spicejet</text>
<circle cx="50" cy="110" r="12" fill="#F9A01B"/><circle cx="80" cy="95" r="16" fill="#F9A01B"/><circle cx="110" cy="70" r="22" fill="#F9A01B"/><circle cx="80" cy="45" r="14" fill="#F9A01B"/>
</svg>''',

    'Vistara': '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 400 150">
<rect width="400" height="150" fill="#3D1152"/>
<text x="140" y="95" fill="white" font-family="Arial, sans-serif" font-size="65" font-weight="100">vistara</text>
<path d="M70,30 L100,75 L70,120 L40,75 Z" fill="none" stroke="#C0934F" stroke-width="5"/>
<path d="M70,50 L90,75 L70,100 L50,75 Z" fill="none" stroke="#C0934F" stroke-width="3"/>
</svg>''',

    'Akasa Air': '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 400 150">
<rect width="400" height="150" fill="#4C1C8D"/>
<text x="120" y="100" fill="white" font-family="Arial, sans-serif" font-size="65" font-weight="bold">Akasa Air</text>
<path d="M60,110 Q90,30 100,20 Q100,60 85,110 Z" fill="#F05E23"/>
</svg>''',
    
    'Air India Express': '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 400 150">
<text x="20" y="95" fill="#EF3E42" font-family="Arial, sans-serif" font-size="40" font-weight="bold">AIR INDIA EXPRESS</text>
</svg>''',

    'Alliance Air': '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 400 150">
<text x="40" y="95" fill="#1B365D" font-family="Arial, sans-serif" font-size="55" font-weight="bold">Alliance Air</text>
</svg>''',

    'Star Air': '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 400 150">
<text x="60" y="95" fill="#DA291C" font-family="Arial, sans-serif" font-size="60" font-weight="bold">Star Air</text>
</svg>''',

    'FlyBig': '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 400 150">
<text x="80" y="95" fill="#11457E" font-family="Arial, sans-serif" font-size="65" font-weight="bold">FlyBig</text>
</svg>'''
}

for name, svg in logos.items():
    print(f'String {name.replace(" ", "")}Logo = "{make_data_uri(svg)}";')
