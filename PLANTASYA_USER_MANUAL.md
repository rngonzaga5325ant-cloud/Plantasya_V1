# PLANTASYA MOBILE APPLICATION
## OFFICIAL COMPREHENSIVE USER MANUAL & OPERATING GUIDE

**App Name:** Plantasya (`Plantasya_MobileApp`)  
**Package Name:** `com.example.plantasya_mobileapp`  
**Target Platform:** Android OS (Version 8.0 / API 26 and higher)  
**Document Version:** 1.0.0  
**Target Audience:** Non-Technical Users, Home Gardeners, Houseplant Enthusiasts, and Application Administrators  

---

# TABLE OF CONTENTS
1. **Document Overview & Welcome Preface**
2. **Chapter 1: Introduction to Plantasya**
   - 1.1 Application Purpose & Core Mission
   - 1.2 Target Audience & User Profiles
   - 1.3 Key Features Summary
3. **Chapter 2: System Requirements & Device Permissions**
   - 2.1 Hardware and Software Requirements
   - 2.2 Permissions Guide (Camera, Storage, Notifications, Exact Alarms)
   - 2.3 Granting Permissions Step-by-Step
4. **Chapter 3: Account Creation, Login & Session Management**
   - 3.1 Application Splash Screen & Launch
   - 3.2 Landing Screen Navigation
   - 3.3 Registering a New Account
   - 3.4 Profile Setup (Avatar Upload & Display Name)
   - 3.5 Logging In
   - 3.6 Session Persistence & Auto-Login
   - 3.7 Changing Password and Credentials
   - 3.8 Logging Out
   - 3.9 Account Deletion and Data Removal
5. **Chapter 4: Main Dashboard & Daily Plant Care Scheduling**
   - 4.1 Dashboard Interface Layout
   - 4.2 Owned Plants Carousel Navigation
   - 4.3 Empty State & Adding Plants
   - 4.4 Automated Task Generation
   - 4.5 Completing Daily Tasks (Mark as Done)
   - 4.6 Setting Custom Care Reminders (Date & Time Picker)
   - 4.7 Android Alarm Manager & System Push Notifications
6. **Chapter 5: AI Plant Identification & Camera Scanning**
   - 5.1 On-Device TensorFlow Lite AI Recognition
   - 5.2 Opening the AI Scanner
   - 5.3 Camera Framing and Lighting Recommendations
   - 5.4 Photo Capture & Real-Time Classification
   - 5.5 Reviewing Results & Confirming Ownership
   - 5.6 Handling Low Confidence and Unknown Plant Scans
7. **Chapter 6: Botanical Plant Library & Catalog**
   - 6.1 Browsing the Botanical Catalog
   - 6.2 Real-Time Search Bar
   - 6.3 Alphabetical Indexing (A-Z Headers)
   - 6.4 Detailed Plant Profiles & Care Specifications
   - 6.5 External Web Search Redirection for Plant Variations
8. **Chapter 7: Interactive 3D Plant Model Viewer**
   - 7.1 Overview of 3D Rendering (Google Filament Engine)
   - 7.2 Launching 3D View Mode
   - 7.3 Touch Gestures (Rotate 360°, Zoom, Pan)
   - 7.4 Omni-Directional 10-Point Studio Lighting
   - 7.5 Using and Dismissing Gesture Overlay Guides
9. **Chapter 8: Scan History & Activity Logging**
   - 8.1 History Feature Overview
   - 8.2 Chronological History List
   - 8.3 Converting Historical Scans to Owned Dashboard Plants
10. **Chapter 9: Profile Customization & Onboarding**
    - 9.1 Profile Tab Features
    - 9.2 Interactive Application Tutorial
    - 9.3 App Info & Version Modal ("About App")
