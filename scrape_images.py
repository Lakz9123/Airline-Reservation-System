import urllib.request, re, json
url = 'https://en.wikipedia.org/wiki/List_of_airlines_of_India'
req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0'})
try:
    with urllib.request.urlopen(req) as response:
        html = response.read().decode('utf-8')
    
    # Split the html into rows
    rows = re.findall(r'<tr.*?>.*?</tr>', html, re.IGNORECASE | re.DOTALL)
    
    airlines = ['IndiGo', 'Air India', 'SpiceJet', 'Vistara', 'Akasa Air', 'Air India Express', 'Alliance Air', 'Star Air', 'FlyBig']
    results = {}
    
    for a in airlines:
        found_url = None
        for row in rows:
            if '>'+a+'<' in row or 'title="'+a+'"' in row:
                # Find the first image in this row
                imgs = re.findall(r'<img[^>]*src=\"(//upload\.wikimedia\.org/wikipedia/commons/thumb/[^\"]+)\"', row)
                for img in imgs:
                    if 'Flag_of_India' not in img and 'Air_India_Logo' not in img and 'IndiGo_logo' not in img:
                        # Found a likely aircraft image
                        found_url = "https:" + img.replace('120px', '400px').replace('100px', '400px')
                        break
                if found_url:
                    break
        results[a] = found_url if found_url else f"https://source.unsplash.com/400x200/?airplane,{a.replace(' ', '')}"
        print(f'{a}: {results[a]}')

except Exception as e:
    print(f'Error: {e}')
