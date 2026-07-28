import urllib.request
logos = {
    'IndiGo': 'https://upload.wikimedia.org/wikipedia/commons/thumb/6/69/IndiGo_Airlines_logo.svg/800px-IndiGo_Airlines_logo.svg.png',
    'Air India': 'https://upload.wikimedia.org/wikipedia/commons/thumb/7/7b/Air_India_logo_%282023%29.svg/800px-Air_India_logo_%282023%29.svg.png',
    'SpiceJet': 'https://upload.wikimedia.org/wikipedia/en/thumb/e/eb/SpiceJet_Logo.svg/800px-SpiceJet_Logo.svg.png',
    'Vistara': 'https://upload.wikimedia.org/wikipedia/commons/thumb/c/c1/Vistara_Logo.svg/800px-Vistara_Logo.svg.png',
    'Akasa Air': 'https://upload.wikimedia.org/wikipedia/commons/thumb/9/90/Akasa_Air_Logo.svg/800px-Akasa_Air_Logo.svg.png',
    'Air India Express': 'https://upload.wikimedia.org/wikipedia/commons/thumb/6/67/Air_India_Express_Logo.svg/800px-Air_India_Express_Logo.svg.png',
    'Alliance Air': 'https://upload.wikimedia.org/wikipedia/commons/thumb/a/ae/Alliance_Air_logo.svg/800px-Alliance_Air_logo.svg.png',
    'Star Air': 'https://upload.wikimedia.org/wikipedia/commons/thumb/2/23/Star_Air_Logo.svg/800px-Star_Air_Logo.svg.png',
    'FlyBig': 'https://upload.wikimedia.org/wikipedia/commons/thumb/4/4b/FlyBig_Logo.svg/800px-FlyBig_Logo.svg.png'
}

req = urllib.request.Request('', headers={'User-Agent': 'Mozilla/5.0'})
for name, url in logos.items():
    try:
        req.full_url = url
        urllib.request.urlopen(req).read(10)
        print(f"{name}: OK")
    except Exception as e:
        print(f"{name}: FAILED - {e}")