11. **Chapter 10: Step-by-Step Operating Procedures (10 Common Workflows)**
    - Workflow 1: New Account Sign-Up & Profile Customization
    - Workflow 2: Scanning a Plant with the AI Camera
    - Workflow 3: Manually Adding a Plant from Library to Dashboard
    - Workflow 4: Setting Custom Care Alarms
    - Workflow 5: Viewing and Interacting with 3D Models
    - Workflow 6: Managing History Log & Upgrading Scans
    - Workflow 7: Updating Username and Security Password
    - Workflow 8: Removing an Owned Plant and Clearing Alarms
    - Workflow 9: Inspecting Pet Toxicity Specs
    - Workflow 10: Logging Out and Deleting Account
12. **Chapter 11: Complete Botanical Catalog Reference Guide (All 10 Pre-loaded Species)**
13. **Chapter 12: Troubleshooting, FAQs & Maintenance**

---

# DOCUMENT OVERVIEW & WELCOME PREFACE

Welcome to the official **Plantasya User Manual**. This document serves as a complete, accessible, non-technical operational manual for the Plantasya mobile application.

Whether you are caring for your very first indoor houseplant or maintaining a diverse collection of tropical plants, Plantasya simplifies plant identification, care task tracking, reminder scheduling, and botanical learning.

No previous technical or IT experience is needed to read and follow this manual. Every chapter includes plain-language descriptions, clear layout diagrams, tabular guides, and step-by-step procedures.

---

# CHAPTER 1: INTRODUCTION TO PLANTASYA

### 1.1 Application Purpose & Core Mission
**Plantasya** is an intelligent mobile plant care management system designed for Android smartphones. The application serves three key functions:
1. **Instant AI Plant Recognition:** Identifies plant species in under a second using your phone's camera and built-in artificial intelligence.
2. **Automated Plant Care Scheduling:** Calculates tailored watering, fertilizing, leaf dusting, and soil check schedules based on plant species.
3. **Interactive Botanical Education:** Provides comprehensive care guidelines, pet toxicity advisories, height ranges, space footprint requirements, and interactive 3D models.

### 1.2 Target Audience & User Profiles
* **Homeowners & Apartment Dwellers:** Keep indoor house plants healthy without forgetting care routines.
* **Pet Owners:** Quickly verify if a house plant is safe or toxic to cats and dogs.
* **Beginner Gardeners:** Access clear, simple guidelines on light, humidity, temperature, and soil needs.
* **Plant Collectors:** Build a digital catalog of owned plants and maintain an activity log of all scanned plants.

### 1.3 Key Features Summary
* **AI Camera Scanner:** On-device TensorFlow Lite classification for offline plant identification.
* **Personal Dashboard:** Carousel display showing all owned plants and active daily care tasks.
* **Task Generator & Alarm System:** Automatically schedules care tasks and triggers exact Android alarm notifications.
* **Botanical Encyclopedia:** Searchable catalog with care specifications for pre-loaded plant species.
* **Interactive 3D Model Viewer:** Real-time 3D plant models with 360-degree touch rotation, zoom, and panning.
* **Scan History Log:** Stores photos, dates, times, and species names for every scanned plant.

---

# CHAPTER 2: SYSTEM REQUIREMENTS & DEVICE PERMISSIONS

### 2.1 Hardware and Software Requirements
* **Operating System:** Android 8.0 (Oreo / API Level 26) or higher.
* **Camera:** Rear camera with autofocus capability.
* **Internal Storage:** Minimum 150 MB free space.
* **Screen Display:** HD Resolution (720p or higher) recommended.

### 2.2 Permissions Guide
| Permission Name | Prompt Context | Purpose in Application |
|---|---|---|
| `CAMERA` | First time opening AI Scanner | Captures live photos for plant identification. |
| `READ_MEDIA_IMAGES` / `READ_EXTERNAL_STORAGE` | Selecting avatar in Profile Setup | Loads custom profile pictures from photo gallery. |
| `POST_NOTIFICATIONS` (Android 13+) | First launch of MainActivity | Displays push notification care alerts. |
| `SCHEDULE_EXACT_ALARM` (Android 12+) | First launch prompt | Triggers precise watering and care alarms at exact set times. |

