package edu.itas.danilvilmont.levelup;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
    private Button addHabitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        habitList = findViewById(R.id.habitList);
        addHabitButton = findViewById(R.id.addHabitButton);

        HabitDatabase db = HabitDatabase.getInstance(this);
        List<Habit> allHabits = db.habitDao().getAllHabits();

        adapter = new HabitAdapter(allHabits);
        habitList.setAdapter(adapter);
        habitList.setLayoutManager(new LinearLayoutManager(this));

        // Sync Local to firebase
        FirebaseHelper.syncAllHabits(allHabits);

        // Sync firebase to Local
        FirebaseHelper.refreshFromFirebase(this, db, adapter, () -> {
            // Optional: anything else to do after refresh
        });


        addHabitButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("New Habit");

            final EditText input = new EditText(this);
            input.setHint("e.g., Quit Junk Food");
            builder.setView(input);

            builder.setPositiveButton("Add", (dialog, which) -> {
                String habitName = input.getText().toString().trim();
                if (!habitName.isEmpty()) {
                    Habit newHabit = new Habit(habitName);
                    db.habitDao().insert(newHabit);
                    adapter.setHabits(db.habitDao().getAllHabits());
                    FirebaseHelper.syncHabit(newHabit); // Now calling from the helper
                    Toast.makeText(this, "Habit added: " + habitName, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Please enter a name.", Toast.LENGTH_SHORT).show();
                }
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            builder.show();
        });

        Button viewStatsButton = findViewById(R.id.viewStatsButton);
        viewStatsButton.setOnClickListener(v -> {
            startActivity(new Intent(this, StatsActivity.class));
        });

        Button refreshButton = findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(v -> {
            FirebaseHelper.refreshFromFirebase(this, db, adapter, () -> {
                // Optional: anything else to do after refresh
            });
        });

        scheduleDailyNotification();
    }

    private void scheduleDailyNotification() {
        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, 0, intent, PendingIntent.FLAG_IMMUTABLE
        );

        long triggerTime = System.currentTimeMillis() + 1000 * 10;
        long repeatInterval = AlarmManager.INTERVAL_DAY;

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                repeatInterval,
                pendingIntent
        );
    }
}
