📅 Event Management – Android App
An Android application that allows students to register for events, admins to create/manage events, and attendance to be marked using QR codes.
The app is structured in two main layers:
•	Frontend (Android UI + Kotlin Logic)
•	Backend (Firebase Services: Auth, Firestore, Storage)
________________________________________
🎨 Frontend (Android App)
🧩 UI Technology
•	Language: Kotlin
•	UI: XML Layouts + Material Components
•	Architecture: Activities, Fragments & Adapters
•	Navigation: Navigation Drawer
•	Lists: RecyclerView (Events list, Registrations list)
•	QR Scanner UI: Camera preview + ZXing scanner view

📱 Frontend Features
👤 User
•	Login & Sign Up screens
•	Event listing screen
•	Event details page
•	Registration button
•	My Registrations list
•	Profile screen
🛠 Admin
•	Admin Dashboard
•	Create Event form
•	Admin Event list
•	QR Code scanner screen

📁 Frontend Folder Structure
app/
 └── java/com/eventmanagement/
      ├── fragments/             # Events UI, Profile UI, Registrations UI
      ├── adapters/              # RecyclerView adapters
      ├── LoginActivity.kt
      ├── SignUpActivity.kt
      ├── MainActivity.kt
      ├── AdminActivity.kt
      ├── CreateEventActivity.kt
      ├── QRScannerActivity.kt
      └── SplashActivity.kt
🎨 Frontend Dependencies
implementation("androidx.appcompat:appcompat:1.7.0")
implementation("com.google.android.material:material:1.12.0")
implementation("androidx.recyclerview:recyclerview:1.3.2")

// QR Code
implementation("com.google.zxing:core:3.5.3")
implementation("com.journeyapps:zxing-android-embedded:4.3.0")
________________________________________

🗄️ Backend (Firebase)
🔧 Backend Technologies
•	Firebase Authentication
•	Cloud Firestore
•	Firebase Storage
•	Realtime backend operations with Firebase SDK

🧠 Backend Responsibilities
🔐 Authentication (Firebase Auth)
•	User login
•	Registration with email/password
•	Token/session management
•	Role assignment (student/admin)

🗂 Firestore Database
Stores:
•	Users
•	Events
•	Registrations
•	Attendance information
Example Firestore collections:
users/
events/
registrations/
attendance/

🖼 Storage (Optional)
•	Store event images (if implemented)

⚙️ Backend Logic Layer
Located in:
app/java/com/eventmanagement/data/
FirebaseRepository.kt handles:
•	user creation
•	login
•	create event
•	get events
•	register user
•	get user registrations
•	QR attendance update

🗄️ Backend Dependencies
implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
implementation("com.google.firebase:firebase-auth-ktx")
implementation("com.google.firebase:firebase-firestore-ktx")
implementation("com.google.firebase:firebase-storage-ktx")
________________________________________
🏗️ How to Run the Project
1️⃣ Clone
git clone https://github.com/<your-username>/EventManagement-Android.git
cd EventManagement-Android
2️⃣ Add Firebase Config
Download google-services.json from Firebase
Place it in:
app/google-services.json
Do NOT upload this file to GitHub.
Add to .gitignore:
app/google-services.json
3️⃣ Build + Run
•	Open in Android Studio
•	Sync Gradle
•	Connect device → Run
________________________________________
📦 APK Download
(Replace this after uploading your APK)
👉 Download Latest APK
________________________________________
🔧 Build APK
Debug
Build → Build APK(s)
Release
Build → Generate Signed Bundle / APK
________________________________________
🌟 Future Enhancements
•	Push notifications
•	Event image gallery
•	Dark mode
•	CSV export for admins
•	Analytics dashboard

📸 Screenshots
1)Splash Screen

![Uploading WhatsApp Image 2025-11-20 at 17.06.49_c177672a.jpg…]()
