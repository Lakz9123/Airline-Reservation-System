import urllib.request, re
url = 'https://en.wikipedia.org/wiki/List_of_airlines_of_India'
req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0'})
try:
    with urllib.request.urlopen(req) as response:
        html = response.read().decode('utf-8')
    
    airlines = ['IndiGo', 'Air India', 'SpiceJet', 'Vistara', 'Akasa Air', 'Air India Express', 'Alliance Air', 'Star Air', 'FlyBig']
    for a in airlines:
        # Regex to find the row containing the airline name, then grab the image src
        # Some airlines might be inside a link like <a href="...">Air India</a>
        match = re.search(r'<tr[^>]*>[\s\S]*?(?:>'+a+'<|title=\"'+a+'\")[^>]*>[\s\S]*?</tr>', html, re.IGNORECASE)
        if match:
            row = match.group(0)
            img_match = re.search(r'img[^>]*src=\"(//upload\.wikimedia\.org/[^\"]+)\"', row)
            if img_match:
                print(f"{a}: https:{img_match.group(1)}")
            else:
                print(f"{a}: No image found in row")
        else:
            print(f"{a}: Row not found")
except Exception as e:
    print(f'Error: {e}')