### 2.3 Granting Permissions Step-by-Step
1. **Notifications:** Tap **"Allow"** when prompted on first launch to ensure care alarms ring on time.
2. **Exact Alarms:** On Android 12+, if a dialog titled *"Exact Alarms Required"* appears, tap **"Settings"**, turn on the toggle switch for Plantasya, and return to the app.
3. **Camera:** Tap **"While using the app"** when launching the camera scanner for the first time.

---

# CHAPTER 3: ACCOUNT CREATION, LOGIN & SESSION MANAGEMENT

### 3.1 Application Splash Screen & Launch
* On opening Plantasya, a dark splash screen displays the central **Plantasya Logo** performing a smooth pulsing animation for 3 seconds while system resources load.
* Returning logged-in users bypass the landing page and land directly on the **Dashboard**.

### 3.2 Landing Screen Navigation
New or logged-out users see two main buttons:
* **LOG IN:** Directs existing users to enter credentials.
* **SIGN UP:** Directs new users to register a new account.

### 3.3 Registering a New Account
1. Tap **SIGN UP** on the Landing Screen.
2. Enter a unique **Username**.
3. Enter a **Password** and re-enter it in **Confirm Password**.
4. (Optional) Check **Show Password** to unhide and verify typed characters.
5. Tap **SIGN UP**. Validation checks ensure fields are not blank and passwords match.

### 3.4 Profile Setup (Avatar Upload & Display Name)
After registering:
1. Tap the circular photo frame to choose a profile image (PNG or JPEG, under 10 MB).
2. Enter your preferred **Display Name** (e.g., *"Maria Gardener"*).
3. Tap **SAVE PROFILE** to complete registration.

### 3.5 Logging In
1. Tap **LOG IN** on the Landing Screen.
2. Enter your **Username** and **Password**.
3. Tap **LOG IN**. Successful authentication navigates to your personal Dashboard.

### 3.6 Session Persistence & Auto-Login
Plantasya saves your session token securely. Closing the app or restarting your phone will **not** log you out. You remain signed in until you choose to log out manually.

### 3.7 Changing Password and Credentials
1. Go to **Profile** > **Settings** > **Change Credentials**.
2. Type your current username and password.
3. Type your new username and new password.
4. Tap **SAVE CHANGES**.

### 3.8 Logging Out
1. Go to **Profile** > **Settings** > **Log Out**.
2. Confirm the logout prompt by tapping **YES**.

### 3.9 Account Deletion and Data Removal
1. Go to **Profile** > **Settings** > **Delete Account**.
2. Tap **DELETE** on the warning prompt.
3. Your user account, profile image, owned plant list, scan history, and active alarms are permanently removed from the local database.

---

# CHAPTER 4: MAIN DASHBOARD & DAILY PLANT CARE SCHEDULING

### 4.1 Dashboard Interface Layout
The Dashboard displays:
* **Top Header:** Section title *"OWNED PLANTS:"* and current account avatar.
* **Center Section:** 3D animated visual carousel showing cards for owned plants.
* **Bottom Section:** Dynamic list of daily care tasks corresponding to the selected carousel plant.
* **Action Button:** Green **+ ADD PLANT** button.

### 4.2 Owned Plants Carousel Navigation
* Drag your finger left or right across the top carousel to cycle through your plants.
* The focused plant card scales up and centers on screen.
* The care task list below **instantly updates** to display tasks for the selected plant!

### 4.3 Empty State & Adding Plants
If no plants are owned:
* The dashboard displays: *"No owned plants found. Tap Add Plant to get started!"*.
* Tapping **+ ADD PLANT** offers two options:
  * **SCAN PLANT:** Launches camera AI scanner.
  * **CHOOSE FROM LIBRARY:** Opens the botanical catalog.

### 4.4 Automated Task Generation
Adding a plant automatically generates care schedules:
* *Aglaonema:* Watering (Every 7-10 days), Fertilizing (Monthly), Leaf Cleaning (Every 2 weeks).
* *Pothos:* Watering (Every 7 days), Fertilizing (Monthly), Pruning (Monthly).
* *Calathea:* Watering (Every 5-7 days), Humidity Check (Daily), Fertilizing (Monthly).
* *Snake Plant:* Watering (Every 21-30 days), Fertilizing (Monthly), Check Soil (Every 2 weeks).

