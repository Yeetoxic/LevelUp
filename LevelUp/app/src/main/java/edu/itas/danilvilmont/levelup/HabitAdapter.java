package edu.itas.danilvilmont.levelup;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import androidx.recyclerview.widget.RecyclerView;

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

        int level = (habit.xp / 100) + 1;
        int xpPercent = habit.xp % 100;

        holder.levelLabel.setText("Level " + level);
        holder.habitXpBar.setProgress(xpPercent);

        holder.btnWin.setOnClickListener(v -> handleWin(v, habit, position));
        holder.btnSlip.setOnClickListener(v -> handleSlip(v, habit, position));
        holder.btnDelete.setOnClickListener(v -> confirmDelete(v, habit, position));
    }

    private void handleWin(View view, Habit habit, int position) {
        try {
            habit.xp += 10;
            habit.streak += 1;
            habit.lastLogged = System.currentTimeMillis();

            if (habit.xp % 100 == 0) {
                Toast.makeText(view.getContext(), habit.name + " leveled up!", Toast.LENGTH_SHORT).show();
            }

            HabitDatabase.getInstance(view.getContext()).habitDao().update(habit);
            FirebaseHelper.syncHabit(habit);
            notifyItemChanged(position);

        } catch (Exception e) {
            Log.e("HabitAdapter", "Error handling win", e);
            Toast.makeText(view.getContext(), "Failed to log progress", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleSlip(View view, Habit habit, int position) {
        try {
            habit.streak = 0;
            habit.xp = Math.max(0, habit.xp - 5);
            habit.lastLogged = System.currentTimeMillis();

            HabitDatabase.getInstance(view.getContext()).habitDao().update(habit);
            FirebaseHelper.syncHabit(habit);
            notifyItemChanged(position);

        } catch (Exception e) {
            Log.e("HabitAdapter", "Error handling slip", e);
            Toast.makeText(view.getContext(), "Failed to log slip", Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmDelete(View view, Habit habit, int position) {
        new android.app.AlertDialog.Builder(view.getContext())
                .setTitle("Delete Habit")
                .setMessage("Are you sure you want to delete \"" + habit.name + "\"?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    try {
                        HabitDatabase.getInstance(view.getContext()).habitDao().delete(habit);
                        habits.remove(position);
                        notifyItemRemoved(position);

                        FirebaseHelper.markHabitAsDeleted(habit);
                        Toast.makeText(view.getContext(), "Deleted " + habit.name, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.e("HabitAdapter", "Error deleting habit", e);
                        Toast.makeText(view.getContext(), "Failed to delete habit", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public int getItemCount() {
        return habits != null ? habits.size() : 0;
    }

    public static class HabitViewHolder extends RecyclerView.ViewHolder {
        TextView habitName, habitStats, levelLabel;
        ProgressBar habitXpBar;
        Button btnWin, btnSlip, btnDelete;

        public HabitViewHolder(View itemView) {
            super(itemView);
            habitName = itemView.findViewById(R.id.habitName);
            habitStats = itemView.findViewById(R.id.habitStats);
            btnWin = itemView.findViewById(R.id.btnWin);
            btnSlip = itemView.findViewById(R.id.btnSlip);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            habitXpBar = itemView.findViewById(R.id.habitXpBar);
            levelLabel = itemView.findViewById(R.id.levelLabel);
        }
    }
}
