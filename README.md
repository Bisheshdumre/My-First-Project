

#  My First Project (Android)

An Android application built with Kotlin and Gradle.  
This project demonstrates modern Android development practices with a clean structure and Gradle build system.

---

##  Features
- Developed in Kotlin  
- Built with Gradle (KTS) 
- Supports Android Studio build and run  
- Includes unit tests and instrumented tests 

---

##  Application Architecture
- Language: Kotlin  
- Build System:** Gradle (Kotlin DSL)  
- IDE: Android Studio  
- Minimum SDK: 26
- Target SDK: 36


## ⚙️ Setup

### Clone the repository
```bash
git clone https://github.com/bisheshdumre/My-First-Project.git

## License
This project is licensed under the MIT License – you are free to use, modify, and distribute it.
Open in Android Studio

Open Android Studio.

Select File → Open… and choose this folder.

Let Gradle sync complete.

▶️ Run the Application

Select an emulator or physical device.

Click Run ▶ in Android Studio.

🛠️ Build via Command Line
./gradlew assembleDebug


The APK will be generated in:

app/build/outputs/apk/debug/app-debug.apk

🧪 Running Tests
Unit tests
./gradlew test

Instrumented tests (requires emulator/device)
./gradlew connectedAndroidTest