### 4.5 Completing Daily Tasks (Mark as Done)
Tap the checkbox next to any task card to mark it as complete. The completion status and timestamp are saved in the database.

### 4.6 Setting Custom Care Reminders (Date & Time Picker)
1. Tap the **Clock Icon** next to any task card.
2. Select desired date on the Calendar dialog and tap **OK**.
3. Select desired hour and minute on the Clock dialog and tap **OK**.
4. The custom alarm time is registered in the Android Alarm System.

### 4.7 Android Alarm Manager & System Push Notifications
* Plantasya uses Android system alarms (`RTC_WAKEUP`).
* Alarms ring even when the app is closed or phone is locked.
* Rebooting your device automatically restores all scheduled reminders.

---

# CHAPTER 5: AI PLANT IDENTIFICATION & CAMERA SCANNING

### 5.1 On-Device TensorFlow Lite AI Recognition
* Plantasya runs an offline TensorFlow Lite deep learning model directly on your phone.
* Scans require a **75% minimum confidence score** to ensure accurate identification.

### 5.2 Opening the AI Scanner
Access the camera scanner via:
* **Dashboard:** Tap **+ ADD PLANT** > **SCAN PLANT**.
* **History Tab:** Tap the round floating camera button.

### 5.3 Camera Framing and Lighting Recommendations
* **Lighting:** Use clear, well-lit natural indoor or daylight conditions.
* **Distance:** Hold phone camera 1 to 2 feet away from plant foliage.
* **Framing:** Center main leaf structure within the camera guide overlay.

### 5.4 Photo Capture & Real-Time Classification
1. Point camera at plant leaves and tap the circular **Capture Button**.
2. Photo is processed through TensorFlow Lite in under 1 second.

### 5.5 Reviewing Results & Confirming Ownership
The **Scan Preview Screen** shows the photo snapshot and predicted plant species:
* **"YES, I OWN THIS PLANT":** Saves scan to history, adds plant to dashboard carousel, and generates care tasks.
* **"NO, JUST SEARCHING / VIEWING":** Logs scan in history without adding to dashboard.

### 5.6 Handling Low Confidence and Unknown Plant Scans
If confidence is below 75% or leaf is obscured:
* Result shows **"Unknown Plant"**.
* Buttons are replaced with a single **"TRY SCANNING AGAIN"** button to retake photo under better conditions.

---

# CHAPTER 6: BOTANICAL PLANT LIBRARY & CATALOG

### 6.1 Browsing the Botanical Catalog
Tap the **Library** tab (book icon) on the bottom menu bar to access the offline plant encyclopedia.

### 6.2 Real-Time Search Bar
Type any plant name or scientific name into the top search bar. The list filters instantly with every keystroke.

### 6.3 Alphabetical Indexing (A-Z Headers)
Plants are grouped under clear alphabetical section headers (**A, C, D, M, O, P, S, Z**).

### 6.4 Detailed Plant Profiles & Care Specifications
Selecting any plant card opens its complete profile including:
* Common & Scientific Name
* Full Description
* Light, Water, Humidity, Temperature, Soil, and Fertilizer Requirements
* Toxicity Status for Pets
* Height Range, Space Footprint, and Recommended Indoor Uses
* Cultivar Variations

### 6.5 External Web Search Redirection for Plant Variations
Tapping listed variation names prompts a safety dialog: *"Leaving Application"*. Tapping **CONTINUE** safely opens Google search results in your default browser for that cultivar.

---

# CHAPTER 7: INTERACTIVE 3D PLANT MODEL VIEWER

### 7.1 Overview of 3D Rendering (Google Filament Engine)
Plantasya integrates Google Filament PBR rendering engine to render realistic, interactive 3D models of houseplants.

