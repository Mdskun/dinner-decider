<div align="center">

# 🍽️ Dinner Decider

### *Stop overthinking dinner. Start enjoying it.*

<p align="center">
  A modern Android app that helps you instantly decide what to eat using a fun spin wheel and smart reminders.
</p>

<p align="center">
  <img src="https://img.shields.io/github/v/release/mdskun/dinner-decider?style=for-the-badge" />
  <img src="https://img.shields.io/github/license/mdskun/dinner-decider?style=for-the-badge" />
</p>
</div>
---

# 📑 Table of Contents

* [📌 Overview](#-overview)
* [✨ Features](#-features)
* [📸 Screenshots](#-screenshots)
* [⚙️ Configuration](#️-configuration)
* [🛠️ Tech Stack](#️-tech-stack)
* [📋 Requirements](#-requirements)
* [🚀 Installation / Setup](#-installation--setup)
* [⚡ Working](#-working)
* [📂 Project Structure](#-project-structure)
* [📄 Documentation](#-documentation)
* [🤝 Contribution](#-contribution)
* [📜 License](#-license)
* [👨‍💻 Author & Credits](#-author--credits)

---

# 📌 Overview

**Dinner Decider** is a beautifully designed Android app that eliminates the daily struggle of choosing what to eat.

Instead of endless debates, simply spin the wheel 🎡 and let the app decide for you.

---

# ✨ Features

* 🎡 **Spin Wheel Decision Maker**
  Instantly pick a random meal in a fun way

* 📋 **Custom Meal List**
  Add, edit, and manage your favorite dishes

* 🔔 **Smart Notifications**
  Get daily reminders for meal decisions

* 🎨 **Modern UI (Material 3)**
  Clean, smooth, and responsive design

* 💾 **Persistent Storage**
  Your meals are saved automatically

---

# 📸 Screenshots

<p align="center">
  <img src="./screenshots/home.jpg" width="22%" />
  <img src="./screenshots/list.jpg" width="22%" />
  <img src="./screenshots/spin.jpg" width="22%" />
  <img src="./screenshots/settings.jpg" width="22%" />
</p>

---

# ⚙️ Configuration

## 🔔 Notifications Setup

To ensure notifications work correctly:

* Enable notification permissions ✅

### 📱 Device-Specific Settings

| Device Brand   | Required Action              |
| -------------- | ---------------------------- |
| Xiaomi / Redmi | Enable *Autostart*           |
| Samsung        | Disable battery optimization |
| OnePlus / OPPO | Lock app in recent apps      |

---

# 🛠️ Tech Stack

* **Kotlin** → Core language
* **Jetpack Compose** → UI framework
* **Material 3** → Design system
* **AndroidX** → Core libraries

---

# 📋 Requirements

* Android 8.0 (Oreo) or higher
* Android Studio Hedgehog or newer

---

# 🚀 Installation / Setup

## 🔽 Download APK

<p align="center">
  <a href="https://github.com/mdskun/dinner-decider/releases">
    <img src="https://img.shields.io/badge/Download-APK-blue?style=for-the-badge&logo=android" />
  </a>
</p>

---

## 🧑‍💻 Build from Source

```bash
git clone https://github.com/Mdskun/dinner-decider.git
cd dinner-decider
./gradlew assembleDebug
```

---

# ⚡ Working

## 🎯 User Flow

1. Add meals in the **Dinner List**
2. Tap **Spin Wheel** 🎡
3. Get a random meal suggestion
4. Receive daily reminders 🔔

---

## 🔍 Internal Logic

* Meals are stored locally using persistent storage
* Spin wheel uses random selection algorithm
* Notification manager triggers scheduled alerts
* UI is fully reactive using Jetpack Compose

<!-- ---

# 📂 Project Structure

```
dinner-decider/
│
├── app/
│   ├── ui/            # Compose UI components
│   ├── data/          # Local data handling
│   ├── logic/         # Spin + business logic
│   └── notifications/ # Reminder system
│
├── screenshots/       # App images
└── README.md
``` -->

---

# 📄 Documentation

<!-- ## 🏗️ Architecture

* MVVM-inspired structure
* Clear separation of UI, logic, and data
* Reactive UI using Compose -->

## 📝 Changelog

### v1.0.1

* Initial release
* Spin wheel feature
* Meal list management
* Notifications
* Material 3 UI

---

# 🤝 Contribution

Contributions are welcome!

* ⭐ Star the repo
* 🐛 Report issues
* 💡 Suggest features
* 🔧 Submit PRs

---

# 📜 License

This project is licensed under the **MIT License**.
See the `LICENSE` file for details.

---

# 👨‍💻 Author & Credits

**Mdskun**
🔗 [https://github.com/mdskun](https://github.com/mdskun)

---

## ❤️ Special Note

> Built with ❤️ to solve a real everyday problem.

> Dedicated to all moms tired of asking:
> **“What should I cook today?” 🍲**
