package edu.itas.danilvilmont.levelup;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;

import java.util.List;

public class StatsActivity extends AppCompatActivity {

    private TextView statTotalHabits, statTotalXP, statLongestStreak;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        statTotalHabits = findViewById(R.id.statTotalHabits);
        statTotalXP = findViewById(R.id.statTotalXP);
        statLongestStreak = findViewById(R.id.statLongestStreak);

        HabitDatabase db = HabitDatabase.getInstance(this);
        List<Habit> allHabits = db.habitDao().getAllHabits();

        int totalXP = 0;
        int longestStreak = 0;

        for (Habit h : allHabits) {
            totalXP += h.xp;
            if (h.streak > longestStreak) longestStreak = h.streak;
        }

        statTotalHabits.setText("Total Habits: " + allHabits.size());
        statTotalXP.setText("Total XP: " + totalXP);
        statLongestStreak.setText("Longest Streak: " + longestStreak);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // closes the StatsActivity
        return true;
    }
}
