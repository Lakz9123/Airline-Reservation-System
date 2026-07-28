import os
from pathlib import Path
import re

templates_dir = Path(r'e:\Desktop\Airline\src\main\resources\templates')
for html_file in templates_dir.rglob('*.html'):
    text = html_file.read_text(encoding='utf-8')
    orig_text = text
    
    # Replace Max Fare ($) -> Max Fare (₹)
    text = text.replace('($)', '(₹)')
    
    # Replace $<span -> ₹<span
    text = text.replace('$<span', '₹<span')
    
    # Replace >$< -> >₹<
    text = text.replace('>$<', '>₹<')
    
    # Replace '$' in JavaScript (e.g., '$0.00', '$' +) -> '₹'
    text = text.replace("'$0.00'", "'₹0.00'")
    text = text.replace("'$'", "'₹'")
    text = text.replace('\"$\"', '\"₹\"')
    
    # Replace >$0.00< -> >₹0.00<
    text = text.replace('>$0.00<', '>₹0.00<')

    # Replace Total Fare: $ -> Total Fare: ₹
    text = text.replace('Fare: $', 'Fare: ₹')
    text = text.replace('fare: $', 'fare: ₹')
    text = text.replace('Price: $', 'Price: ₹')
    text = text.replace('price: $', 'price: ₹')
    
    # Extra check for payment.html and search.html
    text = text.replace('placeholder="Enter Amount in $"', 'placeholder="Enter Amount in ₹"')
    
    if text != orig_text:
        html_file.write_text(text, encoding='utf-8')
        print(f'Updated {html_file.name}')
