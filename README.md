# 📓 NoteWise

NoteWise is an intelligent note-taking Android application that auto-categorizes your notes using AI (Hugging Face) and keeps everything organized with a beautiful Jetpack Compose UI. Built with offline-first architecture and modern Android development practices.

## 🚀 Features

- 📝 Add, view, edit, and delete notes
- 🤖 Auto-categorize notes using Hugging Face API (Work, Personal, Tech, etc.)
- 🔍 Real-time search and category filter
- 🔐 Biometric authentication (Fingerprint, Face, or Device Credential)
- 📦 Offline support with Room database
- 🧠 Smart gibberish detection
- 🌙 Dynamic theming with Material3

## 🧪 Tech Stack

| Layer       | Libraries / Tools                                 |
|-------------|---------------------------------------------------|
| UI          | Jetpack Compose, Material3                        |
| Architecture| MVVM, Hilt (DI), ViewModel, Coroutine Flows       |
| Database    | Room, TypeConverters                              |
| API         | Retrofit, Hugging Face Inference API              |
| Auth        | BiometricPrompt, BiometricManager                 |
| Misc        | Kotlin, Gradle KTS, Scaffold, NavController       |

## 🛠️ Setup & Run

1. **Clone the repository:**
   ```bash
   git clone https://github.com/coderprem/NoteWise.git
   cd NoteWise
   ```

2. **Add Your Hugging Face API Key**
   
   Create a file:
   ```
   app/src/main/java/com/example/notewise/utils/Constants.kt
   ```
   
   And add the following code:
   ```kotlin
   package com.example.notewise.utils
   
   object Constants {
       const val HUGGING_FACE_API_KEY = "your_huggingface_api_key"
   }
   ```
   
   ⚠️ This file is added to .gitignore and will not be tracked in version control.

3. **Open in Android Studio**
   - Open the project using Android Studio (Hedgehog or newer recommended)
   - Connect an emulator or physical device (API level 23+)
   - Click the Run ▶️ button

## 🔐 Biometric Authentication

On app launch:
- Users are prompted with biometric authentication
- Devices without biometric support show fallback messages
- If no biometric is enrolled, users are redirected to settings

## 🧠 Note Auto-Categorization

The app uses Hugging Face's Zero-Shot Text Classification API to auto-classify notes into the following categories:
- 📂 Work
- 👤 Personal
- 💻 Tech
- 💸 Finance
- ❤️ Health
- 🛍️ Shopping
- 💡 Ideas
- ✈️ Travel
- 📇 Contacts

If the note has gibberish or doesn't match anything, it defaults to Ideas.
If a phone number is detected, it's auto-classified as Contacts.

## 📸 Screenshots

| Home Screen | Add Note |
|-------------|----------|
| ![Image](https://github.com/user-attachments/assets/74efe970-6d4c-46bf-8682-fdbc4e93e30b)     | ![Image](https://github.com/user-attachments/assets/dd96201b-b1cd-4cfb-8b00-b6ad0704d13b)  |

*Add your screenshots in the screenshots/ folder.*

## 💡 Future Improvements

- 🔄 Sync notes to cloud (Google Drive / Firebase)
- 🗃️ Custom user-defined categories
- 🌐 Language translation support
- 📅 Note reminders and notifications
- 🧪 UI Tests and snapshot testing
- 📎 Rich text formatting (Markdown/HTML)

## 🤝 Contributing

We welcome contributions! Just follow these steps:

```bash
# 1. Fork the repo
# 2. Create a new branch
git checkout -b feature/my-feature

# 3. Make your changes
git commit -am "Add my feature"

# 4. Push to your fork
git push origin feature/my-feature

# 5. Create a Pull Request 🙌
```

## 📄 License

MIT License © 2025 Prem Kumar

## 🔗 Connect with Me

📧 [erprem013@gmail.com](mailto:erprem013@gmail.com)  
🔗 [LinkedIn](https://www.linkedin.com/in/coderprem/)  
💻 [GitHub](https://github.com/coderprem)
