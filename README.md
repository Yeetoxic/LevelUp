# **LevelUp – Gamified Habit Breaker**

## **Overview**
**LevelUp** is a lightweight Android app designed to help users **break bad habits** while lightly encouraging good ones. Through a simple **XP and streak tracking system**, users stay motivated without being overwhelmed by features. Every time a user **resists** a bad habit ("Win") or **gives in** ("Slip"), they can log it with one tap — building long-term awareness and change.

---

## Core Features

- ✅ **Create habits** to track things like junk food, procrastination, or smoking.
- 🟢 Tap **Win** when you resist temptation — earn XP and build streaks.
- 🔴 Tap **Slip** when you give in — lose XP and reset streaks.
- 🧠 Each habit earns levels and shows progress with a **clean progress bar**.
- 🔁 **Daily notification reminders** to stay consistent.
- ☁️ **Cloud sync with Firebase Firestore** for multi-device support.
- 🧹 **Deleted habit recovery** dialog from cloud on reinstall or reset.
- ♿ **Material Design 3 UI**, including **dark mode compatibility** and **accessible touch targets**.

---

## 📱 Technologies Used

| Layer             | Tech Used                       |
|------------------|----------------------------------|
| UI               | Material 3 + Android XML         |
| Local Storage    | Room Database                    |
| Cloud Sync       | Firebase Firestore               |
| Notifications    | AlarmManager + BroadcastReceiver |
| Offline Support  | Room-first data architecture     |
| Language         | Java (Android Studio)            |

---

## 📊 Screens / Windows

- **Main Screen** – list of habits with Win, Slip, and Delete buttons
- **Stats Page** – total XP, longest streak, and habit count
- **Recovery Dialog** – restore lost habits from Firebase after reinstall
- **Minimal layout** – modern MaterialCardViews & buttons

---

## 🔄 Firebase Sync

- Syncs habits to the cloud immediately after any change
- Deletions are marked instead of removed (`deleted = true`)
- Habits missing from local but present in Firebase are offered for recovery
- Users can **manually refresh** using a refresh button on the main screen

---

## 🚫 Error Handling

- All Room and Firebase operations are wrapped with `try/catch` blocks
- User-friendly toasts and logs provide feedback if an error occurs
- Firebase writes fail gracefully without crashing the app
- Network and offline scenarios are handled via local fallback

---

## Future Improvements

- Optional **dark mode toggle**
- Graphs for win/slip trends
- Long-press gesture to rename habits
- Separate tab for **positive habit-building**

---

## **Documentation and Tutorials**
Resources that will help with development:
- **Room Database** for local habit tracking: [Android Documentation](https://developer.android.com/training/data-storage/room)
- **Firebase Firestore** for cloud-based habit storage: [Firestore Docs](https://firebase.google.com/docs/firestore)
- **Notifications in Android**: [Firebase Cloud Messaging](https://firebase.google.com/docs/cloud-messaging)
- **Android UI Guidelines**: [Material Design Components](https://material.io/components)

---

## Known Limitations

- No user authentication (shared Firebase for all users)
- Data is device-based unless synced
- No dark mode toggle (relies on system theme)
- No real-time syncing (manual refresh only)

---
## **Conclusion**
The **LevelUp app** aims to provide a straightforward way to track and overcome bad habits while lightly encouraging good ones. By using a **simple XP system, streak tracking, and minimal UI**, the app will be easy to use while still being effective.