### 7.2 Launching 3D View Mode
From any plant detail page in the Library that supports 3D, tap **VIEW INTERACTIVE 3D MODEL** at the bottom.

### 7.3 Touch Gestures (Rotate 360°, Zoom, Pan)
* **Single Finger Drag:** Rotates 3D model 360 degrees horizontally and vertically.
* **Pinch / Spread Two Fingers:** Zooms camera in and out.
* **Two Finger Drag:** Pans camera position up, down, left, or right.

### 7.4 Omni-Directional 10-Point Studio Lighting
A custom 10-point studio lighting setup surrounds the 3D model from top, bottom, sides, and diagonal corners, eliminating dark shadows during rotation.

### 7.5 Using and Dismissing Gesture Overlay Guides
On launch, a semi-transparent gesture guide box explains touch controls. Tap **"GOT IT!"** or tap anywhere on screen to dismiss the overlay.

---

# CHAPTER 8: SCAN HISTORY & ACTIVITY LOGGING

### 8.1 History Feature Overview
Tap the **History** tab to view your complete chronological activity log of all scanned plants.

### 8.2 Chronological History List
Each entry displays:
* Captured plant photo thumbnail
* Species name
* Exact date and timestamp
* Ownership status heart icon

### 8.3 Converting Historical Scans to Owned Dashboard Plants
* Tap the hollow heart icon to add a past scan to your dashboard collection.
* Tap a filled green heart icon to remove a plant from your dashboard.

---

# CHAPTER 9: PROFILE CUSTOMIZATION & ONBOARDING

### 9.1 Profile Tab Features
Displays current user avatar, display name, and username alongside core administrative buttons.

### 9.2 Interactive Application Tutorial
Tap **Profile** > **App Tutorial** to launch a full-screen, swipeable onboarding tutorial guiding you through app features.

### 9.3 App Info & Version Modal ("About App")
Tap **Profile** > **About App** to view application version (1.0.0), credits, and architecture info.

---

# CHAPTER 10: STEP-BY-STEP OPERATING PROCEDURES (WORKFLOWS)

### WORKFLOW 1: User Registration & Profile Setup
1. Open Plantasya > Tap **SIGN UP**.
2. Enter Username, Password, and Confirm Password. Tap **SIGN UP**.
3. Tap camera circle frame > Select photo from gallery.
4. Type Display Name > Tap **SAVE PROFILE**.

### WORKFLOW 2: Scanning a Plant with AI Camera
1. Tap **Dashboard** > **+ ADD PLANT** > **SCAN PLANT**.
2. Frame plant leaves in camera guide under good light.
3. Tap **Capture Button**.
4. Tap **"YES, I OWN THIS PLANT"**. Plant & tasks appear on Dashboard!

### WORKFLOW 3: Manually Adding Plant from Library
1. Open **Library** tab > Search plant name (e.g., *"Pothos"*).
2. Tap plant card to open details.
3. Tap **Hollow Heart Icon** in top right header.

### WORKFLOW 4: Setting Custom Care Alarms
1. Go to **Dashboard** > Select plant card in carousel.
2. Tap **Clock Icon** next to target task.
3. Select Date on Calendar > Tap **OK**.
4. Select Time on Clock > Tap **OK**.

### WORKFLOW 5: Viewing 3D Interactive Models
1. Open **Library** > Select plant (e.g., *"Peace Lily"*).
2. Tap **VIEW INTERACTIVE 3D MODEL**.
3. Tap **"GOT IT!"** > Drag finger to rotate plant 360 degrees.

### WORKFLOW 6: Managing History Scans
1. Tap **History** tab on bottom menu bar.
2. Scroll to past scan item.
3. Tap **Hollow Heart Icon** to convert into an owned dashboard plant.

### WORKFLOW 7: Updating Username and Password
1. Tap **Profile** > **Settings** > **Change Credentials**.
2. Enter current credentials in top fields.
3. Enter new credentials in lower fields > Tap **SAVE CHANGES**.

### WORKFLOW 8: Removing an Owned Plant
1. Open **Library** > Select owned plant.
2. Tap **Filled Green Heart Icon**.
3. Confirm prompt by tapping **REMOVE**.

