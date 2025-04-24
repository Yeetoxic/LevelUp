package edu.itas.danilvilmont.levelup;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseHelper {

    public static void syncHabit(Habit habit) {
        try {
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();

            Map<String, Object> habitMap = new HashMap<>();
            habitMap.put("name", habit.name);
            habitMap.put("xp", habit.xp);
            habitMap.put("streak", habit.streak);
            habitMap.put("lastLogged", habit.lastLogged);
            habitMap.put("deleted", false);

            firestore.collection("habits")
                    .document(habit.name)
                    .set(habitMap)
                    .addOnSuccessListener(unused ->
                            Log.d("FIREBASE_SYNC", "Synced: " + habit.name))
                    .addOnFailureListener(e ->
                            Log.e("FIREBASE_SYNC", "Failed to sync: " + habit.name, e));
        } catch (Exception e) {
            Log.e("FIREBASE_SYNC", "Unexpected error syncing habit: " + habit.name, e);
        }
    }

    public static void syncAllHabits(List<Habit> habits) {
        try {
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();

            for (Habit habit : habits) {
                Map<String, Object> habitMap = new HashMap<>();
                habitMap.put("name", habit.name);
                habitMap.put("xp", habit.xp);
                habitMap.put("streak", habit.streak);
                habitMap.put("lastLogged", habit.lastLogged);
                habitMap.put("deleted", false);

                firestore.collection("habits")
                        .document(habit.name)
                        .set(habitMap)
                        .addOnSuccessListener(unused ->
                                Log.d("FIREBASE_SYNC", "Synced: " + habit.name))
                        .addOnFailureListener(e ->
                                Log.e("FIREBASE_SYNC", "Failed to sync: " + habit.name, e));
            }
        } catch (Exception e) {
            Log.e("FIREBASE_SYNC", "Unexpected error syncing all habits", e);
        }
    }

    public static void markHabitAsDeleted(Habit habit) {
        try {
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();

            firestore.collection("habits")
                    .document(habit.name)
                    .update("deleted", true)
                    .addOnSuccessListener(unused ->
                            Log.d("FIREBASE_DELETE", "Marked as deleted: " + habit.name))
                    .addOnFailureListener(e ->
                            Log.e("FIREBASE_DELETE", "Failed to mark as deleted: " + habit.name, e));
        } catch (Exception e) {
            Log.e("FIREBASE_DELETE", "Unexpected error marking as deleted", e);
        }
    }

    public static void refreshFromFirebase(Context context, HabitDatabase db, HabitAdapter adapter, Runnable onComplete) {
        try {
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();

            firestore.collection("habits")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        List<Habit> firebaseHabitsToRestore = new ArrayList<>();

                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            try {
                                String name = doc.getString("name");
                                if (name == null || name.isEmpty()) continue;

                                long xp = doc.getLong("xp") != null ? doc.getLong("xp") : 0;
                                long streak = doc.getLong("streak") != null ? doc.getLong("streak") : 0;
                                long lastLogged = doc.getLong("lastLogged") != null ? doc.getLong("lastLogged") : 0;
                                boolean deleted = doc.getBoolean("deleted") != null && doc.getBoolean("deleted");

                                Habit h = new Habit(name);
                                h.xp = (int) xp;
                                h.streak = (int) streak;
                                h.lastLogged = lastLogged;

                                if (!localHabitExists(h.name, db)) {
                                    if (!deleted) {
                                        firebaseHabitsToRestore.add(h);
                                        syncHabit(h); // reset deleted flag
                                    }
                                }
                            } catch (Exception e) {
                                Log.w("FIREBASE_PARSE", "Skipped malformed doc", e);
                            }
                        }

                        if (!firebaseHabitsToRestore.isEmpty()) {
                            showRestoreDialog(context, firebaseHabitsToRestore, db, adapter);
                        } else {
                            Toast.makeText(context, "No missing habits found.", Toast.LENGTH_SHORT).show();
                        }

                        if (onComplete != null) onComplete.run();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("FIREBASE_SYNC", "Failed to fetch habits from Firebase", e);
                        Toast.makeText(context, "Failed to refresh from Firebase.", Toast.LENGTH_SHORT).show();
                        if (onComplete != null) onComplete.run(); // still call
                    });
        } catch (Exception e) {
            Log.e("FIREBASE_SYNC", "Unexpected error in refreshFromFirebase()", e);
            Toast.makeText(context, "Unexpected sync error.", Toast.LENGTH_SHORT).show();
            if (onComplete != null) onComplete.run(); // always call
        }
    }

    private static boolean localHabitExists(String name, HabitDatabase db) {
        for (Habit h : db.habitDao().getAllHabits()) {
            if (h.name.equals(name)) return true;
        }
        return false;
    }

    private static void showRestoreDialog(Context context, List<Habit> firebaseHabits, HabitDatabase db, HabitAdapter adapter) {
        String[] names = new String[firebaseHabits.size()];
        boolean[] checked = new boolean[firebaseHabits.size()];

        for (int i = 0; i < firebaseHabits.size(); i++) {
            names[i] = firebaseHabits.get(i).name;
            checked[i] = true;
        }

        new AlertDialog.Builder(context)
                .setTitle("Restore Deleted Habits")
                .setMultiChoiceItems(names, checked, (dialog, which, isChecked) -> checked[which] = isChecked)
                .setPositiveButton("Restore", (dialog, which) -> {
                    for (int i = 0; i < checked.length; i++) {
                        if (checked[i]) {
                            Habit h = firebaseHabits.get(i);
                            try {
                                db.habitDao().insert(h);
                                syncHabit(h);
                            } catch (Exception e) {
                                Log.e("FIREBASE_RESTORE", "Failed to restore habit: " + h.name, e);
                            }
                        }
                    }
                    adapter.setHabits(db.habitDao().getAllHabits());
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
