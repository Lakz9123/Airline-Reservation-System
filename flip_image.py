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
try:
    font = ImageFont.truetype("arialbd.ttf", 60)
except:
    font = ImageFont.load_default()

text = "SKYFLY ELITE"

# Get text bounding box for positioning
# The plane is roughly centered. We want the text on the fuselage.
# This requires a rough guess of where the plane is. Center of the image is usually a safe bet.
img_w, img_h = img.size

try:
    # Use textbbox if available
    left, top, right, bottom = draw.textbbox((0, 0), text, font=font)
    text_w = right - left
    text_h = bottom - top
except AttributeError:
    text_w, text_h = draw.textsize(text, font=font)

# Position text in the middle, slightly adjusted
# We can make the text navy blue #0D47A1
text_x = (img_w - text_w) // 2
text_y = (img_h - text_h) // 2

# Draw text with slight transparency or shadow to blend in
shadow_color = (255, 255, 255, 180)
draw.text((text_x + 2, text_y + 2), text, font=font, fill=shadow_color)
draw.text((text_x, text_y), text, font=font, fill=(13, 71, 161, 230))

img.save(out_path)
print("Image flipped and saved with text!")
