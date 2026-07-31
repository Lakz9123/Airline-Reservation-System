import os
from PIL import Image, ImageDraw, ImageFont

img_path = r'e:\Desktop\Airline\src\main\resources\static\images\hero-skyfly-elite.png'
out_path = r'e:\Desktop\Airline\src\main\resources\static\images\hero-skyfly-elite-right.png'

if not os.path.exists(img_path):
    print(f"Error: {img_path} not found")
    exit(1)

# Open image and flip horizontally
img = Image.open(img_path)
img = img.transpose(Image.FLIP_LEFT_RIGHT)

# Draw text
draw = ImageDraw.Draw(img)

# Try to find a nice bold font, fallback to arialbd
font_paths = [
    "C:\\Windows\\Fonts\\impact.ttf",
    "C:\\Windows\\Fonts\\arialbd.ttf",
    "C:\\Windows\\Fonts\\trebucbd.ttf"
]
font = None
for fp in font_paths:
    if os.path.exists(fp):
        try:
            font = ImageFont.truetype(fp, 150) # HUGE font
            break
        except:
            pass

if not font:
    font = ImageFont.load_default()

text = "SKYFLY ELITE"
img_w, img_h = img.size

try:
    left, top, right, bottom = draw.textbbox((0, 0), text, font=font)
    text_w = right - left
    text_h = bottom - top
except AttributeError:
    text_w, text_h = draw.textsize(text, font=font)

# Position text in the center, slightly down (to hit the fuselage typically)
text_x = (img_w - text_w) // 2
text_y = (img_h - text_h) // 2 + 50

# Draw text with slight shadow
shadow_color = (255, 255, 255, 120)
draw.text((text_x + 4, text_y + 4), text, font=font, fill=shadow_color)
draw.text((text_x, text_y), text, font=font, fill=(13, 71, 161, 230))

img.save(out_path)
print("Image flipped and saved with HUGE text!")
