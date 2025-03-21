# **Habit Breaker App**

## **Idea**
The **Habit Breaker App** is designed to help users track and overcome their bad habits through a simple yet effective tracking system. Users log their progress by recording each instance of resisting or giving in to a bad habit. Over time, they can visualize their progress, build streaks, and gain motivation through XP rewards. The app also lightly supports good habit-building but keeps its focus on stopping negative behaviors.

### **How it Works**
- Users create and track bad habits (e.g., smoking, junk food, procrastination).
- They log every time they **resist** a habit ("Win") or **give in** ("Slip").
- The app tracks **streaks** (days without a slip) and provides XP rewards for wins.
- Simple **progress charts** help users visualize improvement.
- A daily notification reminds users to check in and log their progress.
- A minimalistic **good habit section** allows users to track one or two positive habits alongside breaking bad ones.

The goal is to keep the app **lightweight and easy to use**, avoiding complex gamification elements while still providing motivation to improve.

---
## **Phone Features & Technologies Used**
- **Local Storage:** Uses Room Database for offline tracking.
- **Networking:** Firebase Firestore to sync progress across devices (optional).
- **Push Notifications:** Firebase Cloud Messaging for daily habit reminders.
- **Simple UI:** Designed with Material UI principles in Android Studio.

---
## **Documentation and Tutorials**
Resources that will help with development:
- **Room Database** for local habit tracking: [Android Documentation](https://developer.android.com/training/data-storage/room)
- **Firebase Firestore** for cloud-based habit storage: [Firestore Docs](https://firebase.google.com/docs/firestore)
- **Notifications in Android**: [Firebase Cloud Messaging](https://firebase.google.com/docs/cloud-messaging)
- **Android UI Guidelines**: [Material Design Components](https://material.io/components)

---
## **Mock-Up**
A sample screen will include:
- **Main Screen**: List of bad habits with quick "Win" and "Slip" buttons.
- **Habit Details Page**: Shows streaks, XP progress, and log history.
- **Progress Page**: Simple bar graph displaying Win % vs. Slip %.

Mock-ups will be created using **Android Studio XML layouts** and **Figma (if needed).**

---
## **Potential Problems & Challenges**
- **Time Constraints**: Due to limited semester time, features must be minimal and focused.
- **Storing Data Efficiently**: Balancing Firebase vs. Room Database for tracking.
- **Keeping UI Simple Yet Functional**: Designing a clean but engaging experience.
- **Notifications & Reminders**: Ensuring they are useful without being annoying.

---
## **Conclusion**
The **Habit Breaker App** aims to provide a straightforward way to track and overcome bad habits while lightly encouraging good ones. By using a **simple XP system, streak tracking, and minimal UI**, the app will be easy to use while still being effective. The focus on solo use ensures that development stays manageable within the semester timeframe.