### WORKFLOW 9: Checking Pet Toxicity Before Buying
1. Open **Library** > Search plant species name.
2. Open plant details > Scroll to **Toxicity Status**.
3. Check pet safety advisory (e.g., *"Non-toxic to pets"*).

### WORKFLOW 10: Deleting User Account
1. Go to **Profile** > **Settings** > **Delete Account**.
2. Tap **DELETE** on the warning prompt. Account and all data are erased.

---

# CHAPTER 11: COMPLETE BOTANICAL CATALOG REFERENCE GUIDE

### 1. AGLAONEMA (Chinese Evergreen)
* **Scientific Name:** *Aglaonema commutatum*
* **Description:** Glossy patterned evergreen leaves; low-maintenance houseplant.
* **Light:** Low to bright indirect light.
* **Water:** Water when top 1-2 inches dry.
* **Humidity:** Moderate humidity.
* **Temperature:** 65-80°F (18-27°C).
* **Soil:** Well-draining mix with perlite.
* **Fertilizer:** Monthly in spring/summer.
* **Toxicity:** Toxic to pets if ingested.
* **Height:** 1–3 ft. **Footprint:** Compact tabletop/shelf plant.
* **Variations:** 'Emerald', 'Silver Queen'.

### 2. POTHOS (Devil's Ivy)
* **Scientific Name:** *Epipremnum aureum*
* **Description:** Fast-growing trailing vine with heart-shaped leaves.
* **Light:** Low to bright indirect light.
* **Water:** Water when top 2 inches dry.
* **Humidity:** Average humidity.
* **Temperature:** 65-85°F (18-29°C).
* **Soil:** All-purpose potting mix.
* **Fertilizer:** Monthly liquid fertilizer.
* **Toxicity:** Toxic to pets.
* **Height:** Trailing 6–10 ft. **Footprint:** Minimal floor space; hanging/shelf.
* **Variations:** Golden, Marble Queen, Neon.

### 3. CALATHEA (Peacock Plant)
* **Scientific Name:** *Calathea spp.*
* **Description:** Highly decorative foliage with unique leaf movement.
* **Light:** Low to medium indirect light.
* **Water:** Keep evenly moist; use filtered water.
* **Humidity:** High humidity required.
* **Temperature:** 65-80°F (18-27°C).
* **Soil:** Organic rich, slightly acidic mix.
* **Fertilizer:** Monthly diluted fertilizer.
* **Toxicity:** **Non-toxic** to pets.
* **Height:** 1–2 ft. **Footprint:** Tabletop / shelf plant.
* **Variations:** Medallion, Rattlesnake.

### 4. MONEY TREE
* **Scientific Name:** *Pachira aquatica*
* **Description:** Braided trunk tree symbolizing good fortune.
* **Light:** Bright indirect light.
* **Water:** Water when top 2-3 inches dry.
* **Humidity:** Moderate humidity.
* **Temperature:** 65-80°F (18-27°C).
* **Soil:** Moisture retentive well-draining mix.
* **Fertilizer:** Every 4-6 weeks in spring/summer.
* **Toxicity:** Non-toxic to pets.
* **Height:** 3–6 ft. **Footprint:** Medium to large floor plant.

### 5. ORCHID (Moth Orchid)
* **Scientific Name:** *Phalaenopsis*
* **Description:** Epiphytic plant with long-lasting showy blooms.
* **Light:** Bright indirect light.
* **Water:** Water every 7-10 days; let bark dry slightly.
* **Humidity:** 50-70% humidity.
* **Temperature:** 60-80°F (16-27°C).
* **Soil:** Open chunky orchid bark mix.
* **Fertilizer:** Orchid fertilizer every 2-4 weeks.
* **Toxicity:** **Non-toxic** to pets.
* **Height:** 8–24 in. **Footprint:** Compact windowsill/tabletop.

