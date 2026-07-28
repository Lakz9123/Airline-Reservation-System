import os
airlines = {
    'IndiGo': ('indigo', '#000080'),
    'Air India': ('airindia', '#ED1C24'),
    'SpiceJet': ('spicejet', '#F37021'),
    'Vistara': ('vistara', '#5C2D5C'),
    'Akasa Air': ('akasaair', '#FF6600'),
    'Air India Express': ('airindiaexpress', '#EE3124'),
    'Alliance Air': ('allianceair', '#F7941D'),
    'Star Air': ('starair', '#1C2E5D'),
    'FlyBig': ('flybig', '#1E4885')
}
os.makedirs('src/main/resources/static/images/airlines', exist_ok=True)
for name, (fname, color) in airlines.items():
    svg = f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 50" width="100" height="50">
  <rect width="100" height="50" rx="5" fill="{color}" />
  <text x="50" y="25" dominant-baseline="middle" text-anchor="middle" fill="white" font-family="Arial, sans-serif" font-size="14" font-weight="bold">{name}</text>
</svg>'''
    with open(f'src/main/resources/static/images/airlines/{fname}.svg', 'w') as f:
        f.write(svg)
print('Generated local SVGs')
