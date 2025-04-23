package edu.itas.danilvilmont.levelup;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.Button;

import java.util.List;

public class HabitAdapter extends RecyclerView.Adapter<HabitAdapter.HabitViewHolder> {
    private List<Habit> habits;

    public HabitAdapter(List<Habit> habits) {
        this.habits = habits;
    }

    public void setHabits(List<Habit> habits) {
        this.habits = habits;
        notifyDataSetChanged();
    }

    @Override
    public HabitViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.habit_item, parent, false);
        return new HabitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HabitViewHolder holder, int position) {
        Habit habit = habits.get(position);
        holder.habitName.setText(habit.name);
        holder.habitStats.setText("XP: " + habit.xp + " | Streak: " + habit.streak);

        // Set progress bar based on XP (max = 100 for now)
        int level = (habit.xp / 100) + 1;
        int xpPercent = habit.xp % 100;

        holder.levelLabel.setText("Level " + level);
        holder.habitXpBar.setProgress(xpPercent);

        holder.btnWin.setOnClickListener(v -> {
            habit.xp += 10;           // earn XP
            habit.streak += 1;        // continue streak
            habit.lastLogged = System.currentTimeMillis();

            // sync
            FirebaseHelper.syncHabit(habit);


            if (habit.xp % 100 == 0 && habit.xp > 0) {
                Toast.makeText(v.getContext(), habit.name + " leveled up!", Toast.LENGTH_SHORT).show();
            }

            HabitDatabase.getInstance(v.getContext()).habitDao().update(habit);
            notifyItemChanged(position);
        });

        holder.btnSlip.setOnClickListener(v -> {
            habit.streak = 0;         // reset streak
            habit.xp = Math.max(0, habit.xp - 5); // lose XP but not below 0
            habit.lastLogged = System.currentTimeMillis();

            // sync
            FirebaseHelper.syncHabit(habit);


            HabitDatabase.getInstance(v.getContext()).habitDao().update(habit);
            notifyItemChanged(position);
        });

        holder.btnDelete.setOnClickListener(v -> {
            new android.app.AlertDialog.Builder(v.getContext())
                    .setTitle("Delete Habit")
                    .setMessage("Are you sure you want to delete \"" + habit.name + "\"?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        HabitDatabase.getInstance(v.getContext()).habitDao().delete(habit);
                        habits.remove(position);
                        notifyItemRemoved(position);

                        // sync
                        FirebaseHelper.markHabitAsDeleted(habit);

                        Toast.makeText(v.getContext(), "Deleted " + habit.name, Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return habits != null ? habits.size() : 0;
    }

    public static class HabitViewHolder extends RecyclerView.ViewHolder {
        TextView habitName, habitStats;
        Button btnWin, btnSlip;
        ProgressBar habitXpBar;
        TextView levelLabel;
        Button btnDelete;


        public HabitViewHolder(View itemView) {
            super(itemView);
            habitName = itemView.findViewById(R.id.habitName);
            habitStats = itemView.findViewById(R.id.habitStats);
            btnWin = itemView.findViewById(R.id.btnWin);
            btnSlip = itemView.findViewById(R.id.btnSlip);
            habitXpBar = itemView.findViewById(R.id.habitXpBar);
            levelLabel = itemView.findViewById(R.id.levelLabel);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
