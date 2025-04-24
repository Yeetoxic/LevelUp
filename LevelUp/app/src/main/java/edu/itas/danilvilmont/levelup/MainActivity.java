package edu.itas.danilvilmont.levelup;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView habitList;
    private HabitAdapter adapter;
    private Button addHabitButton, viewStatsButton, refreshButton;
    private HabitDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initDatabase();
        loadLocalHabits();
        syncWithFirebase();

        setupAddHabitButton();
        setupStatsButton();
        setupRefreshButton();

        scheduleDailyNotification();
    }

    private void initViews() {
        habitList = findViewById(R.id.habitList);
        addHabitButton = findViewById(R.id.addHabitButton);
        viewStatsButton = findViewById(R.id.viewStatsButton);
        refreshButton = findViewById(R.id.refreshButton);
    }

    private void initDatabase() {
        db = HabitDatabase.getInstance(this);
    }

    private void loadLocalHabits() {
        List<Habit> habits = db.habitDao().getAllHabits();
        adapter = new HabitAdapter(habits);
        habitList.setAdapter(adapter);
        habitList.setLayoutManager(new LinearLayoutManager(this));
    }

    private void syncWithFirebase() {
        try {
            FirebaseHelper.syncAllHabits(db.habitDao().getAllHabits());
            FirebaseHelper.refreshFromFirebase(this, db, adapter, null);
        } catch (Exception e) {
            Toast.makeText(this, "Sync failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void setupAddHabitButton() {
        addHabitButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("New Habit");

            final EditText input = new EditText(this);
            input.setHint("e.g., Quit Junk Food");
            builder.setView(input);

            builder.setPositiveButton("Add", (dialog, which) -> {
                String habitName = input.getText().toString().trim();
                if (habitName.isEmpty()) {
                    Toast.makeText(this, "Please enter a habit name.", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    Habit newHabit = new Habit(habitName);
                    db.habitDao().insert(newHabit);
                    adapter.setHabits(db.habitDao().getAllHabits());
                    FirebaseHelper.syncHabit(newHabit);
                    Toast.makeText(this, "Habit added: " + habitName, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(this, "Error adding habit.", Toast.LENGTH_SHORT).show();
                }
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            builder.show();
        });
    }

    private void setupStatsButton() {
        viewStatsButton.setOnClickListener(v -> {
            startActivity(new Intent(this, StatsActivity.class));
        });
    }

    private void setupRefreshButton() {
        refreshButton.setOnClickListener(v -> {
            FirebaseHelper.refreshFromFirebase(this, db, adapter, () ->
                    Toast.makeText(this, "Sync complete", Toast.LENGTH_SHORT).show()
            );
        });
    }

    private void scheduleDailyNotification() {
        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, 0, intent, PendingIntent.FLAG_IMMUTABLE
        );

        long triggerTime = System.currentTimeMillis() + 1000 * 10;
        long repeatInterval = AlarmManager.INTERVAL_DAY;

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    repeatInterval,
                    pendingIntent
            );
        } else {
            Toast.makeText(this, "Alarm manager unavailable", Toast.LENGTH_SHORT).show();
        }
    }
}
