
# 📅 Event Management – Android App

An Android application that allows students to register for events, admins to create/manage events, and attendance to be marked using QR codes.

The app is structured in two main layers:
- **Frontend (Android UI + Kotlin Logic)**
- **Backend (Firebase Services: Auth, Firestore, Storage)**

---

## 🎨 Frontend (Android App)

### 🧩 UI Technology  
- **Language:** Kotlin  
- **UI:** XML Layouts + Material Components  
- **Architecture:** Activities, Fragments & Adapters  
- **Navigation:** Navigation Drawer  
- **Lists:** RecyclerView (Events list, Registrations list)  
- **QR Scanner UI:** Camera preview + ZXing scanner view  

---

### 📱 Frontend Features

#### 👤 User Features
- Login & Sign Up  
- Event listing  
- Event details  
- Register for event  
- My Registrations  
- Profile Screen  

#### 🛠 Admin Features
- Admin Dashboard  
- Create Event  
- View All Events  
- QR Code Scanner  

---

### 📁 Frontend Folder Structure
```

app/
└── java/com/eventmanagement/
├── fragments/
├── adapters/
├── LoginActivity.kt
├── SignUpActivity.kt
├── MainActivity.kt
├── AdminActivity.kt
├── CreateEventActivity.kt
├── QRScannerActivity.kt
└── SplashActivity.kt

````

---

### 🎨 Frontend Dependencies
```kotlin
implementation("androidx.appcompat:appcompat:1.7.0")
implementation("com.google.android.material:material:1.12.0")
implementation("androidx.recyclerview:recyclerview:1.3.2")

// QR Code
implementation("com.google.zxing:core:3.5.3")
implementation("com.journeyapps:zxing-android-embedded:4.3.0")
````

---

## 🗄️ Backend (Firebase)

### 🔧 Backend Technologies

* Firebase Authentication
* Cloud Firestore
* Firebase Storage
* Firebase KTX SDK

---

### 🧠 Backend Responsibilities

#### 🔐 Authentication (Firebase Auth)

* User login
* Signup (email/password)
* Session management
* Role assignment (student/admin)

#### 🗂 Firestore Database

Stores:

* Users
* Events
* Registrations
* Attendance

Collections used:

```
users/
events/
registrations/
attendance/
```

#### 🖼 Storage (Optional)

* Event images

#### ⚙️ Backend Logic Layer

Located in:

```
app/java/com/eventmanagement/data/
```

`FirebaseRepository.kt` handles:

* User creation
* Login
* Creating events
* Fetching events
* Registering for an event
* Fetching user's registrations
* Updating attendance via QR scan

---

---

## 🏗️ How to Run the Project

### 1️⃣ Clone the Repository

```bash
git clone https://github.com/<your-username>/EventManagement-Android.git
cd EventManagement-Android
```

### 2️⃣ Add Firebase Configuration

Download `google-services.json` from Firebase and place it here:

```
app/google-services.json
```




### 3️⃣ Build & Run

* Open in Android Studio
* Sync Gradle
* Connect an Android device
* Run ▶

---



---

# 📸 Screenshots

### **1) Splash Screen**

![Splash](https://github.com/user-attachments/assets/78596a1d-711f-4c87-bdc9-1e697ea62e29)

### **2) Login Screen**

![Login Page](https://github.com/user-attachments/assets/db703960-6e95-41a3-8184-750a731ec2f1)

### **3) User Profile**

![User Profile](https://github.com/user-attachments/assets/5485ec80-9a45-47d5-823b-03ef5a2d0528)

### **4) Event Lists**

![EventLists](https://github.com/user-attachments/assets/9e4c15a0-08e1-4722-91fe-82082057b04c)

### **5) Admin Panel**

![Admin Panel](https://github.com/user-attachments/assets/1d00fc79-f6af-4922-aea0-21ec2f3f34eb)

### **6) Create Event**

![Create Event](https://github.com/user-attachments/assets/df557d9a-e938-4ef0-b678-16a9c1b3eedd)

---