### 6. PARLOR PALM
* **Scientific Name:** *Chamaedorea elegans*
* **Description:** Graceful slow-growing indoor palm tree.
* **Light:** Low to medium indirect light.
* **Water:** Keep evenly moist.
* **Humidity:** Moderate humidity.
* **Temperature:** 65-80°F (18-27°C).
* **Soil:** Organic rich well-draining soil.
* **Fertilizer:** Monthly spring/summer.
* **Toxicity:** **Non-toxic** to pets.
* **Height:** 2–6 ft. **Footprint:** Floor corner plant.

### 7. ZZ PLANT
* **Scientific Name:** *Zamioculcas zamiifolia*
* **Description:** Glossy, upright foliage; extremely durable.
* **Light:** Low to bright indirect light.
* **Water:** Highly drought tolerant; allow complete drying.
* **Humidity:** Average home humidity.
* **Temperature:** 65-85°F (18-29°C).
* **Soil:** Well-draining cactus/succulent mix.
* **Fertilizer:** Every 6-8 weeks.
* **Toxicity:** Toxic if ingested.
* **Height:** 1–3 ft. **Footprint:** Compact tabletop or small floor pot.
* **Variations:** Standard, 'Raven'.

### 8. PEACE LILY
* **Scientific Name:** *Spathiphyllum spp.*
* **Description:** Deep green leaves with white spathe blooms.
* **Light:** Low to bright indirect light.
* **Water:** Keep lightly moist; wilts when dry.
* **Humidity:** Moderate to high humidity.
* **Temperature:** 65-85°F (18-29°C).
* **Soil:** Rich well-draining potting soil.
* **Fertilizer:** 3 times per year.
* **Toxicity:** Mildly toxic to pets.
* **Height:** 1–4 ft. **Footprint:** Floor or large tabletop plant.

### 9. SNAKE PLANT
* **Scientific Name:** *Dracaena trifasciata*
* **Description:** Architectural vertical sword-shaped leaves.
* **Light:** Low light to direct sun.
* **Water:** Allow soil to dry completely.
* **Humidity:** Low to moderate humidity.
* **Temperature:** 60-85°F (16-29°C).
* **Soil:** Cactus / succulent coarse mix.
* **Fertilizer:** Monthly spring/summer.
* **Toxicity:** Toxic to pets.
* **Height:** 1–4 ft. **Footprint:** Narrow vertical footprint.
* **Variations:** 'Laurentii', 'Silver Hahnii'.

### 10. DRACAENA
* **Scientific Name:** *Dracaena spp.*
* **Description:** Tropical tree with strap-like foliage on wooden canes.
* **Light:** Medium to bright indirect light.
* **Water:** Water when top 1-2 inches dry.
* **Humidity:** Moderate humidity.
* **Temperature:** 65-80°F (18-27°C).
* **Soil:** Organic rich well-draining mix.
* **Fertilizer:** Monthly during growth.
* **Toxicity:** Toxic to pets.
* **Height:** 2–10 ft. **Footprint:** Vertical floor plant.

---

# CHAPTER 12: TROUBLESHOOTING, FAQS & MAINTENANCE

### 12.1 Common Troubleshooting Scenarios
* **AI Camera says "Unknown Plant":** Move closer to the plant leaves (1-2 feet) and ensure clear lighting without glare.
* **Notifications/Alarms not ringing:** Go to Android Phone Settings > Apps > Plantasya > Enable Notifications & Allow Exact Alarms.
* **Profile picture upload fails:** Verify photo size is under 10 MB and in PNG or JPEG format.

### 12.2 Frequently Asked Questions (FAQs)
1. **Is an internet connection required?**  
   *No. Plantasya runs entirely offline including AI scanning, 3D viewing, and alarm scheduling.*
2. **Will I lose my plants if I restart my phone?**  
   *No. All data is permanently saved in the local Room database, and alarms auto-restore on boot.*
3. **Is Plantasya safe for pet owners?**  
   *Yes! The Botanical Library explicitly highlights pet toxicity status for every plant.*

---
*End of Manual - Plantasya Mobile Application Version 1.0.0*
