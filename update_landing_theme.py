file_path = r'e:\Desktop\Airline\src\main\resources\templates\landing.html'

with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

start_tag = "<style>"
end_tag = "</style>"

start_idx = content.find(start_tag)
# Find the end tag AFTER the start tag
end_idx = content.find(end_tag, start_idx)

if start_idx == -1 or end_idx == -1:
    print("Error: style tags not found")
    exit(1)

new_style = """
        /* ─── Design Tokens ─────────────────────────────── */
        :root {
            --sky:        #0284c7;
            --sky-light:  #0ea5e9;
            --sky-glow:   rgba(14, 165, 233, 0.08);
            --sky-dark:   #0369a1;
            --accent:     #f97316;
            --bg:         #f8fafc; /* Premium light slate background */
            --surface:    rgba(255, 255, 255, 0.75);
            --text:       #0f172a; /* Slate 900 for optimal readability */
            --text-muted: #475569; /* Slate 600 */
            --border:     rgba(15, 23, 24, 0.08);
            --shadow-sm:  0 1px 3px rgba(15,23,42,0.05);
            --shadow-md:  0 12px 30px -10px rgba(15, 23, 42, 0.08);
            --shadow-lg:  0 20px 40px -15px rgba(15, 23, 42, 0.12), 0 0 50px rgba(14, 165, 233, 0.04);
            --radius:     24px;
        }

        /* ─── Base ──────────────────────────────────────── */
        *, *::before, *::after { box-sizing: border-box; }
        html { scroll-behavior: smooth; }
        body {
            font-family: 'Plus Jakarta Sans', sans-serif;
            background: var(--bg);
            color: var(--text);
            overflow-x: hidden;
            letter-spacing: -0.01em;
        }

        /* Ambient Glow Orbs */
        .glowing-orb {
            position: absolute;
            width: 400px;
            height: 400px;
            border-radius: 50%;
            background: radial-gradient(circle, rgba(14, 165, 233, 0.06) 0%, transparent 70%);
            filter: blur(40px);
            pointer-events: none;
            z-index: 1;
        }

        /* ─── Navbar ────────────────────────────────────── */
        .navbar-main {
            background: rgba(255, 255, 255, 0.75);
            backdrop-filter: blur(20px);
            -webkit-backdrop-filter: blur(20px);
            border-bottom: 1px solid var(--border);
            padding: 1.1rem 0;
            transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1);
        }
        .navbar-main.scrolled {
            background: rgba(255, 255, 255, 0.92);
            box-shadow: var(--shadow-md);
            padding: .8rem 0;
            border-bottom-color: rgba(14, 165, 233, 0.1);
        }
        .navbar-brand {
            font-family: 'Montserrat', sans-serif;
            font-weight: 900;
            font-size: 1.45rem;
            color: var(--text) !important;
            letter-spacing: -1.2px;
            text-decoration: none;
            display: flex;
            align-items: center;
            gap: .6rem;
        }
        .brand-icon {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            width: 38px;
            height: 38px;
            background: linear-gradient(135deg, var(--sky-light), var(--sky-dark));
            border-radius: 12px;
            color: #fff;
            font-size: 1.1rem;
            box-shadow: 0 4px 15px rgba(14, 165, 233, 0.2);
        }
        .brand-sky  { color: var(--text); }
        .brand-fly  { color: var(--sky-light); }
        .brand-elite {
            font-size: .6rem;
            font-weight: 900;
            letter-spacing: 2px;
            text-transform: uppercase;
            color: #fff;
            background: linear-gradient(135deg, var(--sky-light), var(--sky-dark));
            border: 1px solid rgba(255, 255, 255, 0.15);
            border-radius: 6px;
            padding: .2rem .5rem;
            vertical-align: middle;
            margin-left: .2rem;
            box-shadow: 0 2px 10px rgba(14, 165, 233, 0.15);
        }
        .nav-link-item {
            color: var(--text-muted) !important;
            font-weight: 600;
            font-size: .88rem;
            padding: .4rem .9rem !important;
            border-radius: 10px;
            transition: all .3s ease;
        }
        .nav-link-item:hover {
            color: var(--sky-dark) !important;
            background: rgba(14, 165, 233, 0.05);
        }
        .btn-login {
            color: var(--sky-dark);
            font-weight: 600;
            font-size: .88rem;
            background: transparent;
            border: 1.5px solid rgba(14, 165, 233, 0.3);
            border-radius: 12px;
            padding: .5rem 1.3rem;
            transition: all .3s ease;
            text-decoration: none;
        }
        .btn-login:hover {
            border-color: var(--sky-light);
            background: rgba(14, 165, 233, 0.05);
            box-shadow: 0 0 12px rgba(14, 165, 233, 0.1);
        }
        .btn-signup {
            background: linear-gradient(135deg, var(--sky-light), var(--sky-dark));
            color: #fff;
            font-weight: 700;
            font-size: .88rem;
            border: none;
            border-radius: 12px;
            padding: .55rem 1.5rem;
            transition: all .3s ease;
            text-decoration: none;
            box-shadow: 0 4px 20px rgba(14, 165, 233, 0.25);
        }
        .btn-signup:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 25px rgba(14, 165, 233, 0.4);
            color: #fff;
        }

        /* ─── Hero ──────────────────────────────────────── */
        .hero {
            position: relative;
            min-height: 98vh;
            display: flex;
            align-items: center;
            background: url('/images/hero-skyfly-elite.png') center center / cover no-repeat;
            padding-top: 100px;
        }
        .hero-overlay {
            position: absolute; inset: 0;
            background: linear-gradient(135deg,
                rgba(248, 250, 252, 0.94) 0%,
                rgba(248, 250, 252, 0.82) 50%,
                rgba(14, 165, 233, 0.04) 100%);
        }
        .hero-content { position: relative; z-index: 2; }
        .hero-badge {
            display: inline-flex; align-items: center; gap: .5rem;
            background: rgba(14, 165, 233, 0.08);
            border: 1px solid rgba(14, 165, 233, 0.2);
            border-radius: 50px;
            padding: .4rem 1.1rem;
            font-size: .78rem;
            font-weight: 700;
            color: var(--sky-dark);
            margin-bottom: 1.8rem;
            letter-spacing: .5px;
            backdrop-filter: blur(10px);
            box-shadow: 0 4px 15px rgba(15, 23, 42, 0.05);
        }
        .hero-badge i {
            font-size: .85rem;
            animation: pulse-badge 2s infinite;
        }
        @keyframes pulse-badge {
            0% { transform: scale(1); opacity: 1; }
            50% { transform: scale(1.2); opacity: 0.7; }
            100% { transform: scale(1); opacity: 1; }
        }
        .hero-title {
            font-family: 'Space Grotesk', sans-serif;
            font-size: clamp(2.8rem, 6vw, 4.8rem);
            font-weight: 700;
            line-height: 1.05;
            letter-spacing: -2px;
            color: var(--text);
            margin-bottom: 1.5rem;
        }
        .hero-title-highlight {
            background: linear-gradient(135deg, var(--sky-dark) 30%, var(--sky-light) 100%);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            font-style: normal;
        }
        .hero-subtitle {
            font-size: 1.15rem;
            color: var(--text-muted);
            max-width: 560px;
            line-height: 1.8;
            margin-bottom: 3.2rem;
        }

        /* ─── Search Card (Frosted Glassmorphism) ────────── */
        .search-card {
            background: rgba(255, 255, 255, 0.85);
            backdrop-filter: blur(25px);
            -webkit-backdrop-filter: blur(25px);
            border-radius: var(--radius);
            box-shadow: 0 30px 70px -10px rgba(15, 23, 42, 0.1), 0 0 50px rgba(14, 165, 233, 0.02);
            padding: 2.2rem;
            border: 1px solid rgba(255, 255, 255, 0.6);
            max-width: 960px;
            position: relative;
            overflow: hidden;
        }
        .search-card::before {
            content: '';
            position: absolute; top: 0; left: 0; right: 0; height: 1.5px;
            background: linear-gradient(90deg, transparent, rgba(14, 165, 233, 0.3), transparent);
        }
        .search-card .form-label {
            font-size: .72rem;
            font-weight: 800;
            text-transform: uppercase;
            letter-spacing: 1px;
            color: var(--sky-dark);
            margin-bottom: .5rem;
        }
        .search-card .form-control,
        .search-card .form-select {
            border: 1px solid rgba(15, 23, 42, 0.08);
            border-radius: 14px;
            font-size: .92rem;
            padding: .75rem 1.1rem;
            color: var(--text);
            background: rgba(255, 255, 255, 0.6);
            transition: all .3s cubic-bezier(0.16, 1, 0.3, 1);
        }
        .search-card .form-control:focus,
        .search-card .form-select:focus {
            border-color: var(--sky-light);
            box-shadow: 0 0 0 4px rgba(14, 165, 233, 0.15);
            background: #fff;
        }
        .btn-search {
            background: linear-gradient(135deg, var(--sky-light), var(--sky-dark));
            color: #fff;
            font-weight: 800;
            font-size: .9rem;
            letter-spacing: 1px;
            border: none;
            border-radius: 14px;
            padding: .85rem 1.5rem;
            width: 100%;
            transition: all .3s ease;
            box-shadow: 0 4px 20px rgba(14, 165, 233, 0.25);
        }
        .btn-search:hover {
            transform: translateY(-2px);
            box-shadow: 0 10px 25px rgba(14, 165, 233, 0.45);
        }

        /* ─── Partners ───────────────────────────────────── */
        .partners-strip {
            background: #ffffff;
            border-top: 1px solid var(--border);
            border-bottom: 1px solid var(--border);
            padding: 2rem 0;
        }
        .partner-tag {
            font-size: .85rem;
            font-weight: 800;
            letter-spacing: 1.5px;
            text-transform: uppercase;
            color: #94a3b8;
            margin: 0.6rem 2.2rem;
            transition: all .3s ease;
            cursor: default;
        }
        .partner-tag:hover {
            color: var(--sky-dark);
            transform: scale(1.05);
        }

        /* ─── Section Headings ───────────────────────────── */
        .section-label {
            font-size: .75rem;
            font-weight: 800;
            letter-spacing: 2.5px;
            text-transform: uppercase;
            color: var(--sky-dark);
            margin-bottom: .8rem;
        }
        .section-title {
            font-family: 'Space Grotesk', sans-serif;
            font-size: clamp(2.2rem, 3.8vw, 2.8rem);
            font-weight: 700;
            letter-spacing: -1px;
            color: var(--text);
            margin-bottom: 1.4rem;
        }
        .section-divider {
            width: 64px; height: 5px;
            background: linear-gradient(90deg, var(--sky-light), #22d3ee);
            border-radius: 10px;
            margin-bottom: 4rem;
        }

        /* ─── Feature Cards ──────────────────────────────── */
        .feature-card {
            background: rgba(255, 255, 255, 0.7);
            border: 1px solid var(--border);
            border-radius: var(--radius);
            padding: 2.5rem 2rem;
            height: 100%;
            transition: all .4s cubic-bezier(0.16, 1, 0.3, 1);
            backdrop-filter: blur(10px);
            box-shadow: var(--shadow-sm);
        }
        .feature-card:hover {
            transform: translateY(-10px);
            border-color: rgba(14, 165, 233, 0.2);
            box-shadow: 0 25px 50px -12px rgba(15, 23, 42, 0.08), 0 0 30px rgba(14, 165, 233, 0.04);
            background: #ffffff;
        }
        .feature-icon-wrap {
            width: 60px; height: 60px;
            border-radius: 16px;
            background: rgba(14, 165, 233, 0.06);
            border: 1px solid rgba(14, 165, 233, 0.15);
            display: flex; align-items: center; justify-content: center;
            margin-bottom: 1.8rem;
            transition: all .4s cubic-bezier(0.16, 1, 0.3, 1);
        }
        .feature-card:hover .feature-icon-wrap {
            background: linear-gradient(135deg, var(--sky-light), var(--sky-dark));
            border-color: var(--sky-light);
            box-shadow: 0 0 20px rgba(14, 165, 233, 0.3);
            transform: rotate(5deg) scale(1.05);
        }
        .feature-card:hover .feature-icon-wrap i {
            color: #fff;
        }
        .feature-icon-wrap i {
            font-size: 1.7rem;
            color: var(--sky-dark);
            transition: all .3s ease;
        }
        .feature-title {
            font-size: 1.15rem;
            font-weight: 800;
            margin-bottom: .8rem;
            color: var(--text);
        }
        .feature-text {
            font-size: .92rem;
            color: var(--text-muted);
            line-height: 1.75;
        }

        /* ─── Route Cards ────────────────────────────────── */
        .routes-scroll-wrap {
            overflow-x: auto;
            scroll-snap-type: x mandatory;
            -webkit-overflow-scrolling: touch;
            padding-bottom: 2rem;
            scrollbar-width: none;
        }
        .routes-scroll-wrap::-webkit-scrollbar { display: none; }
        .routes-track {
            display: flex;
            gap: 1.8rem;
            width: max-content;
            padding: 0.8rem;
        }
        .route-card {
            flex: 0 0 300px;
            width: 300px;
            border-radius: var(--radius);
            overflow: hidden;
            position: relative;
            height: 400px;
            scroll-snap-align: start;
            box-shadow: 0 15px 35px -10px rgba(15, 23, 42, 0.15);
            transition: all .4s cubic-bezier(0.16, 1, 0.3, 1);
            border: 1px solid var(--border);
        }
        .route-card:hover {
            transform: translateY(-8px);
            border-color: rgba(14, 165, 233, 0.25);
            box-shadow: 0 25px 50px -15px rgba(15, 23, 42, 0.25), 0 0 30px rgba(14, 165, 233, 0.1);
        }
        .route-img {
            width: 100%; height: 100%;
            object-fit: cover;
            transition: transform .8s cubic-bezier(0.16, 1, 0.3, 1);
        }
        .route-card:hover .route-img { transform: scale(1.1); }
        .route-overlay {
            position: absolute; inset: 0;
            background: linear-gradient(to top, rgba(15, 23, 42, 0.85) 0%, rgba(15, 23, 42, 0.15) 60%, transparent 100%);
            transition: all 0.3s ease;
        }
        .route-card:hover .route-overlay {
            background: linear-gradient(to top, rgba(15, 23, 42, 0.92) 0%, rgba(15, 23, 42, 0.3) 60%, transparent 100%);
        }
        .route-body {
            position: absolute; bottom: 0; left: 0; right: 0;
            padding: 1.8rem;
            z-index: 2;
        }
        .price-badge {
            display: inline-block;
            background: rgba(255, 255, 255, 0.2);
            backdrop-filter: blur(8px);
            -webkit-backdrop-filter: blur(8px);
            color: #fff;
            font-size: .78rem;
            font-weight: 800;
            padding: .35rem 1rem;
            border-radius: 30px;
            margin-bottom: .8rem;
            letter-spacing: .5px;
            border: 1px solid rgba(255, 255, 255, 0.25);
            transition: all 0.3s ease;
        }
        .route-card:hover .price-badge {
            background: var(--sky-light);
            color: #fff;
            box-shadow: 0 0 15px rgba(14, 165, 233, 0.3);
            border-color: var(--sky-light);
        }
        .route-city {
            font-family: 'Space Grotesk', sans-serif;
            font-size: 1.55rem;
            font-weight: 700;
            color: #fff;
            margin: 0 0 .3rem;
            letter-spacing: -0.5px;
        }
        .route-sub {
            font-size: .8rem;
            color: rgba(255, 255, 255, 0.75);
            text-transform: uppercase;
            letter-spacing: 1.2px;
            font-weight: 600;
        }

        /* scroll controls */
        .scroll-btn {
            width: 48px; height: 48px;
            border-radius: 50%;
            border: 1px solid var(--border);
            background: rgba(255, 255, 255, 0.8);
            backdrop-filter: blur(10px);
            color: var(--text);
            font-size: 1.2rem;
            display: inline-flex; align-items: center; justify-content: center;
            cursor: pointer;
            transition: all .3s cubic-bezier(0.16, 1, 0.3, 1);
            box-shadow: var(--shadow-sm);
        }
        .scroll-btn:hover {
            background: linear-gradient(135deg, var(--sky-light), var(--sky-dark));
            color: #fff;
            border-color: var(--sky-light);
            box-shadow: 0 0 20px rgba(14, 165, 233, 0.3);
            transform: translateY(-2px);
        }

        /* ─── Stats Strip ────────────────────────────────── */
        .stats-strip {
            background: linear-gradient(135deg, #ffffff, #f0f9ff);
            position: relative;
            overflow: hidden;
            border-top: 1px solid var(--border);
            border-bottom: 1px solid var(--border);
            padding: 2.5rem 0;
        }
        .stat-card-wrap {
            background: rgba(255, 255, 255, 0.85);
            border: 1px solid var(--border);
            border-radius: 20px;
            padding: 2.2rem 1.5rem;
            text-align: center;
            transition: all 0.3s ease;
            height: 100%;
            box-shadow: var(--shadow-sm);
        }
        .stat-card-wrap:hover {
            background: #ffffff;
            border-color: rgba(14, 165, 233, 0.2);
            transform: translateY(-5px);
            box-shadow: var(--shadow-md);
        }
        .stat-number {
            font-family: 'Space Grotesk', sans-serif;
            font-size: 2.8rem;
            font-weight: 700;
            margin-bottom: .3rem;
            letter-spacing: -1px;
            background: linear-gradient(135deg, var(--sky-dark) 30%, var(--sky-light) 100%);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
        }
        .stat-label {
            font-size: .8rem;
            color: var(--text-muted);
            font-weight: 700;
            letter-spacing: 1px;
            text-transform: uppercase;
        }

        /* ─── CTA ────────────────────────────────────────── */
        .cta-section {
            background: radial-gradient(circle at 10% 10%, rgba(14, 165, 233, 0.04) 0%, transparent 40%), radial-gradient(circle at 90% 90%, rgba(34, 211, 238, 0.04) 0%, transparent 40%), #ffffff;
            padding: 8rem 0;
            border-top: 1px solid var(--border);
            border-bottom: 1px solid var(--border);
            position: relative;
        }
        .btn-cta-primary {
            background: linear-gradient(135deg, var(--sky-light), var(--sky-dark));
            color: #fff;
            font-weight: 800;
            border: none;
            border-radius: 14px;
            padding: 1.1rem 2.6rem;
            font-size: .98rem;
            text-decoration: none;
            display: inline-block;
            transition: all .3s ease;
            box-shadow: 0 5px 25px rgba(14, 165, 233, 0.25);
        }
        .btn-cta-primary:hover {
            transform: translateY(-3px);
            box-shadow: 0 12px 30px rgba(14, 165, 233, 0.45);
            color: #fff;
        }
        .btn-cta-outline {
            background: transparent;
            color: var(--text);
            font-weight: 800;
            border: 2px solid rgba(15, 23, 42, 0.12);
            border-radius: 14px;
            padding: 1rem 2.6rem;
            font-size: .98rem;
            text-decoration: none;
            display: inline-block;
            transition: all .3s ease;
        }
        .btn-cta-outline:hover {
            background: rgba(15, 23, 42, 0.02);
            border-color: rgba(15, 23, 42, 0.25);
        }

        /* ─── Footer ─────────────────────────────────────── */
        .footer-main {
            background: #0f172a; /* Keeping a high-contrast dark footer which frames the design beautifully */
            color: #94a3b8;
            padding: 6rem 0 2.5rem;
            border-top: 1px solid var(--border);
        }
        .footer-brand { font-size: 1.5rem; font-weight: 800; color: #fff; }
        .footer-desc { font-size: .92rem; color: #475569; line-height: 1.8; }
        .footer-heading {
            font-size: .78rem;
            font-weight: 800;
            text-transform: uppercase;
            letter-spacing: 2px;
            color: #334155;
            margin-bottom: 1.4rem;
        }
        .footer-link {
            display: block;
            color: #94a3b8;
            text-decoration: none;
            font-size: .92rem;
            margin-bottom: .85rem;
            transition: all .3s ease;
        }
        .footer-link:hover { color: #fff; transform: translateX(4px); }
        .newsletter-wrap .form-control {
            background: rgba(255, 255, 255, 0.02);
            border: 1px solid rgba(255, 255, 255, 0.08);
            color: #fff;
            border-radius: 12px 0 0 12px;
            font-size: .92rem;
            padding: .7rem 1.1rem;
        }
        .newsletter-wrap .form-control::placeholder { color: #334155; }
        .newsletter-wrap .form-control:focus { box-shadow: none; border-color: rgba(14, 165, 233, 0.4); background: rgba(255, 255, 255, 0.04); }
        .newsletter-wrap .btn {
            background: linear-gradient(135deg, var(--sky-light), var(--sky-dark));
            border: none;
            color: #fff;
            border-radius: 0 12px 12px 0;
            font-size: .92rem;
            padding: .7rem 1.4rem;
            font-weight: 700;
        }
        .newsletter-wrap .btn:hover {
            box-shadow: 0 0 15px rgba(14, 165, 233, 0.3);
        }
        .social-btn {
            display: inline-flex; align-items: center; justify-content: center;
            width: 40px; height: 40px;
            border-radius: 12px;
            background: rgba(255, 255, 255, 0.02);
            color: #94a3b8;
            font-size: 1.1rem;
            text-decoration: none;
            transition: all .3s ease;
            margin-right: .6rem;
            border: 1px solid rgba(255, 255, 255, 0.05);
        }
        .social-btn:hover { background: var(--sky-light); color: #fff; border-color: var(--sky-light); transform: translateY(-3px) rotate(5deg); box-shadow: 0 5px 15px rgba(14, 165, 233, 0.3); }
        .footer-bottom {
            border-top: 1px solid rgba(255, 255, 255, 0.05);
            margin-top: 5rem;
            padding-top: 2.2rem;
            font-size: .85rem;
            color: #334155;
        }
        .footer-bottom a { color: #334155; text-decoration: none; margin-left: 1.8rem; transition: color 0.3s; }
        .footer-bottom a:hover { color: var(--text-muted); }

        /* ─── Responsive Tweaks ──────────────────────────── */
        @media (max-width: 767.98px) {
            .hero { min-height: 100svh; padding-top: 90px; }
            .hero-title { font-size: 2.4rem; }
            .search-card { padding: 1.3rem; }
            .route-card { flex: 0 0 240px; width: 240px; height: 320px; }
            .stat-number { font-size: 2.2rem; }
            .partner-tag { margin: .4rem .9rem; }
            .footer-bottom { text-align: center; }
            .footer-bottom a { margin: 0 .7rem; display: inline-block; }
        }
"""

content_updated = content[:start_idx + len(start_tag)] + new_style + content[end_idx:]

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content_updated)

print("Landing page style successfully updated to Light Theme!")
