---
name: SkyFly Elite
colors:
  surface: '#f6faff'
  surface-dim: '#cfdce7'
  surface-bright: '#f6faff'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#ebf5ff'
  surface-container: '#e3effb'
  surface-container-high: '#ddeaf5'
  surface-container-highest: '#d8e4f0'
  on-surface: '#111d25'
  on-surface-variant: '#44474c'
  inverse-surface: '#26323b'
  inverse-on-surface: '#e6f2fe'
  outline: '#74777d'
  outline-variant: '#c4c6cc'
  surface-tint: '#525f71'
  primary: '#000000'
  on-primary: '#ffffff'
  primary-container: '#0f1c2c'
  on-primary-container: '#778598'
  inverse-primary: '#bac8dc'
  secondary: '#005cab'
  on-secondary: '#ffffff'
  secondary-container: '#0075d6'
  on-secondary-container: '#fefcff'
  tertiary: '#000000'
  on-tertiary: '#ffffff'
  tertiary-container: '#1a1c1c'
  on-tertiary-container: '#838484'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#d6e4f9'
  primary-fixed-dim: '#bac8dc'
  on-primary-fixed: '#0f1c2c'
  on-primary-fixed-variant: '#3a4859'
  secondary-fixed: '#d4e3ff'
  secondary-fixed-dim: '#a5c8ff'
  on-secondary-fixed: '#001c3a'
  on-secondary-fixed-variant: '#004786'
  tertiary-fixed: '#e2e2e2'
  tertiary-fixed-dim: '#c6c6c6'
  on-tertiary-fixed: '#1a1c1c'
  on-tertiary-fixed-variant: '#454747'
  background: '#f6faff'
  on-background: '#111d25'
  surface-variant: '#d8e4f0'
typography:
  display-lg:
    fontFamily: Montserrat
    fontSize: 48px
    fontWeight: '700'
    lineHeight: '1.1'
    letterSpacing: -0.02em
  display-lg-mobile:
    fontFamily: Montserrat
    fontSize: 32px
    fontWeight: '700'
    lineHeight: '1.2'
  headline-md:
    fontFamily: Montserrat
    fontSize: 24px
    fontWeight: '600'
    lineHeight: '1.3'
  body-lg:
    fontFamily: Inter
    fontSize: 18px
    fontWeight: '400'
    lineHeight: '1.6'
  body-md:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: '1.5'
  label-sm:
    fontFamily: Inter
    fontSize: 13px
    fontWeight: '600'
    lineHeight: '1.2'
    letterSpacing: 0.05em
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  unit: 8px
  container-max: 1280px
  gutter: 24px
  margin-desktop: 64px
  margin-mobile: 20px
---

## Brand & Style

The design system is engineered to evoke the prestige and serenity of high-altitude travel. Targeting a mix of discerning business travelers and premium leisure seekers, the UI prioritizes clarity, confidence, and a sense of "boundless" space. 

The aesthetic is **Modern Corporate**, blending the structural reliability of legacy airlines with the airy, fluid interface of contemporary SaaS. It utilizes generous whitespace to reduce cognitive load during complex booking flows. The emotional response is one of calm efficiency—transitioning the user from the stress of planning to the anticipation of the journey.

## Colors

This design system utilizes a high-contrast palette to ensure legibility and professional hierarchy. 

- **Primary (Deep Navy):** Used for primary navigation, headings, and high-emphasis action buttons to convey authority and stability.
- **Secondary (Sky Blue):** Applied to active states, progress indicators, and primary call-to-actions to provide a vibrant, optimistic accent.
- **Neutral/Dividers:** A strict logic of soft grays is used for structural borders and secondary text to maintain a lightweight feel.

In **Dark Mode**, the Deep Navy surfaces become the base background, while the Sky Blue retains its vibrance to guide the eye toward interactive elements. Surfaces use a slightly lighter navy shade to maintain depth.

## Typography

The typography strategy leverages the geometric strength of **Montserrat** for display and headings to establish a premium, editorial feel. **Inter** is utilized for all functional text, body copy, and UI labels due to its exceptional legibility at small sizes and high x-height.

- **Headings:** Should always be bold or semi-bold. Large display type uses tight letter spacing for a "heroic" impact.
- **Labels:** Small labels (like flight numbers or seat classes) use uppercase styling with increased tracking to improve scanning speed.
- **Mobile Scaling:** Headline sizes should aggressively downscale on mobile to ensure flight details remain visible above the fold.

## Layout & Spacing

The layout follows a **Fixed-Fluid Hybrid Grid**. Content is housed within a 1280px central container on desktop to prevent eye strain. 

- **Grid:** 12-column grid for desktop, 8-column for tablet, and 4-column for mobile.
- **Rhythm:** An 8px linear scale governs all padding and margins. 
- **White Space:** For the "Premium" feel, use "Extravagant Padding" (e.g., 80px+) between major vertical sections to allow the brand imagery and flight cards to breathe.
- **Adaptation:** On mobile, margins shrink to 20px, and horizontal lists (like date pickers) transform into swipeable carousels to preserve vertical space.

## Elevation & Depth

Hierarchy is established through **Ambient Shadows** and tonal layering. 

- **Level 1 (Base):** The page background (#FFFFFF).
- **Level 2 (Cards):** Flight results and booking modules sit on a white surface with a very soft, diffused shadow (`0px 4px 20px rgba(0,0,0,0.05)`). This creates a "floating" effect without the harshness of borders.
- **Level 3 (Overlays):** Modals and dropdowns use a slightly more aggressive shadow and a backdrop blur (10px) to focus the user’s attention on the task at hand.
- **Dividers:** Use 1px solid lines in #E0E0E0 for internal card segmentation.

## Shapes

The design system employs a **Rounded** shape language to soften the corporate nature of the brand.

- **Primary Radius:** 16px (`rounded-lg`) is the standard for all primary cards, flight summaries, and hero sections.
- **Secondary Radius:** 8px (`rounded-md`) for buttons and input fields to maintain structural integrity.
- **Interactive Elements:** Buttons utilize the 8px radius, unless they are "Pill" style buttons for category selection (e.g., Round Trip vs. One Way).

## Components

### Buttons
- **Primary:** Deep Navy background, white text, 8px radius. High-emphasis for "Book Now."
- **Secondary:** Sky Blue background or border. Used for "Add Extras" or "Modify Search."
- **Ghost:** Transparent with Navy text for "Cancel" or "View Details."

### Cards
- White background, 16px radius, 1px subtle border (#E0E0E0) only if shadows are not sufficient for the environment. Inside the card, use padding of 24px-32px.

### Input Fields
- Heights should be generous (48px - 56px).
- Borders: 1px solid #E0E0E0. 
- Focus State: 2px solid Sky Blue (#1E90FF) with a light blue outer glow.

### Chips & Badges
- For "Cabin Class" or "Status," use pill-shaped containers with light-tinted backgrounds and dark-tinted text (e.g., Light Blue background with Dark Blue text).

### Specialized Components
- **Seat Map:** Uses 4px rounded squares with a color-coded legend (Blue for Premium, Gray for Occupied).
- **Flight Timeline:** A thin vertical or horizontal Navy line with Sky Blue "nodes" representing departures and arrivals.